package com.abdelrahmman.newsapp.di

import com.abdelrahmman.newsapp.di.main.MainFragmentBuildersModule
import com.abdelrahmman.newsapp.di.main.MainModule
import com.abdelrahmman.newsapp.di.main.MainScope
import com.abdelrahmman.newsapp.di.main.MainViewModelModule
import com.abdelrahmman.newsapp.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

    @MainScope
    @ContributesAndroidInjector(
        modules = [MainModule::class, MainFragmentBuildersModule::class, MainViewModelModule::class]
    )
    abstract fun contributeMainActivity(): MainActivity

}