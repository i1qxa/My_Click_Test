package com.example.myclicktest.data.db.link

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LinkDB(
    @PrimaryKey
    val id:Int,
    val baseLink:String,
    val baseLinkWithParams:String?,
    val lastLink:String?,
)
