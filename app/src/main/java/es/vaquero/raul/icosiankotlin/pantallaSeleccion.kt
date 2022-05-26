package es.vaquero.raul.icosiankotlin

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import es.vaquero.raul.icosiankotlin.databinding.ActivitySeleccionBinding


lateinit var binding2 : ActivitySeleccionBinding
class pantallaSeleccion : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding2 = ActivitySeleccionBinding.inflate(layoutInflater)
        setContentView(binding2.root)


        binding2.btnCrear.setOnClickListener(){
            cambiaJefe()
        }

        binding2.btnCargar.setOnClickListener(){
            cambiaMapa()
        }
    }



    fun cambiaMapa(){
        val intent = Intent(this, MapaActivity::class.java)
        startActivity(intent)
    }

    fun cambiaJefe(){
        val intent = Intent(this, JefeActivity::class.java)
        startActivity(intent)
    }
}