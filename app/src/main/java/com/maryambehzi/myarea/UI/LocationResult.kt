package com.maryambehzi.myarea.UI

import androidx.annotation.NonNull
import com.maryambehzi.myarea.Models.Venue

class LocationResult(private val venue: Venue?) : ILocationResult {
    val id = venue?.id
    val locationName = venue?.name
    val locationCategory = venue?.categories?.firstOrNull()?.pluralName
    val locationIcon = venue?.let { buildIconPath(it) }
    val locationDistance = venue?.location?.distance
    val lat = venue?.location?.lat
    val lng = venue?.location?.lng


    @NonNull
    private fun buildIconPath(venue: Venue): String {
        venue.categories?.firstOrNull()?.icon?.let{
            if (!it.prefix.isNullOrBlank() && !it.suffix.isNullOrBlank()) {
                return it.prefix + "88" + it.suffix
            }
        }

        return ""
    }

    override fun areItemsSame(other: ILocationResult): Boolean {
        return other is LocationResult
    }

    override fun areContentsSame(other: ILocationResult): Boolean {
        val otherResult = other as LocationResult
        return  id == otherResult.id &&
                locationCategory == otherResult.locationCategory &&
                locationDistance == otherResult.locationDistance &&
                locationIcon == otherResult.locationIcon &&
                locationName == otherResult.locationName &&
                lat == otherResult.lat &&
                lng == otherResult.lng
    }

}