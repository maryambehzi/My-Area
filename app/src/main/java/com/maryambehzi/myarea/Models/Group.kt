package com.maryambehzi.myarea.Models

import com.google.gson.annotations.SerializedName

class Group {
    @SerializedName("items")
    var item: ArrayList<Item>? = null

}