package id.application.core.data.local.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import id.application.core.data.local.database.ApplicationDatabase
import id.application.core.data.network.model.geotags.AllGeotaging
import id.application.core.data.network.service.ApplicationService
import id.application.core.domain.model.geotags.toDataAllGeotagingItem
import id.application.core.domain.model.remotekeys.RemoteKeys

@OptIn(ExperimentalPagingApi::class)
class GeotagingRemoteMediator(
    private val apiEndPoint: ApplicationService,
    private val database: ApplicationDatabase
) : RemoteMediator<Int, AllGeotaging>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, AllGeotaging>
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
            val responseData = apiEndPoint.getAllGeotaging(pageItem = page, createdBy = "user")
            val endOfPaginationReached = responseData.data.items.isEmpty()
            database.withTransaction {
                if (loadType == LoadType.REFRESH){
                    database.geotagsDao().deleteAll()
                }
                val prevKey = if (page == 1) null else page -1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = responseData.data.items.map {
                    RemoteKeys(id = it.id.toString(), prevKey = prevKey, nextKey=nextKey)
                }
                database.geotagsDao().insertAllKey(keys)
                database.geotagsDao().insertAllGeotags(responseData.data.items)
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        }catch (exception: Exception){
            MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, AllGeotaging>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty()}?.data?.firstOrNull()?.let { data ->
            database.geotagsDao().getRemoteKeysId(data.id.toString())
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, AllGeotaging>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty()}?.data?.lastOrNull()?.let { data ->
            database.geotagsDao().getRemoteKeysId(data.id.toString())
        }
    }
    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, AllGeotaging>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.geotagsDao().getRemoteKeysId(id.toString())
            }
        }
    }

    companion object{
        const val INITIAL_PAGE_INDEX = 1
    }
}