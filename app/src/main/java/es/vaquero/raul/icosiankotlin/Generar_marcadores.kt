package es.vaquero.raul.icosiankotlin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.type.LatLng
import es.vaquero.raul.icosiankotlin.databinding.ActivityGenerarMarcadoresBinding
import java.util.*


class Generar_marcadores : AppCompatActivity() {

    lateinit var binding4: ActivityGenerarMarcadoresBinding

    companion object {
        private const val REQUEST_CODE_AUTOCOMPLETE_FROM = 1
        private const val REQUEST_CODE_AUTOCOMPLETE_TO = 2
        private const val TAG = "MainActivity"
        lateinit var place : Place
        private lateinit var mFromLatLng: LatLng
        private var corde: Double = 0.0
        private var corde2: Double = 0.0
        private val db = FirebaseFirestore.getInstance()
        private val bdd = FirebaseDatabase.getInstance().getReference()
        lateinit var geoPoint: GeoPoint
        lateinit var location: LatLng
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding4 = ActivityGenerarMarcadoresBinding.inflate(layoutInflater)
        setContentView(binding4.root)

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

        // GeoPoint geoPoint = document.getGeoPoint("position");


    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_AUTOCOMPLETE_FROM){

        }else if(requestCode == REQUEST_CODE_AUTOCOMPLETE_TO){

        }
    }*/

    private fun startAutoCompleteForm(requestCode: Int) {
        val fields =
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)

        val intent =
            Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(this)

        Log.v("Tags", "Inicio")
        startActivityForResult(intent, requestCode)
        Log.v("Tags", "FINAL")
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE_FROM) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                    place =Autocomplete.getPlaceFromIntent(data)
                        Log.i(TAG, "Place Name & Id: ${place.name}, ${place.id}")
                        Log.i(TAG, "Place Id: ${place.id}")
                        Log.i(TAG, "Place LatLng: ${place.latLng}")
                        // place.latLng.also { mFromLatLng = it }

                        binding4.tvFrom.text = getString(R.string.label_from, place.latLng)
                        corde = place.latLng.latitude
                        corde2 = place.latLng.longitude
                        Log.i(TAG, "LatLngitud:" + corde)
                        Log.i(TAG, "LatLngitud:" + corde2)
                        var hashMap : HashMap<String, Double>
                                = HashMap<String, Double> ()
                        hashMap.put("Latitud", corde)
                        hashMap.put("Longitud", corde2)

                        bdd.child("Puntos").push().setValue(hashMap)
                        place.latLng?.let {
                             //mFromLatLng = it
                        }
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


}


