package com.foursquare.android.sample

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
//import com.maryambehzi.myarea.UI.DetailsActivity
//import com.pnuema.android.foursite.helpers.Errors
import com.maryambehzi.myarea.UI.ExploreResultsAdapter
import com.maryambehzi.myarea.UI.LocationResult
import com.maryambehzi.myarea.UI.LocationClickListener
import com.maryambehzi.myarea.UI.MainScreenViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


/**
 * A sample activity demonstrating usage of the Foursquare auth library.
 *
 * @date 2013-06-01
 */
class MainActivity : AppCompatActivity(), LifecycleOwner,
    LocationClickListener {
    companion object {
        const val STATE_QUERY_STRING = "queryString"
        private const val PERMISSION_LOCATION_REQUEST_CODE = 579
    }
    private val viewModel: MainScreenViewModel by lazy { ViewModelProviders.of(this).get<MainScreenViewModel>(
        MainScreenViewModel::class.java) }
    private val adapter: ExploreResultsAdapter by lazy {
        ExploreResultsAdapter(
            this
        )
    }
    private val locationProviderClient: FusedLocationProviderClient by lazy { FusedLocationProviderClient(this) }
    private var snackBar: Snackbar? = null
    private var searchView: SearchView? = null
    private var currentLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        setSupportActionBar(toolbar)

        handleGetLocation()

        main_locations_recycler.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        main_locations_recycler.adapter = adapter

        savedInstanceState?.let {
            val query = it.getString(STATE_QUERY_STRING)
            query?.let { queryString ->
                viewModel.setQuery(queryString, currentLocation)
            }
        }

        main_swipe_refresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorAccent))
        main_swipe_refresh.setOnRefreshListener {
            if (viewModel.searchFilter.isBlank()) {
                main_swipe_refresh.isRefreshing = false
                return@setOnRefreshListener
            }

            dismissSnackBar()

            if (currentLocation == null) {
                handleGetLocation()
            } else {
                viewModel.refresh(currentLocation)
            }
        }

        viewModel.locationResults.observe(this, Observer<ArrayList<LocationResult>> {
            //cancel the progress indicator
            main_swipe_refresh.isRefreshing = false
            dismissSnackBar()

            if (it.isEmpty()) {
                //set empty state
                toggleEmptyState(true)
            } else {
                //update adapter with new results
                toggleEmptyState(false)
                adapter.setLocationResults(locations = it)
            }

        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        adapter.notifyDataSetChanged()
    }

    /**
     * Handle standard on back pressed events but intercept back presses
     * and clear out the search results first before exiting on a subsequent call
     */
    override fun onBackPressed() {
        searchView?.let {
            if (!it.isIconified) {
                it.onActionViewCollapsed()
                return
            }
        }
        super.onBackPressed()
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
     * Set screen to show the empty message and glyph or show the results and swipe refresh
     * True sets it to empty, false will display the results with no glyph shown
     */
    private fun toggleEmptyState(state: Boolean) {
        if (state) {
            main_swipe_refresh.visibility = View.GONE
            group_empty_data.visibility = View.VISIBLE

        } else {
            main_swipe_refresh.visibility = View.VISIBLE
            group_empty_data.visibility = View.GONE
        }
    }

    private fun handleGetLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf( Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_LOCATION_REQUEST_CODE
            )
            return
        }

        locationProviderClient.lastLocation.addOnSuccessListener {
            currentLocation = it
            dismissSnackBar()
            viewModel.refresh(currentLocation)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_LOCATION_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    //permission denied by user
                    dismissSnackBar()
                    AlertDialog.Builder(this)
                        .setMessage(R.string.error_closing_no_location_permission)
                        .setPositiveButton(R.string.close) { _, _ -> finish() }
                        .setNegativeButton(R.string.enable) {_, _ ->
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intent.data = Uri.fromParts("package", packageName, null)
                            startActivity(intent)
                        }
                        .setCancelable(false)
                        .create()
                        .show()
                } else {
                    //permission granted
                    dismissSnackBar()
                    handleGetLocation()
                }
            }
        }
    }
}