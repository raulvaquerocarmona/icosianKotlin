package es.vaquero.raul.icosiankotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import es.vaquero.raul.icosiankotlin.databinding.ActivityJefeBinding

lateinit var binding3 : ActivityJefeBinding
class JefeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding3 = ActivityJefeBinding.inflate(layoutInflater)
        setContentView(binding3.root)

        binding3.btnCrearMarcador.setOnClickListener(){
            cambiaJefe()
        }
    }

fun cambiaJefe(){
    val intent = Intent(this, Generar_marcadores::class.java)
    startActivity(intent)
}
}