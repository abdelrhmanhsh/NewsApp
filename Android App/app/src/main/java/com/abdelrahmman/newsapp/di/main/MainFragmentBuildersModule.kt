package com.abdelrahmman.newsapp.di.main

import com.abdelrahmman.newsapp.ui.main.MainFragment
import com.abdelrahmman.newsapp.ui.main.ViewNewsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentBuildersModule {

    @ContributesAndroidInjector()
    abstract fun contributeMainFragment(): MainFragment

    @ContributesAndroidInjector()
    abstract fun contributeViewNewsFragment(): ViewNewsFragment

}