package com.abdelrahmman.newsapp.persistence

import androidx.lifecycle.LiveData
import androidx.room.*
import com.abdelrahmman.newsapp.models.NewsPost
import com.abdelrahmman.newsapp.util.Constants.Companion.PAGINATION_PAGE_SIZE

@Dao
interface NewsPostDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(newsPost: NewsPost): Long

    @Query("""
        SELECT * FROM news_post WHERE title LIKE '%' || :query || '%'
        OR body LIKE '%' || :query || '%' LIMIT (:page * :pageSize)
    """)
    fun getAllNewsPosts(
        query: String,
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): LiveData<List<NewsPost>>

}