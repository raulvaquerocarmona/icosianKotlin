
package es.vaquero.raul.icosiankotlin


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import es.vaquero.raul.icosiankotlin.databinding.ActivitySeleccionBinding


lateinit var binding2 : ActivitySeleccionBinding
private val db = FirebaseFirestore.getInstance()
class pantallaSeleccion : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding2 = ActivitySeleccionBinding.inflate(layoutInflater)
        setContentView(binding2.root)

        val email :String = binding.editTextTextEmailAddress.getText().toString()
            db.collection("Datos usuarios").document(email).get().addOnSuccessListener {
                binding2.textView2.setText(it.get("Nombre") as String?)
                var num = it.get("Permisos") as Long
                println("aqui "+num.toInt())
                if(num.toInt() == 0){
                    binding2.btnCrear.isEnabled = false
                    binding2.btnCargar.isEnabled = true
                }
                else{
                    binding2.btnCrear.isEnabled = true
                    binding2.btnCargar.isEnabled = true
                }
            }




        binding2.btnCrear.setOnClickListener(){
            cambiaMapa()
        }

        binding2.btnCargar.setOnClickListener(){
            cambiaCarga()
        }
    }



    fun cambiaMapa(){
        val intent = Intent(this, MapaActivity::class.java)
        startActivity(intent)
    }

    fun cambiaCarga(){
        val intent = Intent(this, JefeActivity::class.java)
        startActivity(intent)
    }
}