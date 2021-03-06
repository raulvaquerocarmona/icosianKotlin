package es.vaquero.raul.icosiankotlin

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import es.vaquero.raul.icosiankotlin.databinding.ActivityMapaBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.json.JSONTokener
import java.util.*
import kotlin.collections.HashMap


class MapaActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val REQUEST_CODE_AUTOCOMPLETE_FROM = 1
        private const val REQUEST_CODE_AUTOCOMPLETE_TO = 2
        private const val REQUEST_CODE_AUTOCOMPLETE_WAY = 3
        private const val TAG = "MainActivity"
        lateinit var place: Place
        private var corde: Double = 0.0
        private var corde2: Double = 0.0
        private var nombre: String = ""
        private val db = FirebaseFirestore.getInstance()
        private val bdd = FirebaseDatabase.getInstance().getReference()
        var ruta: Int = 1
        var extraPoints: Int = 5
        var numWaypoint: Int = 1
        var oriCord: LatLng = LatLng(2.0, 2.0)
        lateinit var finCord: LatLng
        var origenMap: HashMap<String, Double> = HashMap()
        var finalMap: HashMap<String, Double> = HashMap()
        var wayMap: HashMap<String, String> = HashMap()
    }

    lateinit var binding4: ActivityMapaBinding
    private lateinit var map: GoogleMap
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        }
        binding4.btnFinal.setOnClickListener {
            startAutoCompleteForm(REQUEST_CODE_AUTOCOMPLETE_TO)
            binding4.btnFinal.isEnabled = false
        }

        binding4.btnWaypoints.setOnClickListener {
            if (extraPoints>0){
                startAutoCompleteForm(REQUEST_CODE_AUTOCOMPLETE_WAY)
                db.collection("Ruta"+ruta).document("waypoints").set(wayMap)
                extraPoints--
                if(extraPoints==0){
                    binding4.btnWaypoints.isEnabled = false
                }
            }
        }

        binding4.btnCrearRuta.setOnClickListener {
            mapFragment.getMapAsync {
                map = it
                var urlMapa = getDirectionURL()
                GetDirection1(urlMapa).execute()
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(oriCord, 14F))
                extraPoints = 5
                ruta++
            }
            binding4.btnOrigen.isEnabled = true
            binding4.btnFinal.isEnabled = true
            binding4.btnWaypoints.isEnabled = true

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

        startActivityForResult(intent, requestCode)
    }


    @SuppressLint("StringFormatInvalid")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE_FROM) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        place = Autocomplete.getPlaceFromIntent(data)

                        //binding4.tvFrom.text = getString(R.string.label_from, place.latLng.toString())
                        corde = place.latLng.latitude
                        corde2 = place.latLng.longitude
                        nombre = place.name

                        oriCord = LatLng(corde, corde2)

                        /*
                        var hashMap: HashMap<String, Double> = HashMap()
                        hashMap.put("Latitud", corde)
                        hashMap.put("Longitud", corde2)
                         */
                        origenMap.put("Latitud", corde)
                        origenMap.put("Longitud", corde2)

                        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                        mapFragment.getMapAsync(this)

                        mapFragment.getMapAsync{
                            map = it
                            val destinationLocation = LatLng(corde, corde2)
                            map.clear()
                            map.addMarker(MarkerOptions()
                                .position(destinationLocation)
                                .title("Origen")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLocation, 15f))
                        }
                        db.collection("Ruta"+ruta).document("origen").set(origenMap)
                    }
                }
                AutocompleteActivity.RESULT_ERROR,
                -> {
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

                        //binding4.tvFrom.text = getString(R.string.label_from, place.latLng.toString())
                        corde = place.latLng.latitude
                        corde2 = place.latLng.longitude
                        nombre = place.name

                        finCord = LatLng(corde, corde2)

                        /*
                        var hashMap: HashMap<String, Double> = HashMap()
                        hashMap.put("Latitud", corde)
                        hashMap.put("Longitud", corde2)
                         */
                        finalMap.put("Latitud", corde)
                        finalMap.put("Longitud", corde2)

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
                        db.collection("Ruta"+ruta).document("final").set(finalMap)
                    }
                }
                AutocompleteActivity.RESULT_ERROR,
                -> {
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

                       // binding4.tvFrom.text = getString(R.string.label_from, place.latLng)
                        corde = place.latLng.latitude
                        corde2 = place.latLng.longitude
                        nombre = place.name

                        /*
                        var hashMap: HashMap<String, Double> = HashMap()
                        hashMap.put("Latitud", corde)
                        hashMap.put("Longitud", corde2)

                         */

                        var palMap = ""
                        palMap = corde.toString() + "," + corde2

                        wayMap.put("Marker"+ numWaypoint, palMap)
                        numWaypoint++

                        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                        mapFragment.getMapAsync(this)

                        mapFragment.getMapAsync{
                            map = it
                            val destinationLocation = LatLng(corde, corde2)
                            map.addMarker(MarkerOptions()
                                .position(destinationLocation)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)))
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLocation, 15f))
                        }
                    }
                }
                AutocompleteActivity.RESULT_ERROR,
                -> {
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        status.statusMessage?.let { message -> Log.i(TAG, message) }
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun getDirectionURL() : String{
        var wayPoints = ""
        wayMap.forEach { (i, value) ->  wayPoints += "|" + value}
        db.collection("Ruta"+ruta).document("origen").get().addOnSuccessListener {
             var origenLatitud = it.get("Latitud") as Double
             var origenLongitud = it.get("Longitud") as Double
        }
        db.collection("Ruta"+ruta).document("final").get().addOnSuccessListener {
             var finalLatitud = it.get("Latitud") as Double
             var finalLongitud = it.get("Longitud") as Double
        }

        /*
        for (i in 1..numWaypoint){
            db.collection("Ruta"+ruta).document("waypoints").get().addOnSuccessListener {
                wayPoints += "|" + it.get("Marker"+i) as String
                println("Punticos = " + wayPoints)
            }
        }
         */
        numWaypoint = 1

            if(extraPoints == 5){
                return "https://maps.googleapis.com/maps/api/directions/json?" +
                        "&origin=" + oriCord.latitude + "," + oriCord.longitude +
                        "&destination="+ finCord.latitude + "," + finCord.longitude +
                        "&key=AIzaSyCafMUo4i93krYGQ3iaV0qOk3GuxyMjUrA"
            }else{
                return "https://maps.googleapis.com/maps/api/directions/json?" +
                        "&origin=" + oriCord.latitude + "," + oriCord.longitude +
                        "&destination="+ finCord.latitude + "," + finCord.longitude +
                        "&waypoints=optimize:true" + wayPoints +
                        "&key=AIzaSyCafMUo4i93krYGQ3iaV0qOk3GuxyMjUrA"
            }
    }


    @SuppressLint("StaticFieldLeak")
    private inner class GetDirection1(val url: String) : AsyncTask<Void, Void, List<List<LatLng>>>() {
        override fun doInBackground(vararg p0: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body!!.string()

            val result =  ArrayList<List<LatLng>>()

            val jsonObject = JSONTokener(data).nextValue() as JSONObject
            val jsonArray = jsonObject.getJSONArray("routes")
            val path =  ArrayList<LatLng>()
            var rutaP = ""
            for (i in 0 until jsonArray.length()) {
                val polyline = jsonArray.getJSONObject(i).getJSONObject("overview_polyline")
                rutaP = polyline.getString("points")
                path.addAll(decodePolyline(rutaP))
            }

            var hashMap: HashMap<String, String> = HashMap()
            hashMap.put("Ruta", (ruta-1).toString())
            hashMap.put("codigoRuta", rutaP)

            db.collection("Rutas").document().set(hashMap)
            result.add(path)

            return result
        }

        override fun onPostExecute(result: List<List<LatLng>>) {
            val lineoption = PolylineOptions()
            for (i in result.indices){
                lineoption.addAll(result[i])
                lineoption.width(10f)
                lineoption.color(Color.GREEN)
                lineoption.geodesic(true)
            }
            map.addPolyline(lineoption)
        }
    }

    fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val latLng = LatLng((lat.toDouble() / 1E5),(lng.toDouble() / 1E5))
            poly.add(latLng)
        }
        return poly
    }
}

