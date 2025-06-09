package com.vortexen.sharpchat.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.vortexen.sharpchat.data.DataState
import com.vortexen.sharpchat.data.model.ContactSuggestion
import com.vortexen.sharpchat.services.ContactDiscoveryService
import kotlinx.coroutines.flow.firstOrNull

class ContactPagingSource(
    private val service: ContactDiscoveryService, private val pageSize: Int
) : PagingSource<String, ContactSuggestion>() {

    override fun getRefreshKey(state: PagingState<String, ContactSuggestion>): String? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.suggestion_id
        }
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, ContactSuggestion> {
        val pageKey = params.key ?: "0"
        return try {
            when (val dataState = service.getContactSuggestions(pageSize).firstOrNull()) {
                is DataState.Success -> {
                    val suggestions = dataState.data ?: emptyList()

                    val nextKey = if (suggestions.size < pageSize) null else pageKey + pageSize
                    LoadResult.Page(
                        data = suggestions,
                        prevKey = if (pageKey == "0") null else suggestions.first().suggestion_id,
                        nextKey = nextKey
                    )
                }

                is DataState.Error -> LoadResult.Error(Exception(dataState.exception))
                else -> LoadResult.Page(
                    emptyList(), prevKey = null, nextKey = null
                ) // Loading state fallback
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
