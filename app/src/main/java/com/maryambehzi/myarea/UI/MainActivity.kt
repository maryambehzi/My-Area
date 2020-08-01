package com.foursquare.android.sample

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import android.view.View
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.maryambehzi.myarea.detail.ui.DetailsActivity
import com.maryambehzi.myarea.Helper.Error
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
class MainActivity : AppCompatActivity(), LifecycleOwner, LocationClickListener {
    companion object {
        const val STATE_QUERY_STRING = "queryString"
        private const val PERMISSION_LOCATION_REQUEST_CODE = 579
    }

    private val viewModel: MainScreenViewModel by lazy { ViewModelProviders.of(this).get<MainScreenViewModel>(MainScreenViewModel::class.java) }
    private val adapter: ExploreResultsAdapter by lazy { ExploreResultsAdapter(this) }
    private val locationProviderClient: FusedLocationProviderClient by lazy { FusedLocationProviderClient(this) }
    private var snackBar: Snackbar? = null
    private var searchView: SearchView? = null
    private var currentLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        var editor: SharedPreferences.Editor = preferences.edit()

        handleGetLocation()
        Log.d("location1", currentLocation.toString())

        var json =preferences.getString("savedlocations", "def")
        val type = object : TypeToken<ArrayList<LocationResult>>() {}.type
//        adapter.setLocationResults(Gson().fromJson(json, type))
        adapter.setLocationResults(Gson().fromJson<ArrayList<LocationResult>>(json, type))

//        if (preferences.getString("response", "def") != "def")

        main_locations_recycler.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        main_locations_recycler.adapter = adapter

        savedInstanceState?.let {
            val query = it.getString(STATE_QUERY_STRING)
            query?.let { queryString ->
                viewModel.setQuery(queryString, currentLocation)
                Log.d("location2", currentLocation.toString())

//                val sharedPreferences = getSharedPreferences("production", Context.MODE_PRIVATE)
//                sharedPreferences.edit().putString("CurrentLocation", currentLocation.toString()).apply()
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
                Log.d("location3", currentLocation.toString())

            } else {
                viewModel.refresh(currentLocation)
                Log.d("location4", currentLocation.toString())

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
                editor.putString("CurrentLocation", currentLocation!!.latitude.toString()+","+currentLocation!!.longitude.toString())
                editor.commit()
                Log.d("location6", currentLocation.toString())
                Log.d("latlong", preferences.getString("CurrentLocation", "def"))
                editor.putString("response", viewModel.locationResults.value.toString())
                var json: String = Gson().toJson(it)
                editor.putString("savedlocations", json)
                editor.commit()

            }

        })

        viewModel.locationResultsError.observe(this, Observer<Int> {
            dismissSnackBar()
            if (it != null) {
                when (it) {
                    MainScreenViewModel.ERROR_CODE_RETRIEVE -> snackBar = Error.showError(main_coordinator, R.string.request_failed_main, R.string.retry, View.OnClickListener {
                        dismissSnackBar()
                        viewModel.refresh(currentLocation)
                        Log.d("location7", currentLocation.toString())

//                        viewModel.refreshOffline(preferences.getString("CurrentLocation", "def"))

                    })
                    MainScreenViewModel.ERROR_CODE_NO_CURRENT_LOCATION -> snackBar = Error.showError(main_coordinator, R.string.error_location_permission_denied, R.string.enable, View.OnClickListener {
                        //launch app detail settings page to let the user enable the permission that they denied
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        intent.data = Uri.fromParts("package", packageName, null)
                        Log.d("location8", currentLocation.toString())

                        startActivity(intent)
                        finish()
                    })
                }
            }
        })
//        viewModel.refreshOffline()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        adapter.notifyDataSetChanged()
    }

    /**
     * Handle clicks on the location view holders and startup the details screen
     */
    override fun onLocationClicked(id: String) {
        currentLocation?.let { currentLocation ->
            startActivityForResult(DetailsActivity.buildIntent(this, id, LatLng(currentLocation.latitude, currentLocation.longitude)), DetailsActivity.DETAILS_REQUEST_CODE)
        }
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
        val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        var editor: SharedPreferences.Editor = preferences.edit()
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
//            editor.putString("CurrentLoction", currentLocation!!.latitude.toString()+" "+currentLocation!!.longitude.toString())
//            editor.commit()
//            Log.d("CurrentLoction", preferences.getString("CurrentLoction", "def"))
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
