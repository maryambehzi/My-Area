package com.maryambehzi.myarea.Models

import com.google.gson.annotations.SerializedName

class Response {
    @SerializedName("groups")
    var groups: ArrayList<Group>? = null
}
