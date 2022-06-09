package es.vaquero.raul.icosiankotlin

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import es.vaquero.raul.icosiankotlin.databinding.ActivityMainBinding


private val db = FirebaseFirestore.getInstance()
// Create a reference to the cities collection
//private val citiesRef = db.collection("users")
// Create a query against the collection.
//private val query = citiesRef.whereEqualTo("rango", "1");
@SuppressLint("StaticFieldLeak")
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
        binding.button.setOnClickListener(View.OnClickListener {
                val intent = Intent(this, RegistroActivity::class.java)
                startActivity(intent)

        })

        //clic al botó Accedir
        binding.button2.setOnClickListener {
            val email: String = binding.editTextTextEmailAddress.getText().toString()
            val password: String = binding.editTextTextPassword.getText().toString()
            if (email.isEmpty() || !email.contains("@")) {
                showError(binding.editTextTextEmailAddress, "Email no valido")
            } else if (password.isEmpty() || password.length < 7) {
                showError(binding.editTextTextPassword, "Password invalida")
            }
            else {
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
        builder.setMessage("Usuario no registrado")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showError(input: EditText, s: String) {
        input.error = s
        input.requestFocus()
    }

    fun verificarCredenciales() {
        val email: String = binding.editTextTextEmailAddress.getText().toString()
        val password: String = binding.editTextTextPassword.getText().toString()
        if (email.isEmpty() || !email.contains("@")) {
            showError(binding.editTextTextEmailAddress, "Email no valido")
        } else if (password.isEmpty() || password.length < 7) {
            showError(binding.editTextTextPassword, "Password invalida")
        } else {
            //Mostrar ProgressBar
            val mProgressBar = ProgressDialog(this@MainActivity)
            mProgressBar.setTitle("Login")
            mProgressBar.setMessage("Iniciando sesión, espere un momento..")
            mProgressBar.setCanceledOnTouchOutside(false)
            mProgressBar.show()
            //Registrar usuario
            //Exitoso -> Mostrar toast
            //redireccionar - intent a login
            val intent = Intent(this@MainActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            //ocultar progressBar
            mProgressBar.dismiss()
        }
    }

    //l'autenticació funciona correctament
    private fun showHome(email: String, provider: MainActivity.ProviderType){
        val homeIntent = Intent(this, pantallaSeleccion::class.java).apply{
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }
}