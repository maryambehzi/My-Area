package com.maryambehzi.myarea.detail.ui

import android.content.Context
import android.content.Intent
import android.net.Uri

class DetailsClickHandlers {
    fun onWebsiteClick(context: Context, url: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}