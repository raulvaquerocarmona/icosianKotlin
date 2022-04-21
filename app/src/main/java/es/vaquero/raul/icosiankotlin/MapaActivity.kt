package es.vaquero.raul.icosiankotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapaActivity : AppCompatActivity(), OnMapReadyCallback{

    private lateinit var map:GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync { googleMap ->
            map = googleMap
        }
    }

    /*
    private fun createFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    */


    override fun onMapReady(googleMap: GoogleMap) {
        createMarker()
        map = googleMap
        createMarker()
    }

    private fun createMarker() {
        val coordinates = LatLng(41.49247797006594, 2.0345114115255423)
        val marker = MarkerOptions().position(coordinates).title("ALMACEN 1")
        map.addMarker(marker)
    }
}