package com.abdelrahmman.newsapp.di.main

import com.abdelrahmman.newsapp.api.main.ApiMainService
import com.abdelrahmman.newsapp.persistence.AppDatabase
import com.abdelrahmman.newsapp.persistence.NewsPostDao
import com.abdelrahmman.newsapp.repository.main.MainRepository
import com.abdelrahmman.newsapp.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class MainModule {

    @MainScope
    @Provides
    fun provideOpenApiService(retrofitBuilder: Retrofit.Builder): ApiMainService {
        return retrofitBuilder
            .build()
            .create(ApiMainService::class.java)
    }


    @MainScope
    @Provides
    fun provideNewsPostDao(db: AppDatabase): NewsPostDao {
        return db.getNewsPostDao()
    }

    @MainScope
    @Provides
    fun provideNewsRepository(
        apiMainService: ApiMainService,
        newsPostDao: NewsPostDao,
        sessionManager: SessionManager
    ): MainRepository {
        return MainRepository(apiMainService, newsPostDao, sessionManager)
    }

}