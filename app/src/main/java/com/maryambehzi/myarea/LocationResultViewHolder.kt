package com.maryambehzi.myarea

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.foursquare.android.sample.R
import kotlinx.android.synthetic.main.location_result_item.view.*
import kotlin.math.roundToInt

class LocationResultViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(
    R.layout.location_result_item, parent, false)){

    fun bind(locationResult: LocationResult, onClickListener: LocationClickListener) {
        val context = itemView.context

        //name and category display
        itemView.locationName.text = locationResult.locationName?: ""
        itemView.locationCategory.text = locationResult.locationCategory?: ""

        //distance display
        itemView.locationDistance.text = ""
        locationResult.locationDistance?.let {meters ->
            //if longer than a mile display miles
            if (meters >= 1609.34) {
                val miles = (meters / 1609.34) //convert to miles
                itemView.locationDistance.text = "You are Here"
            } else {
                val feet = (meters / 3.28084).roundToInt() //convert to feet
                itemView.locationDistance.text = "You are Here"
            }
        }

        //load the image async with Glide so that the UI doesnt have to wait around on images to load (GlideConfig.kt)
//        GlideApp.with(context).load(locationResult.locationIcon).into(itemView.locationImage)

        //set the initial state of the favorites icon by checking if its a favorite in the database
//        setupFavoriteIndicator(locationResult, onClickListener)

        //send the click event to the listener
        itemView.setOnClickListener{
            locationResult.id?.let {
                onClickListener.onLocationClicked(it)
            }
        }
    }

}