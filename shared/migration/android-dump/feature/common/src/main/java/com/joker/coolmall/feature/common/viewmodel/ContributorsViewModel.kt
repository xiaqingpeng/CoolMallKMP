package com.joker.coolmallkmp.feature.common.viewmodel

import com.joker.coolmallkmp.core.common.base.viewmodel.BaseNetWorkListViewModel
import com.joker.coolmallkmp.core.data.repository.UserContributorRepository
import com.joker.coolmallkmp.core.model.entity.UserContributor
import com.joker.coolmallkmp.core.model.request.PageRequest
import com.joker.coolmallkmp.core.model.response.NetworkPageData
import com.joker.coolmallkmp.core.model.response.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 贡献者列表 ViewModel
 *
 * @author Joker.X
 */
@HiltViewModel
class ContributorsViewModel @Inject constructor(
    private val userContributorRepository: UserContributorRepository,
) : BaseNetWorkListViewModel<UserContributor>() {

    init {
        initLoad()
    }

    /**
     * 通过重写来给父类提供API请求的Flow
     *
     * @return 用户贡献者分页数据的Flow
     * @author Joker.X
     */
    override fun requestListData(): Flow<NetworkResponse<NetworkPageData<UserContributor>>> {
        return userContributorRepository.getUserContributorPage(
            PageRequest(
                page = super.currentPage,
                size = super.pageSize
            )
        )
    }

}
