package ipvc.estg.streetreport

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import ipvc.estg.streetreport.api.EndPoints
import ipvc.estg.streetreport.api.Report
import ipvc.estg.streetreport.api.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val ID = "ID"

class ReportMapa : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var reports: List<Report>

    // add to implement last known location
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    //added to implement location periodic updates
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest

    //added to implement distance between two locations
    private var continenteLat: Double = 0.0
    private var continenteLong: Double = 0.0

    private val newReportRequestCode = 1

    //markers
    private lateinit var marker: Marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_mapa)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //initialize fusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        //requestlocation
        createLocationRequest()


        //obter atualizacoes da localizacao
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                lastLocation = p0.lastLocation
                if (ActivityCompat.checkSelfPermission(
                        this@ReportMapa,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this@ReportMapa,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    return
                }
                mMap.isMyLocationEnabled = true

                var loc = LatLng(lastLocation.latitude, lastLocation.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15.0f))


            }
        }
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getReports()

        var position: LatLng
        //obter reports e criar markers
        call.enqueue(object : Callback<List<Report>> {
            override fun onResponse(call: Call<List<Report>>, response: Response<List<Report>>) {

                if (response.isSuccessful) {
                    reports = response.body()!!
                    for (report in reports) {
                        position = LatLng(report.latitude, report.longitude)
                        val sharedPref: SharedPreferences = getSharedPreferences(
                            getString(R.string.sharedPref), Context.MODE_PRIVATE
                        )

                        val user: Int = sharedPref.getInt(R.string.userlogged.toString(), 0)
                        if (report.utilizador_id != user) {
                            marker = mMap.addMarker(
                                MarkerOptions().position(position).title(report.nomeTipo)
                                    .snippet(report.descricao).icon(
                                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                                )
                            )
                            marker.tag = report.id
                        } else {
                            marker = mMap.addMarker(
                                MarkerOptions().position(position).title(report.nomeTipo)
                                    .snippet(report.descricao).icon(
                                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                                )
                            )
                            marker.tag = report.id
                        }


                    }
                }
            }

            override fun onFailure(call: Call<List<Report>>, t: Throwable) {
                Toast.makeText(this@ReportMapa, "${t.message}", Toast.LENGTH_SHORT).show()
                Log.d("erro", "${t.message}")
            }
        })

    }

    companion object {
        // add to implement last known location
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1

        //added to implement location periodic updates
        private const val REQUEST_CHECK_SETTINGS = 2
    }


    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null /* Looper */
        )
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    public override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    override fun onInfoWindowClick(marker: Marker) {
        val intent = Intent(this, ReportInfo::class.java).apply {
            putExtra(ID, marker.tag.toString())
        }
        startActivity(intent)


    }

    override fun onMapReady(googleMap: GoogleMap) {


        mMap = googleMap
        mMap.setOnInfoWindowClickListener(this)


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection

        val sharedPref: SharedPreferences = getSharedPreferences(
            getString(R.string.sharedPref), Context.MODE_PRIVATE
        )
        return when (item.itemId) {
            R.id.todos -> {
                val request = ServiceBuilder.buildService(EndPoints::class.java)
                val call = request.getReports()

                var position: LatLng
                //obter reports e criar markers
                call.enqueue(object : Callback<List<Report>> {
                    override fun onResponse(
                        call: Call<List<Report>>,
                        response: Response<List<Report>>
                    ) {

                        if (response.isSuccessful) {
                            reports = response.body()!!
                            for (report in reports) {
                                position = LatLng(report.latitude, report.longitude)
                                val sharedPref: SharedPreferences = getSharedPreferences(
                                    getString(R.string.sharedPref), Context.MODE_PRIVATE
                                )

                                val user: Int = sharedPref.getInt(R.string.userlogged.toString(), 0)
                                if (report.utilizador_id != user) {
                                    marker = mMap.addMarker(
                                        MarkerOptions().position(position).title(report.nomeTipo)
                                            .snippet(report.descricao).icon(
                                            BitmapDescriptorFactory.defaultMarker(
                                                BitmapDescriptorFactory.HUE_RED
                                            )
                                        )
                                    )
                                    marker.tag = report.id
                                } else {
                                    marker = mMap.addMarker(
                                        MarkerOptions().position(position).title(report.nomeTipo)
                                            .snippet(report.descricao).icon(
                                            BitmapDescriptorFactory.defaultMarker(
                                                BitmapDescriptorFactory.HUE_BLUE
                                            )
                                        )
                                    )
                                    marker.tag = report.id
                                }


                            }
                        }
                    }

                    override fun onFailure(call: Call<List<Report>>, t: Throwable) {
                        Toast.makeText(this@ReportMapa, "${t.message}", Toast.LENGTH_SHORT).show()
                        Log.d("erro", "${t.message}")
                    }
                })
                true
            }
            R.id.obras -> {
                mMap.clear()
                val request = ServiceBuilder.buildService(EndPoints::class.java)
                val call = request.getReportByTipo("Obras")

                var position: LatLng
                //obter reports e criar markers
                call.enqueue(object : Callback<List<Report>> {
                    override fun onResponse(
                        call: Call<List<Report>>,
                        response: Response<List<Report>>
                    ) {

                        if (response.isSuccessful) {
                            if (response.body()!!.isEmpty()) {
                                Toast.makeText(
                                    this@ReportMapa,
                                    R.string.noReports,
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            reports = response.body()!!
                            for (report in reports) {
                                position = LatLng(report.latitude, report.longitude)
                                val sharedPref: SharedPreferences = getSharedPreferences(
                                    getString(R.string.sharedPref), Context.MODE_PRIVATE
                                )

                                val user: Int = sharedPref.getInt(R.string.userlogged.toString(), 0)
                                if (report.utilizador_id != user) {
                                    marker = mMap.addMarker(
                                        MarkerOptions().position(position).title(report.nomeTipo)
                                            .snippet(report.descricao).icon(
                                            BitmapDescriptorFactory.defaultMarker(
                                                BitmapDescriptorFactory.HUE_RED
                                            )
                                        )
                                    )
                                    marker.tag = report.id
                                } else {
                                    marker = mMap.addMarker(
                                        MarkerOptions().position(position).title(report.nomeTipo)
                                            .snippet(report.descricao).icon(
                                            BitmapDescriptorFactory.defaultMarker(
                                                BitmapDescriptorFactory.HUE_BLUE
                                            )
                                        )
                                    )
                                    marker.tag = report.id
                                }


                            }
                        }
                    }

                    override fun onFailure(call: Call<List<Report>>, t: Throwable) {
                        Toast.makeText(this@ReportMapa, "${t.message}", Toast.LENGTH_SHORT).show()

                    }
                })

                true

            }
            R.id.terreno -> {
                mMap.clear()
                val request = ServiceBuilder.buildService(EndPoints::class.java)
                val call = request.getReportByTipo("Terreno")

                var position: LatLng
                //obter reports e criar markers
                call.enqueue(object : Callback<List<Report>> {
                    override fun onResponse(
                        call: Call<List<Report>>,
                        response: Response<List<Report>>
                    ) {

                        if (response.isSuccessful) {
                            if (response.body()!!.isEmpty()) {
                                Toast.makeText(
                                    this@ReportMapa,
                                    R.string.noReports,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            reports = response.body()!!
                            for (report in reports) {
                                position = LatLng(report.latitude, report.longitude)
                                val sharedPref: SharedPreferences = getSharedPreferences(
                                    getString(R.string.sharedPref), Context.MODE_PRIVATE
                                )

                                val user: Int = sharedPref.getInt(R.string.userlogged.toString(), 0)
                                if (report.utilizador_id != user) {
                                    marker = mMap.addMarker(
                                        MarkerOptions().position(position).title(report.nomeTipo)
                                            .snippet(report.descricao).icon(
                                            BitmapDescriptorFactory.defaultMarker(
                                                BitmapDescriptorFactory.HUE_RED
                                            )
                                        )
                                    )
                                    marker.tag = report.id
                                } else {
                                    marker = mMap.addMarker(
                                        MarkerOptions().position(position).title(report.nomeTipo)
                                            .snippet(report.descricao).icon(
                                            BitmapDescriptorFactory.defaultMarker(
                                                BitmapDescriptorFactory.HUE_BLUE
                                            )
                                        )
                                    )
                                    marker.tag = report.id
                                }


                            }
                        }
                    }

                    override fun onFailure(call: Call<List<Report>>, t: Throwable) {
                        Toast.makeText(this@ReportMapa, "${t.message}", Toast.LENGTH_SHORT).show()
                        Log.d("erro", "${t.message}")
                    }
                })
                true

            }
            R.id.acidente -> {
                mMap.clear()
                val request = ServiceBuilder.buildService(EndPoints::class.java)
                val call = request.getReportByTipo("Acidente")

                var position: LatLng
                //obter reports e criar markers
                call.enqueue(object : Callback<List<Report>> {
                    override fun onResponse(
                        call: Call<List<Report>>,
                        response: Response<List<Report>>
                    ) {

                        if (response.isSuccessful) {
                            if (response.body()!!.isEmpty()) {
                                Toast.makeText(
                                    this@ReportMapa,
                                    R.string.noReports,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            reports = response.body()!!
                            for (report in reports) {
                                position = LatLng(report.latitude, report.longitude)
                                val sharedPref: SharedPreferences = getSharedPreferences(
                                    getString(R.string.sharedPref), Context.MODE_PRIVATE
                                )

                                val user: Int = sharedPref.getInt(R.string.userlogged.toString(), 0)
                                if (report.utilizador_id != user) {
                                    marker = mMap.addMarker(
                                        MarkerOptions().position(position).title(report.nomeTipo)
                                            .snippet(report.descricao).icon(
                                            BitmapDescriptorFactory.defaultMarker(
                                                BitmapDescriptorFactory.HUE_RED
                                            )
                                        )
                                    )
                                    marker.tag = report.id
                                } else {
                                    marker = mMap.addMarker(
                                        MarkerOptions().position(position).title(report.nomeTipo)
                                            .snippet(report.descricao).icon(
                                            BitmapDescriptorFactory.defaultMarker(
                                                BitmapDescriptorFactory.HUE_BLUE
                                            )
                                        )
                                    )
                                    marker.tag = report.id
                                }


                            }
                        }
                    }

                    override fun onFailure(call: Call<List<Report>>, t: Throwable) {
                        Toast.makeText(this@ReportMapa, "${t.message}", Toast.LENGTH_SHORT).show()
                        Log.d("erro", "${t.message}")
                    }
                })
                true

            }
            R.id.outro -> {
                mMap.clear()
                val request = ServiceBuilder.buildService(EndPoints::class.java)
                val call = request.getReportByTipo("Outro")

                var position: LatLng
                //obter reports e criar markers
                call.enqueue(object : Callback<List<Report>> {
                    override fun onResponse(
                        call: Call<List<Report>>,
                        response: Response<List<Report>>
                    ) {

                        if (response.isSuccessful) {
                            if (response.body()!!.isEmpty()) {
                                Toast.makeText(
                                    this@ReportMapa,
                                    R.string.noReports,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            reports = response.body()!!
                            for (report in reports) {
                                position = LatLng(report.latitude, report.longitude)
                                val sharedPref: SharedPreferences = getSharedPreferences(
                                    getString(R.string.sharedPref), Context.MODE_PRIVATE
                                )

                                val user: Int = sharedPref.getInt(R.string.userlogged.toString(), 0)
                                if (report.utilizador_id != user) {
                                    marker = mMap.addMarker(
                                        MarkerOptions().position(position).title(report.nomeTipo)
                                            .snippet(report.descricao).icon(
                                            BitmapDescriptorFactory.defaultMarker(
                                                BitmapDescriptorFactory.HUE_RED
                                            )
                                        )
                                    )
                                    marker.tag = report.id
                                } else {
                                    marker = mMap.addMarker(
                                        MarkerOptions().position(position).title(report.nomeTipo)
                                            .snippet(report.descricao).icon(
                                            BitmapDescriptorFactory.defaultMarker(
                                                BitmapDescriptorFactory.HUE_BLUE
                                            )
                                        )
                                    )
                                    marker.tag = report.id
                                }


                            }
                        }
                    }

                    override fun onFailure(call: Call<List<Report>>, t: Throwable) {
                        Toast.makeText(this@ReportMapa, "${t.message}", Toast.LENGTH_SHORT).show()
                        Log.d("erro", "${t.message}")
                    }
                })
                true

            }

            R.id.logout -> {
                with(sharedPref.edit()) {
                    putString(ipvc.estg.streetreport.R.string.userlogged.toString(), null)
                    commit()
                }
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.notes -> {
                val intent = Intent(this, Notes::class.java)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun addOcorrencia(view: View) {
        val intent = Intent(this, AddReport::class.java).apply {
            putExtra(
                "localizacao",
                LatLng(lastLocation.latitude, lastLocation.longitude)
            )
        }
        startActivity(intent)
    }

}