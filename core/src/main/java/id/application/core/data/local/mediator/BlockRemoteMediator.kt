package id.application.core.data.local.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import id.application.core.data.local.database.ApplicationDatabase
import id.application.core.data.network.service.ApplicationService
import id.application.core.domain.model.blocks.ItemAllBlocks
import id.application.core.domain.model.blocks.toAllBlocksList
import id.application.core.domain.model.remotekeys.RemoteKeys

@OptIn(ExperimentalPagingApi::class)
class BlockRemoteMediator(
    private val apiEndPoint: ApplicationService,
    private val database: ApplicationDatabase
) : RemoteMediator<Int, ItemAllBlocks>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ItemAllBlocks>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                prevKey
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                nextKey
            }
        }
        return try {
            val responseData = apiEndPoint.getAllBlocks(pageItem = page)
            val endOfPaginationReached = responseData.data.items.isEmpty()
            database.withTransaction {
                if (loadType == LoadType.REFRESH){
                    database.blocksDao().deleteAllBlock()
                }
                val prevKey = if (page == 1) null else page -1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = responseData.data.items.map {
                    RemoteKeys(id = it.id.toString(), prevKey = prevKey, nextKey=nextKey)
                }
                database.blocksDao().insertAllKeyBlock(keys)
                database.blocksDao().insertAllBlock(responseData.data.items.toAllBlocksList())
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        }catch (exception: Exception){
            MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ItemAllBlocks>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty()}?.data?.firstOrNull()?.let { data ->
            database.blocksDao().getRemoteKeysIdBlock(data.id.toString())
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ItemAllBlocks>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty()}?.data?.lastOrNull()?.let { data ->
            database.blocksDao().getRemoteKeysIdBlock(data.id.toString())
        }
    }
    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, ItemAllBlocks>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.blocksDao().getRemoteKeysIdBlock(id.toString())
            }
        }
    }

    companion object{
        const val INITIAL_PAGE_INDEX = 1
    }
}