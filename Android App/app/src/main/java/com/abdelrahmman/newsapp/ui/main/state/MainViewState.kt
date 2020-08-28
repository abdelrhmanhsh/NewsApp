package com.abdelrahmman.newsapp.ui.main.state

import com.abdelrahmman.newsapp.models.NewsPost

data class MainViewState(

    // NewsFragment vars
    var newsFields: NewsFields = NewsFields(),

    // ViewNewsFragment vars
    var viewNewsFields: ViewNewsFields = ViewNewsFields()


){
    data class NewsFields(
        var newsList: List<NewsPost> = ArrayList<NewsPost>(),
        var searchQuery: String = "",
        var page: Int = 1,
        var isQueryInProgress: Boolean = false,
        var isQueryExhausted: Boolean = false
    )

    data class ViewNewsFields(
        var newsPost: NewsPost? = null
    )

}