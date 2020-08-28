package com.abdelrahmman.newsapp.ui

interface DataStateChangeListener {

    fun onDataStateChange(dataState: DataState<*>?)
    fun expandAppBar()
    fun hideSoftKeyboard()

}