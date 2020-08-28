package com.abdelrahmman.newsapp.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import com.abdelrahmman.newsapp.models.NewsPost
import com.abdelrahmman.newsapp.repository.main.MainRepository
import com.abdelrahmman.newsapp.session.SessionManager
import com.abdelrahmman.newsapp.ui.BaseViewModel
import com.abdelrahmman.newsapp.ui.DataState
import com.abdelrahmman.newsapp.ui.main.state.MainStateEvent
import com.abdelrahmman.newsapp.ui.main.state.MainStateEvent.*
import com.abdelrahmman.newsapp.ui.main.state.MainViewState
import com.abdelrahmman.newsapp.util.AbsentLiveData
import javax.inject.Inject

class MainViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val mainRepository: MainRepository
): BaseViewModel<MainStateEvent, MainViewState>() {

    override fun initNewViewState(): MainViewState {
        return MainViewState()
    }

    override fun handleStateEvent(stateEvent: MainStateEvent): LiveData<DataState<MainViewState>> {

        when(stateEvent){

            is NewsSearchEvent -> {

                return mainRepository.searchNewsPost(
                    query = getSearchQuery(),
                    page = getPage()
                )

            }

            is None -> {

                return AbsentLiveData.create()
                }
            }
        }

    // getters
    fun getPage(): Int {
        getCurrentViewStateOrNew().let {
            return it.newsFields.page
        }
    }

    fun getIsQueryExhausted(): Boolean {
        getCurrentViewStateOrNew().let {
            return it.newsFields.isQueryExhausted
        }
    }

    fun getIsQueryInProgress(): Boolean {
        getCurrentViewStateOrNew().let {
            return it.newsFields.isQueryInProgress
        }
    }

    fun getSearchQuery(): String {
        getCurrentViewStateOrNew().let {
            return it.newsFields.searchQuery
        }
    }


    //setters
    fun setQuery(query: String) {
        val update = getCurrentViewStateOrNew()

//        if (query.equals(update.newsFields.searchQuery)){
//            return
//        }

        update.newsFields.searchQuery = query
        setViewState(update)
    }

    fun setNewsListData(newsList: List<NewsPost>) {
        val update = getCurrentViewStateOrNew()
        update.newsFields.newsList = newsList
        setViewState(update)
    }

    fun setNewsPost(newsPost: NewsPost) {
        val update = getCurrentViewStateOrNew()
        update.viewNewsFields.newsPost = newsPost
        setViewState(update)
    }

    fun setQueryExhausted(isExhausted: Boolean) {
        val update = getCurrentViewStateOrNew()
        update.newsFields.isQueryExhausted = isExhausted
        setViewState(update)
    }

    fun setQueryInProgress(isInProgress: Boolean) {
        val update = getCurrentViewStateOrNew()
        update.newsFields.isQueryInProgress = isInProgress
        setViewState(update)
    }

    // pagination
    fun resetPage(){
        val update = getCurrentViewStateOrNew()
        update.newsFields.page = 1
        setViewState(update)
    }

    fun loadFirstPage(){
        setQueryInProgress(true)
        setQueryExhausted(false)
        resetPage()
        setStateEvent(NewsSearchEvent())
    }

    fun incrementPageNumber(){
        val update = getCurrentViewStateOrNew()
        val page = update.copy().newsFields.page
        update.newsFields.page = page + 1
        setViewState(update)
    }

    fun nextPage(){
        if (!getIsQueryExhausted() && !getIsQueryInProgress()){
            Log.d(TAG, "NewsViewModel: attempting to load next page...")
            incrementPageNumber()
            setQueryInProgress(true)
            setStateEvent(NewsSearchEvent())
        }
    }

    fun handleIncomingNewsListData(viewState: MainViewState){
        setQueryExhausted(viewState.newsFields.isQueryExhausted)
        setQueryInProgress(viewState.newsFields.isQueryInProgress)
        setNewsListData(viewState.newsFields.newsList)
    }

    fun cancelActiveJobs(){
        mainRepository.cancelActiveJobs()
        handlePendingData()
    }

    private fun handlePendingData() {
        setStateEvent(None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

}