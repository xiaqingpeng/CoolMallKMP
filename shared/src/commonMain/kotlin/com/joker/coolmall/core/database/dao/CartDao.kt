package com.joker.coolmallkmp.core.database.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update
import com.joker.coolmallkmp.core.database.entity.CartEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCart(cart: CartEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCarts(carts: List<CartEntity>)

    @Update
    suspend fun updateCart(cart: CartEntity)

    @Delete
    suspend fun deleteCart(cart: CartEntity)

    @Query("DELETE FROM carts WHERE goodsId = :goodsId")
    suspend fun deleteCartByGoodsId(goodsId: Long)

    @Query("DELETE FROM carts")
    suspend fun clearCart()

    @Query("SELECT * FROM carts")
    fun getAllCarts(): Flow<List<CartEntity>>

    @Query("SELECT * FROM carts WHERE goodsId = :goodsId")
    suspend fun getCartByGoodsId(goodsId: Long): CartEntity?

    @Query("SELECT COUNT(*) FROM carts")
    fun getCartCount(): Flow<Int>
}
