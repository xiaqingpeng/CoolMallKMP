package com.joker.coolmallkmp.core.data.repository

import com.joker.coolmallkmp.core.database.dao.FootprintDao
import com.joker.coolmallkmp.core.database.entity.FootprintEntity
import com.joker.coolmallkmp.core.model.entity.Footprint
import com.joker.coolmallkmp.core.model.entity.Goods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * 本地足迹仓库（Room）
 */
class FootprintRepository(
    private val footprintDao: FootprintDao,
) {
    fun getAllFootprints(): Flow<List<Footprint>> =
        footprintDao.getAllFootprints()
            .map { list -> list.map { it.toModel() } }
            .flowOn(Dispatchers.IO)

    fun getFootprintCount(): Flow<Int> =
        footprintDao.getFootprintCount().flowOn(Dispatchers.IO)

    fun getRecentFootprints(limit: Int): Flow<List<Footprint>> =
        footprintDao.getRecentFootprints(limit)
            .map { list -> list.map { it.toModel() } }
            .flowOn(Dispatchers.IO)

    suspend fun addFootprint(footprint: Footprint) = withContext(Dispatchers.IO) {
        if (footprint.goodsId <= 0) return@withContext
        footprintDao.insertFootprint(footprint.toEntity())
    }

    @OptIn(ExperimentalTime::class)
    suspend fun recordGoodsView(goods: Goods) = withContext(Dispatchers.IO) {
        if (goods.id <= 0) return@withContext
        footprintDao.insertFootprint(
            FootprintEntity(
                goodsId = goods.id,
                goodsName = goods.title,
                goodsSubTitle = goods.subTitle,
                goodsMainPic = goods.mainPic,
                goodsPrice = goods.price,
                goodsSold = goods.sold,
                viewTime = Clock.System.now().toEpochMilliseconds(),
            )
        )
    }

    suspend fun removeFootprint(goodsId: Long) = withContext(Dispatchers.IO) {
        footprintDao.deleteFootprintByGoodsId(goodsId)
    }

    suspend fun clearAllFootprints() = withContext(Dispatchers.IO) {
        footprintDao.clearAllFootprints()
    }

    private fun FootprintEntity.toModel(): Footprint = Footprint(
        goodsId = goodsId,
        goodsName = goodsName,
        goodsSubTitle = goodsSubTitle,
        goodsMainPic = goodsMainPic,
        goodsPrice = goodsPrice,
        goodsSold = goodsSold,
        viewTime = viewTime,
    )

    private fun Footprint.toEntity(): FootprintEntity = FootprintEntity(
        goodsId = goodsId,
        goodsName = goodsName,
        goodsSubTitle = goodsSubTitle,
        goodsMainPic = goodsMainPic,
        goodsPrice = goodsPrice,
        goodsSold = goodsSold,
        viewTime = viewTime,
    )
}
