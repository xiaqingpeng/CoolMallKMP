package com.joker.coolmallkmp.core.order

import com.joker.coolmallkmp.core.model.entity.Cart
import com.joker.coolmallkmp.core.model.entity.SelectedGoods
import com.joker.coolmallkmp.core.model.entity.toSelectedGoodsList

/**
 * 结算草稿：替代 Android MMKV 的 selectedGoodsList / carts 缓存。
 * 进入订单确认页时读取并清空，避免重复下单。
 */
object CheckoutDraft {
    private var cartsSnapshot: List<Cart> = emptyList()
    private var goodsSnapshot: List<SelectedGoods> = emptyList()
    private var fromCart: Boolean = false

    fun prepareFromCart(carts: List<Cart>) {
        cartsSnapshot = carts.map { it.copy(spec = it.spec.toList()) }
        goodsSnapshot = carts.toSelectedGoodsList()
        fromCart = true
    }

    fun prepareFromGoods(goods: List<SelectedGoods>) {
        cartsSnapshot = emptyList()
        goodsSnapshot = goods
        fromCart = false
    }

    fun consume(): Snapshot {
        val snapshot = Snapshot(
            carts = cartsSnapshot,
            goods = goodsSnapshot,
            fromCart = fromCart,
        )
        clear()
        return snapshot
    }

    fun clear() {
        cartsSnapshot = emptyList()
        goodsSnapshot = emptyList()
        fromCart = false
    }

    data class Snapshot(
        val carts: List<Cart>,
        val goods: List<SelectedGoods>,
        val fromCart: Boolean,
    ) {
        val displayCarts: List<Cart>
            get() = if (carts.isNotEmpty()) {
                carts
            } else {
                // 无 carts 快照时由 goods 反构展示
                goods.groupBy { it.goodsId }.map { (goodsId, items) ->
                    val first = items.first()
                    Cart(
                        goodsId = goodsId,
                        goodsName = first.goodsInfo?.title.orEmpty(),
                        goodsMainPic = first.goodsInfo?.mainPic.orEmpty(),
                        spec = items.mapNotNull { item ->
                            val spec = item.spec ?: return@mapNotNull null
                            com.joker.coolmallkmp.core.model.entity.CartGoodsSpec(
                                id = spec.id,
                                goodsId = spec.goodsId,
                                name = spec.name,
                                price = spec.price,
                                stock = spec.stock,
                                count = item.count,
                                images = spec.images,
                            )
                        },
                    )
                }
            }

        val totalAmount: Int
            get() = displayCarts.sumOf { it.totalAmount }

        val totalCount: Int
            get() = displayCarts.sumOf { it.totalCount }

        val isEmpty: Boolean
            get() = goods.isEmpty() && carts.isEmpty()
    }
}
