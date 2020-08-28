package com.abdelrahmman.newsapp.ui.main

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.abdelrahmman.newsapp.R
import com.abdelrahmman.newsapp.models.NewsPost
import com.abdelrahmman.newsapp.ui.DataState
import com.abdelrahmman.newsapp.ui.main.state.MainViewState
import com.abdelrahmman.newsapp.util.ErrorHandling
import com.abdelrahmman.newsapp.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : BaseMainFragment(),
    NewsListAdapter.Interaction,
    SwipeRefreshLayout.OnRefreshListener
{

    private lateinit var recyclerAdapter: NewsListAdapter
    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        swipe_refresh.setOnRefreshListener(this)

        subscribeObservers()
        initRecyclerView()

        if (savedInstanceState == null){
            viewModel.loadFirstPage()
        }
    }

    private fun onNewsSearch(){
        viewModel.loadFirstPage().let {
            resetUI()
        }
    }

    private fun resetUI(){
        recyclerview.smoothScrollToPosition(0)
        stateChangeListener.hideSoftKeyboard()
        focusable_view.requestFocus()
    }

    private fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            if (dataState != null){
                handlePagination(dataState)
                stateChangeListener.onDataStateChange(dataState)
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer {viewState ->
            Log.d(TAG, "MainFragment, ViewState: ${viewState}")
            if (viewState != null){

                recyclerAdapter.apply {

                    preLoaderGlideImages(requestManager, viewState.newsFields.newsList)

                    submitList(
                        list = viewState.newsFields.newsList,
                        isQueryExhausted = viewState.newsFields.isQueryExhausted
                    )
                }

            }
        })

    }

    private fun initSearchView(menu: Menu){
        activity?.apply {
            val searchManager: SearchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
            searchView = menu.findItem(R.id.action_search).actionView as SearchView
            searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
            searchView.maxWidth = Integer.MAX_VALUE
            searchView.setIconifiedByDefault(true)
            searchView.isSubmitButtonEnabled = true
        }

        //case1: keyboard
        val searchPlate = searchView.findViewById(R.id.search_src_text) as EditText
        searchPlate.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED || actionId == EditorInfo.IME_ACTION_SEARCH){
                val searchQuery = v.text.toString()
                Log.e(TAG, "SearchView: (keyboard) executing search... $searchQuery")
                viewModel.setQuery(searchQuery).let {
                    onNewsSearch()
                }
            }
            true
        }

        //case2: toolbar
        (searchView.findViewById(R.id.search_go_btn) as View).setOnClickListener{
            val searchQuery = searchPlate.text.toString()
            Log.e(TAG, "SearchView: (toolbar) executing search... $searchQuery")
            viewModel.setQuery(searchQuery).let {
                onNewsSearch()
            }
        }

    }

    private fun handlePagination(dataState: DataState<MainViewState>) {

        dataState.data?.let {
            it.data?.let {
                it.getContentIfNotHandled()?.let {
                    viewModel.handleIncomingNewsListData(it)
                }
            }
        }


        dataState.error?.let { event ->
            event.peekContent().response.message?.let {
                if (ErrorHandling.isPaginationDone(it)){

                    event.getContentIfNotHandled()

                    viewModel.setQueryExhausted(true)
                }
            }
        }
    }

    private fun initRecyclerView(){
        recyclerview.apply {
            layoutManager = LinearLayoutManager(this@MainFragment.context)
            val topSpacingItemDecoration = TopSpacingItemDecoration(30)
            removeItemDecoration(topSpacingItemDecoration)
            addItemDecoration(topSpacingItemDecoration)
            recyclerAdapter = NewsListAdapter(requestManager = requestManager, interaction = this@MainFragment)

            addOnScrollListener(object : RecyclerView.OnScrollListener(){
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (lastPosition == recyclerAdapter.itemCount.minus(1)){
                        Log.d(TAG, "MainFragment: Attempting to load next page...")
                        viewModel.nextPage()
                    }
                }
            })
            adapter = recyclerAdapter

        }
    }

    override fun onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu()
//        recyclerview.adapter = null
    }

    override fun onItemSelected(position: Int, item: NewsPost) {
        viewModel.setNewsPost(item)
        findNavController().navigate(R.id.action_mainFragment_to_viewNewsFragment)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        initSearchView(menu)
    }

    override fun onRefresh() {
        onNewsSearch()
        swipe_refresh.isRefreshing = false
    }

}