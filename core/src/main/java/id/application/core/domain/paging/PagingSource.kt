package id.application.core.domain.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import id.application.core.data.local.database.ApplicationDatabase
import id.application.core.data.local.mediator.GeotagingRemoteMediator
import id.application.core.data.network.model.geotags.AllGeotaging
import id.application.core.data.network.service.ApplicationService
import id.application.core.domain.model.blocks.ItemAllBlocks
import id.application.core.domain.model.geotags.ItemAllGeotaging
import id.application.core.domain.repository.ApplicationRepository
import kotlinx.coroutines.flow.Flow

class GeotagingPagingSource(
    private val repository : ApplicationRepository
) :PagingSource<Int, ItemAllGeotaging>(){

    override fun getRefreshKey(state: PagingState<Int, ItemAllGeotaging>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ItemAllGeotaging> {
        try {
            val currentPage = params.key ?: 1
            val response = repository.getAllGeotaging(
                block = null,
                createdBy = "user",
                limitItem = params.loadSize,
                pageItem = currentPage
            )
            if (response.code == 200) {
                val storeResponse = response.data
                storeResponse.let {
                    val store = it.items
                    val prevKey = if (currentPage == 1) null else currentPage - 1
                    val nextKey = if (currentPage == it.totalPages) null else currentPage + 1
                    return LoadResult.Page(store, prevKey, nextKey)
                }
            }
        } catch (e : Exception){
            return LoadResult.Error(e)
        }
        return LoadResult.Error(Exception("Unknown error"))
    }
}


class GeotagingAllPagingSource(
    private val repository : ApplicationRepository
) :PagingSource<Int, ItemAllGeotaging>(){

    override fun getRefreshKey(state: PagingState<Int, ItemAllGeotaging>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ItemAllGeotaging> {
        try {
            val currentPage = params.key ?: 1
            val response = repository.getAllGeotaging(
                block = null,
                createdBy = "all",
                limitItem = params.loadSize,
                pageItem = currentPage
            )
            if (response.code == 200) {
                val storeResponse = response.data
                storeResponse.let {
                    val store = it.items
                    val prevKey = if (currentPage == 1) null else currentPage - 1
                    val nextKey = if (currentPage == it.totalPages) null else currentPage + 1
                    return LoadResult.Page(store, prevKey, nextKey)
                }
            }
        } catch (e : Exception){
            return LoadResult.Error(e)
        }
        return LoadResult.Error(Exception("Unknown error"))
    }
}

class BlocksPagingSource(
    private val repository : ApplicationRepository
) : PagingSource<Int, ItemAllBlocks>() {

    override fun getRefreshKey(state: PagingState<Int, ItemAllBlocks>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ItemAllBlocks> {
        try {
            val currentPage = params.key ?: 1
            val response = repository.getAllBlocks(
                limitItem = params.loadSize,
                pageItem = currentPage
            )
            if (response.code == 200) {
                val storeResponse = response.data
                storeResponse.let {
                    val store = it.items
                    val prevKey = if (currentPage == 1) null else currentPage - 1
                    val nextKey = if (currentPage == it.totalPages) null else currentPage + 1
                    return LoadResult.Page(store, prevKey, nextKey)
                }
            }
        } catch (e : Exception){
            return LoadResult.Error(e)
        }
        return LoadResult.Error(Exception("Unknown error"))
    }
}

class GeotagingPagingMediator(
    private val api : ApplicationService,
    private val database : ApplicationDatabase)
{
    @OptIn(ExperimentalPagingApi::class)
    fun fetchGeotags() : Flow<PagingData<AllGeotaging>> = Pager(
        config = PagingConfig(
            enablePlaceholders = false,
            pageSize = 10,
            initialLoadSize = 10,
            prefetchDistance = 1
        ),
        remoteMediator = GeotagingRemoteMediator(api, database),
        pagingSourceFactory = {
            database.geotagsDao().retrieveAllGeotags()
        }
    ).flow
}