package com.joker.coolmall.core.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSItemProvider
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSUUID
import platform.Foundation.writeToFile
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UniformTypeIdentifiers.UTTypeImage
import platform.darwin.NSObject

@Composable
actual fun rememberPlatformImagePicker(
    maxCount: Int,
    onResult: (List<PickedImage>) -> Unit,
): () -> Unit {
    val latestOnResult = rememberUpdatedState(onResult)
    val latestMax = rememberUpdatedState(maxCount.coerceAtLeast(1))
    return remember {
        {
            presentPhPicker(
                maxCount = latestMax.value,
                onResult = { latestOnResult.value(it) },
            )
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun presentPhPicker(
    maxCount: Int,
    onResult: (List<PickedImage>) -> Unit,
) {
    val root = topViewController()
    if (root == null) {
        onResult(emptyList())
        return
    }
    val config = PHPickerConfiguration()
    config.selectionLimit = maxCount.toLong()
    config.filter = PHPickerFilter.imagesFilter

    val picker = PHPickerViewController(configuration = config)
    val delegate = PhPickerDelegate { images ->
        onResult(images)
        // 回调结束后释放持有
        pickerDelegates.remove(picker.hashCode())
    }
    picker.delegate = delegate
    pickerDelegates[picker.hashCode()] = delegate
    root.presentViewController(picker, animated = true, completion = null)
}

private class PhPickerDelegate(
    private val onDone: (List<PickedImage>) -> Unit,
) : NSObject(), PHPickerViewControllerDelegateProtocol {
    @OptIn(ExperimentalForeignApi::class)
    override fun picker(
        picker: PHPickerViewController,
        didFinishPicking: List<*>,
    ) {
        picker.dismissViewControllerAnimated(true, completion = null)
        val results = didFinishPicking.filterIsInstance<PHPickerResult>()
        if (results.isEmpty()) {
            onDone(emptyList())
            return
        }
        val images = mutableListOf<PickedImage>()
        var remaining = results.size
        results.forEach { result ->
            loadPickedImage(result.itemProvider) { image ->
                if (image != null) images += image
                remaining -= 1
                if (remaining <= 0) {
                    onDone(images.toList())
                }
            }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun loadPickedImage(
    provider: NSItemProvider,
    onDone: (PickedImage?) -> Unit,
) {
    if (!provider.hasItemConformingToTypeIdentifier(UTTypeImage.identifier)) {
        onDone(null)
        return
    }
    provider.loadDataRepresentationForTypeIdentifier(UTTypeImage.identifier) { data, _ ->
        if (data == null) {
            onDone(null)
            return@loadDataRepresentationForTypeIdentifier
        }
        val fileName = "pick_${NSUUID().UUIDString}.jpg"
        val path = NSTemporaryDirectory() + fileName
        val ok = data.writeToFile(path, atomically = true)
        onDone(
            if (ok) {
                PickedImage(
                    uri = path,
                    fileName = fileName,
                    mimeType = "image/jpeg",
                )
            } else {
                null
            }
        )
    }
}

private fun topViewController(): UIViewController? {
    val app = UIApplication.sharedApplication
    val keyWindow = app.windows
        .mapNotNull { it as? UIWindow }
        .firstOrNull { it.isKeyWindow() }
    var current: UIViewController = keyWindow?.rootViewController ?: return null
    while (true) {
        val presented = current.presentedViewController ?: break
        current = presented
    }
    return current
}

/** 强引用 delegate，避免 picker 展示期间被 ARC 回收 */
private val pickerDelegates = mutableMapOf<Int, Any>()
