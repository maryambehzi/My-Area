package com.maryambehzi.myarea.Helper

import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.setMargins
import com.foursquare.android.sample.R
import com.google.android.material.snackbar.Snackbar

class Error {
    companion object {
        /**
         * Show error message in a snack bar.  If an action is specified then the click listener will be applied to the retry button.
         */
        fun showError(view: View, @StringRes message: Int, @StringRes buttonText: Int, action: View.OnClickListener?) : Snackbar {
            val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE)
            val margin = snackBar.context.resources.getDimension(R.dimen.snackbar_margin).toInt()
            (snackBar.view.layoutParams as ViewGroup.MarginLayoutParams).setMargins(margin)
//            snackBar.view.background = ContextCompat.getDrawable(snackBar.context, R.drawable.snackbar_background)
            snackBar.setActionTextColor(ContextCompat.getColor(snackBar.context, R.color.colorAccent))
            snackBar.behavior
            action?.let {
                snackBar.setAction(buttonText, action)
            }

            snackBar.show()

            return snackBar
        }
    }
}