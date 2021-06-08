package xyz.reisminer.trackingnotes

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import okhttp3.*
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createHomeTiles()
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)

        checkLocationPermission()

        val createButton = findViewById<AppCompatButton>(R.id.createButton)
        createButton.setOnClickListener {
            val intent = Intent(this, writeNote::class.java)
            startActivity(intent)
        }
    }

    private fun createHomeTiles() {
        val table = findViewById<TableLayout>(R.id.mainTable)

        val params = TableLayout.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.MATCH_PARENT
        )
        params.setMargins(10, 10, 10, 10)

        val tr = mutableListOf<TableRow>()

        val tileList = mutableListOf<TableLayout>()

        var helper = true

        //enormous loop to generate the tiles on the homescreen
        //the if is there that on each row is two tiles. without it there would be one per row. looks nice too i guess
        //we're using a template which we definde our own in xml.
        for ((i, fileName) in fileList().withIndex()) {
            if (helper) {
                tr.add(TableRow(this))
                tr[i / 2].layoutParams = params
                tileList.add(TableLayout.inflate(this, R.layout.tiles_home, null) as TableLayout)

                (tileList[i].getChildAt(0) as TextView).text = fileName
                //open file and display the text of it in the lil preview
                val bufferedReader: BufferedReader =
                    File("${filesDir.absolutePath}/$fileName").bufferedReader()
                val inputString = bufferedReader.use { it.readText() }

                (tileList[i].getChildAt(1) as TextView).text = inputString
                tileList[i].setOnClickListener { openAccordingTile(fileName) }
                tr[i / 2].addView(tileList[i])
                helper = !helper
            } else {
                tileList.add(TableLayout.inflate(this, R.layout.tiles_home, null) as TableLayout)
                (tileList[i].getChildAt(0) as TextView).text = fileName

                val bufferedReader: BufferedReader =
                    File("${filesDir.absolutePath}/$fileName").bufferedReader()
                val inputString = bufferedReader.use { it.readText() }
                (tileList[i].getChildAt(1) as TextView).text = inputString

                //adds the new tile to the row.
                tileList[i].setOnClickListener { openAccordingTile(fileName) }
                tr[i / 2].addView(tileList[i])
                helper = !helper
            }
        }

        for (trr in tr)
            table.addView(trr, params)
    }

    private fun openAccordingTile(title: String) {
        val intent = Intent(this, writeNote::class.java)

        //go through each file in the directory and checks if the name of the file is
        // equals the title of the pressed note. it then opens said file and fills out
        // the fields on the next activity
        File(filesDir.toURI()).walk().forEach { daFile ->
            if (title == daFile.name) {
                intent.putExtra("title", title)
                val bufferedReader: BufferedReader = File(daFile.toURI()).bufferedReader()
                val inputString = bufferedReader.use { it.readText() }
                intent.putExtra("content", inputString)
                intent.putExtra("path", daFile.toPath().toString())
                //intent.extra sends data to the next activity
            }
        }
        intent.putExtra("new", false)
        startActivity(intent)
    }

    override fun onBackPressed() {
        finishAffinity() //-> close app
    }

    //some properties for the location gathering
    private var fusedLocationProvider: FusedLocationProviderClient? = null
    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        numUpdates =1
        interval=5

    }

    //is executed every time when location is requested
    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                //The last location in the list is the newest
                val location = locationList.last()

                //send data to webserver which then inserts data to db
                openAPI("https://reisminer.xyz/trackingnotes/?k=${md5(location.latitude.toString())}" +
                        "&a=${location.latitude}&o=${location.longitude}")
            }
        }
    }

    //hover over "onResume" to see explaination
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

    //hover over "onPause" to see explaination
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
            //this generates perhaps probably presumably maybe the amogus popup
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

    //generating hash for pseudo security. api validates this serverside.
    //this prevents random users from inserting data
    fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

    //open url to transmit data. using okhttp library
    fun openAPI(url: String) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) =
                println(response.body?.string())
        })
    }
}
