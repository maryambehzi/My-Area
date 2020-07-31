package com.maryambehzi.myarea.detail.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.foursquare.android.sample.R
import com.google.android.gms.maps.model.LatLng
import com.maryambehzi.myarea.detail.viewmodel.DetailsViewModel
import kotlinx.android.synthetic.main.content_details.*

class DetailsActivity : AppCompatActivity() {
    private val viewModel: DetailsViewModel by lazy { ViewModelProviders.of(fragment_details).get(
        DetailsViewModel::class.java) }

    companion object {
        private const val PARAM_LOCATION: String = "PARAM_LOCATION"
        private const val PARAM_CURRENT_LOCATION: String = "PARAM_CURRENT_LOCATION"
        const val DETAILS_REQUEST_CODE = 204

        fun buildIntent(context: Context, locationId: String, currentLocation: LatLng): Intent {
            return Intent(context, DetailsActivity::class.java)
                        .putExtra(PARAM_LOCATION, locationId)
                        .putExtra(PARAM_CURRENT_LOCATION, currentLocation)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowTitleEnabled(false)
        }

        viewModel.locationId = intent.getStringExtra(PARAM_LOCATION)
        viewModel.currentLocation = intent.getParcelableExtra(PARAM_CURRENT_LOCATION)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
