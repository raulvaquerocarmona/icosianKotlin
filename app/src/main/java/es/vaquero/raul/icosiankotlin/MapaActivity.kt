package es.vaquero.raul.icosiankotlin

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.TextView
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
import es.vaquero.raul.icosiankotlin.databinding.ActivityGenerarMarcadoresBinding
import es.vaquero.raul.icosiankotlin.databinding.ActivityMapaBinding
import org.json.JSONArray
import java.util.*


class MapaActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val REQUEST_CODE_AUTOCOMPLETE_FROM = 1
        private const val REQUEST_CODE_AUTOCOMPLETE_TO = 2
        private const val REQUEST_CODE_AUTOCOMPLETE_WAY = 3
        private const val TAG = "MainActivity"
        lateinit var place: Place
        private lateinit var mFromLatLng: com.google.type.LatLng
        private var corde: Double = 0.0
        private var corde2: Double = 0.0
        private var nombre: String = ""
        private val db = FirebaseFirestore.getInstance()
        private val bdd = FirebaseDatabase.getInstance().getReference()
        lateinit var geoPoint: GeoPoint
        lateinit var location: com.google.type.LatLng
        private lateinit var mMap: GoogleMap
        var mLatLng: LatLng = LatLng(2.88,2.77)
        var ruta: Int = 1
        var extraPoints: Int = 2

    }

    lateinit var binding4: ActivityMapaBinding
    private lateinit var map: GoogleMap
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_mapa)
        binding4 = ActivityMapaBinding.inflate(layoutInflater)
        setContentView(binding4.root)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(),
                getString(R.string.android_sdk_places_api_key),
                Locale.US);
        }


        binding4.btnOrigen.setOnClickListener {
            startAutoCompleteForm(REQUEST_CODE_AUTOCOMPLETE_FROM)
            binding4.btnOrigen.isEnabled = false
            Log.v("Tags", "Visualizar")
        }
        binding4.btnFinal.setOnClickListener {
            startAutoCompleteForm(REQUEST_CODE_AUTOCOMPLETE_TO)
            binding4.btnFinal.isEnabled = false
            Log.v("Tags", "Visualizar2")
        }

        binding4.btnWaypoints.setOnClickListener {
            if (extraPoints>0){
                startAutoCompleteForm(REQUEST_CODE_AUTOCOMPLETE_WAY)
                extraPoints--
                if(extraPoints==0){
                    binding4.btnWaypoints.isEnabled = false
                }
            }
        }

        binding4.btnCrearRuta.setOnClickListener {
            binding4.btnOrigen.isEnabled = true
            binding4.btnFinal.isEnabled = true
            binding4.btnWaypoints.isEnabled = true
            extraPoints = 2
            ruta++
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        val cole = LatLng(41.49109224245079, 2.0396451346114075)
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

                        binding4.tvFrom.text = getString(R.string.label_from, place.latLng.toString())
                        corde = place.latLng.latitude
                        corde2 = place.latLng.longitude
                        nombre = place.name
                        mLatLng = LatLng(corde, corde2)

                        var hashMap: HashMap<String, Double> = HashMap<String, Double>()
                        hashMap.put("Latitud", corde)
                        hashMap.put("Longitud", corde2)


                        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                        mapFragment.getMapAsync(this)

                        mapFragment.getMapAsync{
                            map = it
                            val destinationLocation = LatLng(corde, corde2)
                            map.addMarker(MarkerOptions()
                                .position(destinationLocation)
                                .title("Origen")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLocation, 15f))
                        }

                        db.collection("Ruta"+ruta).document("origen").set(hashMap)

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
        }else if (requestCode == REQUEST_CODE_AUTOCOMPLETE_TO){
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        place = Autocomplete.getPlaceFromIntent(data)

                        binding4.tvFrom.text = getString(R.string.label_from, place.latLng.toString())
                        corde = place.latLng.latitude
                        corde2 = place.latLng.longitude
                        nombre = place.name
                        mLatLng = LatLng(corde, corde2)

                        var hashMap: HashMap<String, Double> = HashMap<String, Double>()
                        hashMap.put("Latitud", corde)
                        hashMap.put("Longitud", corde2)


                        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                        mapFragment.getMapAsync(this)

                        mapFragment.getMapAsync{
                            map = it
                            val destinationLocation = LatLng(corde, corde2)
                            map.addMarker(MarkerOptions()
                                .position(destinationLocation)
                                .title("Final")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLocation, 15f))
                        }

                        db.collection("Ruta"+ruta).document("final").set(hashMap)

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
        }else if(requestCode == REQUEST_CODE_AUTOCOMPLETE_WAY){
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        place = Autocomplete.getPlaceFromIntent(data)

                        binding4.tvFrom.text = getString(R.string.label_from, place.latLng)
                        corde = place.latLng.latitude
                        corde2 = place.latLng.longitude
                        nombre = place.name
                        mLatLng = LatLng(corde, corde2)

                        var hashMap: HashMap<String, Double> = HashMap<String, Double>()
                        hashMap.put("Latitud", corde)
                        hashMap.put("Longitud", corde2)


                        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                        mapFragment.getMapAsync(this)

                        mapFragment.getMapAsync{
                            map = it
                            val destinationLocation = LatLng(corde, corde2)
                            map.addMarker(MarkerOptions()
                                .position(destinationLocation)
                                .title("Final")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)))
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLocation, 15f))
                        }

                        db.collection("Ruta"+ruta).document("waypoint").set(hashMap)

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
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}

