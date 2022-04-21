package es.vaquero.raul.icosiankotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import es.vaquero.raul.icosiankotlin.databinding.ActivitySeleccionBinding


lateinit var binding2 : ActivitySeleccionBinding
class pantallaSeleccion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding2 = ActivitySeleccionBinding.inflate(layoutInflater)
        setContentView(binding2.root)

        binding2.button3.setOnClickListener(){
            cambiaMapa()
            print("He sido ejecutado!!!")
        }
    }

    fun cambiaMapa(){
        val intent = Intent(this, MapaActivity::class.java)
        startActivity(intent)
        print("He funcionado!!")
    }
}