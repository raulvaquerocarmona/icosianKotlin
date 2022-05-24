package es.vaquero.raul.icosiankotlin

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import com.google.maps.android.PolyUtil
import es.vaquero.raul.icosiankotlin.databinding.ActivityGenerarMarcadoresBinding
import es.vaquero.raul.icosiankotlin.databinding.ActivityMapaBinding
import org.json.JSONArray
import java.util.*


class MapaActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val REQUEST_CODE_AUTOCOMPLETE_FROM = 1
        private const val REQUEST_CODE_AUTOCOMPLETE_TO = 2
        private const val TAG = "MainActivity"
        lateinit var place : Place
        private lateinit var mFromLatLng: com.google.type.LatLng
        private var corde: Double = 0.0
        private var corde2: Double = 0.0
        private var nombre: String = ""
        private val db = FirebaseFirestore.getInstance()
        private val bdd = FirebaseDatabase.getInstance().getReference()
        lateinit var geoPoint: GeoPoint
        lateinit var location: com.google.type.LatLng
        private lateinit var mMap: GoogleMap
    }
    lateinit var binding4: ActivityMapaBinding
    private lateinit var map: GoogleMap
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_mapa)
        binding4 = ActivityMapaBinding.inflate(layoutInflater)
        setContentView(binding4.root)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(),
                getString(R.string.android_sdk_places_api_key),
                Locale.US);
        }

        binding4.btnForm.setOnClickListener {
            startAutoCompleteForm(REQUEST_CODE_AUTOCOMPLETE_FROM)
            Log.v("Tags", "Visualizar")
        }
        binding4.btnTo.setOnClickListener {
            startAutoCompleteForm(REQUEST_CODE_AUTOCOMPLETE_TO)
            Log.v("Tags", "Visualizar2")
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
         val cole = LatLng(41.49109224245079, 2.0396451346114075)
        googleMap.addMarker(
            MarkerOptions()
                .position(cole)
                .title("Almacen principal")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cole, 15f))

    }
    private fun startAutoCompleteForm(requestCode: Int) {
        val fields =
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)

        val intent =
            Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(this)

        Log.v("Tags", "Inicio")
        startActivityForResult(intent, requestCode)
        Log.v("Tags", "FINAL")
    }


    @SuppressLint("StringFormatInvalid")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE_FROM) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        place = Autocomplete.getPlaceFromIntent(data)
                        Log.i(TAG, "Place Name & Id: ${place.name}, ${place.id}")
                        Log.i(TAG, "Place Id: ${place.id}")
                        Log.i(TAG, "Place LatLng: ${place.latLng}")
                        // place.latLng.also { mFromLatLng = it }

                        binding4.tvFrom.text = getString(R.string.label_from, place.latLng)
                        corde = place.latLng.latitude
                        corde2 = place.latLng.longitude
                        nombre = place.name

                        Log.i(TAG, "LatLngitud:" + corde)
                        Log.i(TAG, "LatLngitud:" + corde2)
                        var hashMap : HashMap<String, Double>
                                = HashMap<String, Double> ()
                        hashMap.put("Latitud", corde)
                        hashMap.put("Longitud", corde2)

                        val originLocation = LatLng(corde, corde2)
                        mMap.addMarker(MarkerOptions().position(originLocation))
                        //val destinationLocation = LatLng(destinationLatitude, destinationLongitude)
                       // mMap.addMarker(MarkerOptions().position(destinationLocation))

                        // bdd.child("Puntos").child(nombre).push().setValue(hashMap)
                       // MapaActivity.db.collection("Puntos").document(MapaActivity.nombre).set(hashMap)
                        /*FirebaseDatabase.getInstance("https://database-b98c9-default-rtdb.firebaseio.com").reference.child("Ciudades")
                            .child(nombre).push().setValue(hashMap);*/

                        place.latLng?.let {
                            //mFromLatLng = it
                        }
                        //drawRoute(onActivityResult.steps)
                    }
                }
                AutocompleteActivity.RESULT_ERROR,
                -> {
                    // TODO: Handle the error.
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        status.statusMessage?.let { message -> Log.i(TAG, message) }
                    }
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
       /* this.map = map
        // Sample coordinates
        val latLngOrigin = LatLng(10.3181466, 123.9029382) // Ayala
        val latLngDestination = LatLng(10.311795, 123.915864) // SM City
        this.map!!.addMarker(MarkerOptions().position(latLngOrigin).title("Ayala"))
        this.map!!.addMarker(MarkerOptions().position(latLngDestination).title("SM City"))
        this.map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngOrigin, 14.5f))
        val path: MutableList<List<LatLng>> = ArrayList()
        val urlDirections =
            "https://maps.googleapis.com/maps/api/directions/json?origin=10.3181466,123.9029382&destination=10.311795,123.915864&key=AIzaSyDN_mUEzByNKUA9gcRgFrLDnv3qQNZutyE"
        val directionsRequest = object :
            StringRequest(Request.Method.GET, urlDirections, Response.Listener<String> { response ->
                val jsonResponse = JSONObject(response)
                // Get routes
                val routes = jsonResponse.getJSONArray("routes")
                val legs = routes.getJSONObject(0).getJSONArray("legs")
                val steps = legs.getJSONObject(0).getJSONArray("steps")
                for (i in 0 until steps.length()) {
                    val points =
                        steps.getJSONObject(i).getJSONObject("polyline").getString("points")
                    path.add(PolyUtil.decode(points))
                }
                for (i in 0 until path.size) {
                    this.map!!.addPolyline(PolylineOptions().addAll(path[i]).color(Color.RED))
                }
            }, Response.ErrorListener { _ ->
            }) {}
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(directionsRequest)*/
    }
