package com.joker.coolmall.core.data.repository

import com.joker.coolmall.core.database.dao.CartDao
import com.joker.coolmall.core.database.entity.CartEntity
import com.joker.coolmall.core.database.util.CartSpecJson
import com.joker.coolmall.core.model.entity.Cart
import com.joker.coolmall.core.model.entity.CartGoodsSpec
import com.joker.coolmall.core.model.entity.Goods
import com.joker.coolmall.core.model.entity.GoodsSpec
import com.joker.coolmall.core.model.entity.toCartGoodsSpec
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Room 持久化购物车（API 与原先进程内实现对齐）。
 */
class CartRepository(
    private val cartDao: CartDao,
) {
    private val mutex = Mutex()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val cartItems: StateFlow<List<Cart>> = cartDao.getAllCarts()
        .map { entities -> entities.map { it.toCart() } }
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList(),
        )

    val itemCount: Int
        get() = cartItems.value.sumOf { it.totalCount }

    fun observeCartCount(): Flow<Int> = cartDao.getCartCount()

    suspend fun addGoods(
        goods: Goods,
        spec: GoodsSpec? = null,
        count: Int = 1,
    ) {
        if (goods.id <= 0 || count <= 0) return
        val cartSpec = (spec ?: defaultSpec(goods)).toCartGoodsSpec(count)
        addOrMerge(
            goodsId = goods.id,
            goodsName = goods.title,
            goodsMainPic = goods.mainPic,
            cartSpec = cartSpec,
        )
    }

    suspend fun addOrMerge(
        goodsId: Long,
        goodsName: String,
        goodsMainPic: String,
        cartSpec: CartGoodsSpec,
    ) = mutex.withLock {
        if (goodsId <= 0 || cartSpec.count <= 0) return@withLock
        val existing = cartDao.getCartByGoodsId(goodsId)
        if (existing == null) {
            cartDao.insertCart(
                CartEntity(
                    goodsId = goodsId,
                    goodsName = goodsName,
                    goodsMainPic = goodsMainPic,
                    specJson = CartSpecJson.encode(listOf(cartSpec)),
                )
            )
        } else {
            val specs = CartSpecJson.decode(existing.specJson)
            val specIndex = specs.indexOfFirst { it.id == cartSpec.id }
            val newSpecs = if (specIndex < 0) {
                specs + cartSpec
            } else {
                specs.toMutableList().also { list ->
                    val old = list[specIndex]
                    list[specIndex] = old.copy(count = old.count + cartSpec.count)
                }
            }
            cartDao.updateCart(
                existing.copy(
                    goodsName = goodsName.ifBlank { existing.goodsName },
                    goodsMainPic = goodsMainPic.ifBlank { existing.goodsMainPic },
                    specJson = CartSpecJson.encode(newSpecs),
                )
            )
        }
    }

    suspend fun updateSpecCount(goodsId: Long, specId: Long, count: Int) = mutex.withLock {
        val existing = cartDao.getCartByGoodsId(goodsId) ?: return@withLock
        val newSpecs = CartSpecJson.decode(existing.specJson).mapNotNull { spec ->
            if (spec.id != specId) spec
            else if (count <= 0) null
            else spec.copy(count = count)
        }
        if (newSpecs.isEmpty()) {
            cartDao.deleteCartByGoodsId(goodsId)
        } else {
            cartDao.updateCart(existing.copy(specJson = CartSpecJson.encode(newSpecs)))
        }
    }

    suspend fun removeSpec(goodsId: Long, specId: Long) = mutex.withLock {
        val existing = cartDao.getCartByGoodsId(goodsId) ?: return@withLock
        val newSpecs = CartSpecJson.decode(existing.specJson).filterNot { it.id == specId }
        if (newSpecs.isEmpty()) {
            cartDao.deleteCartByGoodsId(goodsId)
        } else {
            cartDao.updateCart(existing.copy(specJson = CartSpecJson.encode(newSpecs)))
        }
    }

    suspend fun removeGoods(goodsId: Long) = mutex.withLock {
        cartDao.deleteCartByGoodsId(goodsId)
    }

    suspend fun clear() = mutex.withLock {
        cartDao.clearCart()
    }

    private fun defaultSpec(goods: Goods): GoodsSpec {
        val fromGoods = goods.specs?.firstOrNull()
        if (fromGoods != null) return fromGoods
        return GoodsSpec(
            id = goods.id,
            goodsId = goods.id,
            name = "默认规格",
            price = goods.price,
            stock = 999,
            images = listOf(goods.mainPic).filter { it.isNotBlank() }.ifEmpty { null },
        )
    }

    private fun CartEntity.toCart(): Cart = Cart(
        goodsId = goodsId,
        goodsName = goodsName,
        goodsMainPic = goodsMainPic,
        spec = CartSpecJson.decode(specJson),
    )
}
