package com.joker.coolmall.core.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.joker.coolmall.core.database.entity.FootprintEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FootprintDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFootprint(footprint: FootprintEntity)

    @Query("DELETE FROM footprints WHERE goodsId = :goodsId")
    suspend fun deleteFootprintByGoodsId(goodsId: Long)

    @Query("DELETE FROM footprints")
    suspend fun clearAllFootprints()

    @Query("SELECT * FROM footprints ORDER BY viewTime DESC")
    fun getAllFootprints(): Flow<List<FootprintEntity>>

    @Query("SELECT COUNT(*) FROM footprints")
    fun getFootprintCount(): Flow<Int>

    @Query("SELECT * FROM footprints ORDER BY viewTime DESC LIMIT :limit")
    fun getRecentFootprints(limit: Int): Flow<List<FootprintEntity>>

    @Query("SELECT * FROM footprints WHERE goodsId = :goodsId")
    suspend fun getFootprintByGoodsId(goodsId: Long): FootprintEntity?
}
