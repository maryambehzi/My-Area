package com.maryambehzi.myarea

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class ExploreResultsAdapter (private val onClickListener: LocationClickListener): RecyclerView.Adapter<LocationResultViewHolder>() {
    private var locationDataItems: List<LocationResult> = ArrayList()

    /**
     * Sets the data that the adapter operates on.  Will automatically chose between a full notifydatasetchanged or
     * using diffutils to only update the items that are needed to be updated.
     */
    fun setLocationResults(locations: List<LocationResult>) {
        if (locations.isNullOrEmpty()) {
            locationDataItems = locations
            notifyDataSetChanged()
            return
        }
        val oldItems = ArrayList(locationDataItems)
        locationDataItems = locations

        val callback = object : DiffCallback<LocationResult>(oldItems, locationDataItems) {}

        DiffUtil.calculateDiff(callback, true).dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationResultViewHolder {
        return LocationResultViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return locationDataItems.size
    }

    override fun onBindViewHolder(holder: LocationResultViewHolder, position: Int) {
        holder.bind(locationDataItems[position], onClickListener)
    }
}