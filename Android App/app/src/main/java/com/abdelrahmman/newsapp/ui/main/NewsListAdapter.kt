package com.abdelrahmman.newsapp.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.abdelrahmman.newsapp.R
import com.abdelrahmman.newsapp.models.NewsPost
import com.abdelrahmman.newsapp.util.DateUtils
import com.abdelrahmman.newsapp.util.GenericViewHolder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.android.synthetic.main.layout_news_list_item.view.*

class NewsListAdapter(
    private val interaction: Interaction? = null,
    private val requestManager: RequestManager
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val NO_MORE_RESULTS = -1
    private val NEWS_ITEM = 0
    private val NO_MORE_RESULTS_NEWS_MARKER = NewsPost(
        NO_MORE_RESULTS,
        "",
        "",
        "",
        "",
        0
    )

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NewsPost>() {

        override fun areItemsTheSame(oldItem: NewsPost, newItem: NewsPost): Boolean {
            return oldItem.pk == newItem.pk
        }

        override fun areContentsTheSame(oldItem: NewsPost, newItem: NewsPost): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(NewsRecyclerChangeCallback(this), AsyncDifferConfig.Builder(DIFF_CALLBACK).build())

    internal inner class NewsRecyclerChangeCallback(
        private val adapter: NewsListAdapter
    ): ListUpdateCallback {
        override fun onChanged(position: Int, count: Int, payload: Any?) {
            adapter.notifyItemRangeChanged(position, count, payload)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            adapter.notifyDataSetChanged()
        }

        override fun onInserted(position: Int, count: Int) {
            adapter.notifyItemRangeChanged(position, count)
        }

        override fun onRemoved(position: Int, count: Int) {
            adapter.notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        when(viewType){

            NO_MORE_RESULTS -> {
                return GenericViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_no_more_results, parent, false))
            }

            NEWS_ITEM -> {
                return NewsViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.layout_news_list_item, parent, false),
                    interaction = interaction,
                    requestManager = requestManager
                )
            }

            else -> {
                return NewsViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.layout_news_list_item, parent, false),
                    interaction = interaction,
                    requestManager = requestManager
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NewsViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<NewsPost>, isQueryExhausted: Boolean) {
        val newList = list?.toMutableList()
        if (isQueryExhausted){
            newList?.add(NO_MORE_RESULTS_NEWS_MARKER)
        }
        differ.submitList(list)
    }

    fun preLoaderGlideImages(
        requestManager: RequestManager,
        list: List<NewsPost>
    ){
        for(newsPost in list){
            requestManager
                .load(newsPost.image)
                .preload()
        }
    }

    class NewsViewHolder
    constructor(
        itemView: View,
        val requestManager: RequestManager,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: NewsPost) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }

            requestManager
                .load(item.image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(itemView.news_image)

            itemView.news_title.text = item.title
            itemView.news_update_date.text = DateUtils.convertLongToStringDate(item.date_updated)

        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: NewsPost)
    }
}