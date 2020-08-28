package com.abdelrahmman.newsapp.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.abdelrahmman.newsapp.R
import com.abdelrahmman.newsapp.models.NewsPost
import com.abdelrahmman.newsapp.util.DateUtils
import kotlinx.android.synthetic.main.fragment_view_news.*

class ViewNewsFragment : BaseMainFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_view_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeObservers()
        stateChangeListener.expandAppBar()
    }

    private fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState.viewNewsFields.newsPost?.let { newsPost ->
                setBlogProperties(newsPost)
            }
        })
    }

    private fun setBlogProperties(newsPost: NewsPost){

        requestManager
            .load(newsPost.image)
            .into(news_image)

        news_title.text = newsPost.title
        news_body.text = newsPost.body
        news_update_date.text = DateUtils.convertLongToStringDate(newsPost.date_updated)
    }

}