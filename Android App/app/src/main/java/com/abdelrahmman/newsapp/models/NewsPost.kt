package com.abdelrahmman.newsapp.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news_post")
data class NewsPost(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "pk")
    var pk: Int,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "slug")
    var slug: String,

    @ColumnInfo(name = "body")
    var body: String,

    @ColumnInfo(name = "image")
    var image: String,

    @ColumnInfo(name = "date_updated")
    var date_updated: Long

) {

    override fun toString(): String {
        return "NewsPost(pk=$pk, " +
                "title='$title', " +
                "slug='$slug', " +
                "image='$image', " +
                "date_updated=$date_updated, "
    }

}