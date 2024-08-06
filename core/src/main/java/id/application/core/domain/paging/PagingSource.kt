package id.application.core.domain.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import id.application.core.data.local.database.ApplicationDatabase
import id.application.core.data.local.mediator.BlockRemoteMediator
import id.application.core.data.network.service.ApplicationService
import id.application.core.domain.model.blocks.ItemAllBlocks
import id.application.core.domain.model.geotags.ItemAllGeotaging
import id.application.core.domain.repository.ApplicationRepository
import kotlinx.coroutines.flow.Flow

class GeotagingPagingSource(
    private val repository: ApplicationRepository
) : PagingSource<Int, ItemAllGeotaging>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ItemAllGeotaging> {
        val currentPage = params.key ?: 1
        return try {
            val response = repository.getAllGeotaging(
                block = null,
                createdBy = null,
                limitItem = params.loadSize,
                pageItem = currentPage
            )
            if (response.code == 200) {
                val storeResponse = response.data
                val items = storeResponse.items
                val prevKey = if (currentPage == 1) null else currentPage - 1
                val nextKey = if (currentPage >= storeResponse.totalPages) null else currentPage + 1
                LoadResult.Page(
                    data = items,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            } else {
                LoadResult.Error(Exception("Error code: ${response.code}"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ItemAllGeotaging>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}

class BlockPagingMediator(
    private val api : ApplicationService,
    private val database : ApplicationDatabase)
{
    @OptIn(ExperimentalPagingApi::class)
    fun fetchBlocks() : Flow<PagingData<ItemAllBlocks>> = Pager(
        config = PagingConfig(
            enablePlaceholders = false,
            pageSize = 10,
            initialLoadSize = 10,
            prefetchDistance = 1
        ),
        remoteMediator = BlockRemoteMediator(api, database),
        pagingSourceFactory = {
            database.blocksDao().retrieveAllBlock()
        }
    ).flow
}
