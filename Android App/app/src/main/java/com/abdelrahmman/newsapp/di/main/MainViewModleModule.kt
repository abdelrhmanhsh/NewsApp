package com.abdelrahmman.newsapp.di.main

import androidx.lifecycle.ViewModel
import com.abdelrahmman.newsapp.di.ViewModelKey
import com.abdelrahmman.newsapp.ui.main.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindNewsViewModel(mainViewModel: MainViewModel): ViewModel


}