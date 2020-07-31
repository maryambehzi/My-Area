package com.maryambehzi.myarea.detail.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.foursquare.android.sample.R
import com.foursquare.android.sample.databinding.FragmentDetailsBinding
import com.maryambehzi.myarea.detail.models.VenueDetail
import com.maryambehzi.myarea.detail.viewmodel.DetailsViewModel
import kotlinx.android.synthetic.main.fragment_details.*

class DetailsFragment : Fragment() {
    companion object {
        private const val PERMISSION_CALL_REQUEST_CODE = 124
    }
    private val viewModel: DetailsViewModel by lazy { ViewModelProviders.of(this).get(
        DetailsViewModel::class.java) }
    private var snackBar: Snackbar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate view and obtain an instance of the binding class.
        val binding: FragmentDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_details, container, false)

        // Specify the current fragment as the lifecycle owner
        binding.lifecycleOwner = this

        //assign the view model to be bound
        binding.model = viewModel
        binding.handlers =
            DetailsClickHandlers()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //handle bad data by going back if no location id
        val locationId = viewModel.locationId
        if (locationId.isBlank()) {
            activity?.onBackPressed()
            return
        }

        dismissSnackBar()
//        viewModel.details.observe(viewLifecycleOwner, Observer { venueDetail ->
//            if (venueDetail == null) {
//                //no data returned which is indicative of an error case, so show an error message
//                activity?.let {
//                    activity?.findViewById<CoordinatorLayout>(R.id.details_coordinator)?.let {
//                        snackBar = Error.showError(it, R.string.request_failed_details, R.string.retry, View.OnClickListener {
//                            dismissSnackBar()
//                            viewModel.refresh()
//                        })
//                    }
//                }
//            } else {
//                //data returned successfully so lets populate the screen
//                populateScreen(venueDetail)
//            }
//        })
        viewModel.refresh()
    }

    /**
     * Dismiss any error message that is showing
     */
    private fun dismissSnackBar() {
        snackBar?.let {
            it.dismiss()
            snackBar = null
        }
    }

    /**
     * Populates the screen with the data retrieved from the API
     */
    private fun populateScreen(venueDetail: VenueDetail?) {
        if (venueDetail == null || !isAdded || context == null) {
            activity?.onBackPressed()
            return
        }

        //set the ratings bar to the color provided if its available
        context?.let {
            DrawableCompat.setTint(details_rating_bar.progressDrawable, ContextCompat.getColor(it, R.color.grey_disabled))
        }
        venueDetail.ratingColor?.let { ratingColor ->
            if (ratingColor != "null") {
                DrawableCompat.setTint(details_rating_bar.progressDrawable, Color.parseColor("#$ratingColor"))
            }
        }

        //phone button
        val number = venueDetail.contact?.phone
        details_phone.setOnClickListener {
            handleCall(number?:"")
        }
    }

    private fun handleCall(number: String) {
        if (ContextCompat.checkSelfPermission(activity as Context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity as Activity, arrayOf( Manifest.permission.CALL_PHONE),
                PERMISSION_CALL_REQUEST_CODE
            )
            return
        }

        startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$number")))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_CALL_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    //permission denied by user
                    details_phone.isEnabled = false
                } else {
                    //permission granted
                    val number = viewModel.details.value?.contact?.phone
                    if (!number.isNullOrBlank()) {
                        handleCall(number)
                    }
                }
            }
        }
    }
}
