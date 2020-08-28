package com.abdelrahmman.newsapp.api.main

import androidx.lifecycle.LiveData
import com.abdelrahmman.newsapp.api.main.responses.NewsListSearchResponse
import com.abdelrahmman.newsapp.util.GenericApiResponse
import retrofit2.http.*

interface ApiMainService {

    @GET("news/list")
    fun searchListPosts(
        @Query("search") query: String,
        @Query("page") page: Int
    ): LiveData<GenericApiResponse<NewsListSearchResponse>>

}











