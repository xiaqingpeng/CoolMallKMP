package com.joker.coolmall.core.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.joker.coolmall.core.config.AppConfig
import com.joker.coolmall.core.data.local.AuthPreferences
import com.joker.coolmall.core.data.local.GuidePreferences
import com.joker.coolmall.core.data.local.SearchHistoryPreferences
import com.joker.coolmall.core.data.local.ThemePreferences
import com.joker.coolmall.core.data.local.UserPreferences
import com.joker.coolmall.core.data.local.createDataStore
import com.joker.coolmall.core.data.repository.AddressRepository
import com.joker.coolmall.core.data.repository.AuthRepository
import com.joker.coolmall.core.data.repository.CartRepository
import com.joker.coolmall.core.data.repository.CommonRepository
import com.joker.coolmall.core.data.repository.CouponRepository
import com.joker.coolmall.core.data.repository.CustomerServiceRepository
import com.joker.coolmall.core.data.repository.FeedbackRepository
import com.joker.coolmall.core.data.repository.FileUploadRepository
import com.joker.coolmall.core.data.repository.FootprintRepository
import com.joker.coolmall.core.data.repository.GoodsRepository
import com.joker.coolmall.core.data.repository.OrderRepository
import com.joker.coolmall.core.data.repository.PageRepository
import com.joker.coolmall.core.data.repository.UserRepository
import com.joker.coolmall.core.database.createAppDatabase
import com.joker.coolmall.core.network.KtorAddressNetworkDataSource
import com.joker.coolmall.core.network.KtorAuthNetworkDataSource
import com.joker.coolmall.core.network.KtorCommonNetworkDataSource
import com.joker.coolmall.core.network.KtorCouponNetworkDataSource
import com.joker.coolmall.core.network.KtorCustomerServiceNetworkDataSource
import com.joker.coolmall.core.network.KtorFeedbackNetworkDataSource
import com.joker.coolmall.core.network.KtorGoodsNetworkDataSource
import com.joker.coolmall.core.network.KtorOrderNetworkDataSource
import com.joker.coolmall.core.network.KtorPageNetworkDataSource
import com.joker.coolmall.core.network.KtorUserNetworkDataSource
import com.joker.coolmall.core.network.Network
import com.joker.coolmall.core.platform.appVersionCode
import com.joker.coolmall.core.platform.appVersionName

/**
 * KMP 共享依赖容器。
 * Compose Android 工程使用 Hilt；KMP 不能依赖 Hilt，改为手动单例容器。
 */
class AppContainer(
    dataStore: DataStore<Preferences>,
    context: Any? = null,
) {
    val dataStore: DataStore<Preferences> = dataStore

    private val database = createAppDatabase(context)
    private val goodsNetworkDataSource = KtorGoodsNetworkDataSource(Network.httpClient)
    private val pageNetworkDataSource = KtorPageNetworkDataSource(Network.httpClient)
    private val userNetworkDataSource = KtorUserNetworkDataSource(Network.httpClient)
    private val addressNetworkDataSource = KtorAddressNetworkDataSource(Network.httpClient)
    private val orderNetworkDataSource = KtorOrderNetworkDataSource(Network.httpClient)
    private val couponNetworkDataSource = KtorCouponNetworkDataSource(Network.httpClient)
    private val feedbackNetworkDataSource = KtorFeedbackNetworkDataSource(Network.httpClient)
    private val customerServiceNetworkDataSource =
        KtorCustomerServiceNetworkDataSource(Network.httpClient)

    val authPreferences: AuthPreferences = AuthPreferences(dataStore)
    val userPreferences: UserPreferences = UserPreferences(dataStore)

    val userRepository: UserRepository = UserRepository(
        dataSource = userNetworkDataSource,
        userPreferences = userPreferences,
    )

    val authRepository: AuthRepository = AuthRepository(
        dataSource = KtorAuthNetworkDataSource(Network.httpClient),
        authPreferences = authPreferences,
        userRepository = userRepository,
    )

    val pageRepository: PageRepository = PageRepository(
        dataSource = pageNetworkDataSource,
    )

    val goodsRepository: GoodsRepository = GoodsRepository(
        dataSource = goodsNetworkDataSource,
    )

    val addressRepository: AddressRepository = AddressRepository(
        dataSource = addressNetworkDataSource,
    )

    val orderRepository: OrderRepository = OrderRepository(
        dataSource = orderNetworkDataSource,
    )
    val commonRepository: CommonRepository = CommonRepository(
        dataSource = KtorCommonNetworkDataSource(Network.httpClient),
    )

    val couponRepository: CouponRepository = CouponRepository(
        dataSource = couponNetworkDataSource,
    )

    val fileUploadRepository: FileUploadRepository = FileUploadRepository(
        commonRepository = commonRepository,
    )

    val cartRepository: CartRepository = CartRepository(
        cartDao = database.cartDao(),
    )

    val footprintRepository: FootprintRepository = FootprintRepository(
        footprintDao = database.footprintDao(),
    )

    val feedbackRepository: FeedbackRepository = FeedbackRepository(
        dataSource = feedbackNetworkDataSource,
    )

    val customerServiceRepository: CustomerServiceRepository = CustomerServiceRepository(
        dataSource = customerServiceNetworkDataSource,
    )

    val guidePreferences: GuidePreferences = GuidePreferences(dataStore)

    val themePreferences: ThemePreferences = ThemePreferences(dataStore)

    val searchHistoryPreferences: SearchHistoryPreferences = SearchHistoryPreferences(dataStore)
}

object AppContainerProvider {
    private var appContainer: AppContainer? = null

    var appContext: Any? = null
        private set

    var versionName: String = ""
        private set

    var versionCode: String = ""
        private set

    val instance: AppContainer
        get() = checkNotNull(appContainer) {
            "AppContainerProvider must be initialized before accessing instance"
        }

    val isInitialized: Boolean
        get() = appContainer != null

    fun initialize(
        context: Any? = null,
        baseUrl: String = AppConfig.baseUrl,
        isDebug: Boolean = AppConfig.isDebug,
    ) {
        if (appContainer != null) return
        appContext = context
        AppConfig.configure(baseUrl = baseUrl, isDebug = isDebug)
        versionName = appVersionName(context)
        versionCode = appVersionCode(context)
        appContainer = AppContainer(
            dataStore = createDataStore(context),
            context = context,
        )
    }
}
