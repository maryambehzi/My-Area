package com.maryambehzi.myarea.api

import com.maryambehzi.myarea.Models.FoursquareResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FoursquareService {
    companion object {
        private const val CLIENT_ID = "IK4QB0KWMKJMNIFDXJY5X51VUP4NPXSPV0K12D4Z3D5YZUQZ"
        private const val CLIENT_SECRET = "PGSIDON1C2TSKTSDNKIK4G2GSMCRMK1AXTKUJZWUU0GRWWEL"
        private const val VERSION = "20180401"
        private const val COMMON_PARAMS = "&client_id=$CLIENT_ID&client_secret=$CLIENT_SECRET&v=$VERSION"

        //'35.72037,51.48758'
    }


    @GET("/v2/venues/explore?limit=50$COMMON_PARAMS")
    fun getLocationResults(@Query("ll") latlng: String): Call<FoursquareResponse>


}