package xyz.reisminer.trackingnotes

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import java.io.BufferedReader
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createHomeTiles(10)
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)

        checkLocationPermission()

        val createButton = findViewById<AppCompatButton>(R.id.createButton)
        createButton.setOnClickListener {
            val intent = Intent(this, writeNote::class.java)
            startActivity(intent)
        }
    }

    private fun createHomeTiles(y: Int) {
        val table = findViewById<TableLayout>(R.id.mainTable)

        val params = TableLayout.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.MATCH_PARENT
        )
        params.setMargins(10, 10, 10, 10)

        val tr = mutableListOf<TableRow>()

        val tileList = mutableListOf<TableLayout>()

        var helper = true

        for ((i, f) in fileList().withIndex()) {
            if (helper) {
                tr.add(TableRow(this))
                tr[i / 2].layoutParams = params
                tileList.add(TableLayout.inflate(this, R.layout.tiles_home, null) as TableLayout)

                (tileList[i].getChildAt(0) as TextView).text = f
                val bufferedReader: BufferedReader =
                    File("${filesDir.absolutePath}/$f").bufferedReader()
                val inputString = bufferedReader.use { it.readText() }

                (tileList[i].getChildAt(1) as TextView).text = inputString
                tileList[i].setOnClickListener { openAccordingTile(f) }
                tr[i / 2].addView(tileList[i])
                helper = !helper
            } else {
                tileList.add(TableLayout.inflate(this, R.layout.tiles_home, null) as TableLayout)
                (tileList[i].getChildAt(0) as TextView).text = f

                val bufferedReader: BufferedReader =
                    File("${filesDir.absolutePath}/$f").bufferedReader()
                val inputString = bufferedReader.use { it.readText() }
                (tileList[i].getChildAt(1) as TextView).text = inputString

                tileList[i].setOnClickListener { openAccordingTile(f) }
                tr[i / 2].addView(tileList[i])
                helper = !helper
            }
        }

        for (trr in tr)
            table.addView(trr, params)
    }

    private fun openAccordingTile(title: String) {
        val intent = Intent(this, writeNote::class.java)

        File(filesDir.toURI()).walk().forEach { daFile ->
            if (title == daFile.name) {
                intent.putExtra("title", title)
                val bufferedReader: BufferedReader = File(daFile.toURI()).bufferedReader()
                val inputString = bufferedReader.use { it.readText() }
                intent.putExtra("content", inputString)
                intent.putExtra("path", daFile.toPath().toString())
            }
        }
        intent.putExtra("new", false)
        startActivity(intent)
    }

    override fun onBackPressed() {
        finishAffinity()
    }

    private var fusedLocationProvider: FusedLocationProviderClient? = null
    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 30
        fastestInterval = 10
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        maxWaitTime = 60
    }

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                //The last location in the list is the newest
                val location = locationList.last()
                Toast.makeText(
                    this@MainActivity,
                    "Got Location: " + location.toString(),
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {

            fusedLocationProvider?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    override fun onPause() {
        super.onPause()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {

            fusedLocationProvider?.removeLocationUpdates(locationCallback)
        }
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        //Prompt the user once explanation has been shown
                        requestLocationPermission()
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                requestLocationPermission()
            }
        }
    }

    private fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationProvider?.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper()
                        )
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
    }
}
