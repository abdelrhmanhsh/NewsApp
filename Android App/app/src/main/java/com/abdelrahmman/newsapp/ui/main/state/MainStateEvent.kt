package com.abdelrahmman.newsapp.ui.main.state

sealed class MainStateEvent {

    class NewsSearchEvent: MainStateEvent()

    class None: MainStateEvent()

}