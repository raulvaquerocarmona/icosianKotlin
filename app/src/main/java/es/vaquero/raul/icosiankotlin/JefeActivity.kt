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
            //cambiaJefe()
        }
    }

    /*fun alertaAdmin(uid) {
        var uid = firebase.auth().currentUser.uid
        console.log("function alertaAdmin activada")
        if (uid == "P4vQc3iYeJZqJzVUC2qfsQzkc9") {
            console.log("El usuario es administrador")
            funcionesExclusivasAdministrador()
        } else {
            console.log("El usuario es usuario común")
        }
    }*/
/*
fun cambiaJefe(){
    val intent = Intent(this, Generar_marcadores::class.java)
    startActivity(intent)
}
 */
}