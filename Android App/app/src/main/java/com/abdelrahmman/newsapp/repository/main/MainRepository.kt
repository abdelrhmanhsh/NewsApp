package com.abdelrahmman.newsapp.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.abdelrahmman.newsapp.api.main.ApiMainService
import com.abdelrahmman.newsapp.api.main.responses.NewsListSearchResponse
import com.abdelrahmman.newsapp.models.NewsPost
import com.abdelrahmman.newsapp.persistence.NewsPostDao
import com.abdelrahmman.newsapp.repository.JobManager
import com.abdelrahmman.newsapp.repository.NetworkBoundResource
import com.abdelrahmman.newsapp.session.SessionManager
import com.abdelrahmman.newsapp.ui.DataState
import com.abdelrahmman.newsapp.ui.main.state.MainViewState
import com.abdelrahmman.newsapp.ui.main.state.MainViewState.*
import com.abdelrahmman.newsapp.util.Constants.Companion.PAGINATION_PAGE_SIZE
import com.abdelrahmman.newsapp.util.DateUtils
import com.abdelrahmman.newsapp.util.GenericApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

class MainRepository
@Inject
constructor(
    val apiMainService: ApiMainService,
    val newsPostDao: NewsPostDao,
    val sessionManager: SessionManager
) : JobManager("MainRepository") {

    private val TAG: String = "AppDebug"

    fun searchNewsPost(
        query: String,
        page: Int
    ): LiveData<DataState<MainViewState>> {
        return object : NetworkBoundResource<NewsListSearchResponse, List<NewsPost>, MainViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            false,
            true
        ) {
            override suspend fun createCacheRequestAndReturn() {
                withContext(Dispatchers.Main) {
                    //finish by viewing db cache
                    result.addSource(loadFromCache()) { viewState ->
                        viewState.newsFields.isQueryInProgress = false
                        if (page * PAGINATION_PAGE_SIZE > viewState.newsFields.newsList.size) {
                            viewState.newsFields.isQueryExhausted = true
                        }
                        onCompleteJob(DataState.data(viewState, null))
                    }
                }
            }

            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<NewsListSearchResponse>) {
                val newsPostList: ArrayList<NewsPost> = ArrayList()
                for (newsPostResponse in response.body.results) {
                    newsPostList.add(
                        NewsPost(
                            pk = newsPostResponse.pk,
                            slug = newsPostResponse.slug,
                            title = newsPostResponse.title,
                            body = newsPostResponse.body,
                            image = newsPostResponse.image,
                            date_updated = DateUtils.convertServerStringDateToLong(newsPostResponse.date_updated)
                        )
                    )
                }
                updateLocalDb(newsPostList)
                createCacheRequestAndReturn()
            }

            override fun createCall(): LiveData<GenericApiResponse<NewsListSearchResponse>> {
                return apiMainService.searchListPosts(
                    query = query,
                    page = page
                )
            }

            override fun loadFromCache(): LiveData<MainViewState> {
                return newsPostDao.getAllNewsPosts(
                    query = query,
                    page = page
                )
                    .switchMap {
                        object : LiveData<MainViewState>() {
                            override fun onActive() {
                                super.onActive()
                                value = MainViewState(
                                    NewsFields(
                                        newsList = it,
                                        isQueryInProgress = true
                                    )
                                )
                            }
                        }
                    }
            }

            override suspend fun updateLocalDb(cacheObject: List<NewsPost>?) {
                if (cacheObject != null) {
                    withContext(Dispatchers.IO) {
                        for (newsPost in cacheObject) {
                            try {

                                launch {
                                    Log.d(TAG, "updateLocalDb: inserting news $newsPost")
                                    newsPostDao.insert(newsPost)
                                }

                            } catch (e: Exception) {
                                Log.e(
                                    TAG,
                                    "updateLocalDb: error updating cache on news post with slug ${newsPost.slug}"
                                )
                            }
                        }
                    }
                }
            }

            override fun setJob(job: Job) {
                addJob("searchNewsPost", job)
            }

        }.asLiveData()
    }
}