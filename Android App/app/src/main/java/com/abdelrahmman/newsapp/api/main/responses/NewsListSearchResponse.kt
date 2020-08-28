package com.abdelrahmman.newsapp.api.main.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class NewsListSearchResponse(

    @SerializedName("results")
    @Expose
    var results: List<NewsSearchResponse>,

    @SerializedName("detail")
    @Expose
    var detail: String
) {

    override fun toString(): String {
        return "NewsListSearchResponse(results=$results, detail='$detail')"
    }
}