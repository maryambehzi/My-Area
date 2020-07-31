package com.maryambehzi.myarea.UI

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
            itemView.locationDistance.text = meters.toString()+" meters away"
        }

        //send the click event to the listener
        itemView.setOnClickListener{
            locationResult.id?.let {
                onClickListener.onLocationClicked(it)
            }
        }
    }

}