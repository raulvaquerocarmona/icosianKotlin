package es.vaquero.raul.icosiankotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import es.vaquero.raul.icosiankotlin.databinding.ActivityMainBinding


private val db = FirebaseFirestore.getInstance()
// Create a reference to the cities collection
//private val citiesRef = db.collection("users")

// Create a query against the collection.
//private val query = citiesRef.whereEqualTo("rango", "1");
lateinit var binding : ActivityMainBinding
class MainActivity : AppCompatActivity() {

    private var email : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setup()
    }

    enum class ProviderType{
        BASIC
    }
    private fun setup() {
        //canviem el títol de l'aplicació
        title = "Autenticació"
        //clic al botó inscriu-te
        binding.button.setOnClickListener {
            if (binding.editTextTextEmailAddress.text.isNotEmpty() && binding.editTextTextPassword.text.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding.editTextTextEmailAddress.text.toString(),
                    binding.editTextTextPassword.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        //showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                       /* email = binding.editTextTextEmailAddress.toString()
                        db.collection("Autenticacion").document(email).set(0)*/
                    } else {
                        showAlert()
                    }
                }
            }

        }
        //clic al botó Accedir
        binding.button2.setOnClickListener {
            if (binding.editTextTextEmailAddress.text.isNotEmpty() && binding.editTextTextPassword.text.isNotEmpty() /*&& query.equals(0)*/) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    binding.editTextTextEmailAddress.text.toString(),
                    binding.editTextTextPassword.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                    } else {
                        showAlert()
                    }
                }
            }

        }
    }

    //falla la autenticació d'usuaris
    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("S'ha produït un error autenticant l'usuari!")
        builder.setPositiveButton("Acceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


    //l'autenticació funciona correctament
    private fun showHome(email: String, provider: ProviderType){
        val homeIntent = Intent(this, pantallaSeleccion::class.java).apply{
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }
}