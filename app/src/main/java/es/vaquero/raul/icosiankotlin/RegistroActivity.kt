package es.vaquero.raul.icosiankotlin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import es.vaquero.raul.icosiankotlin.databinding.ActivityResgistroBinding

@SuppressLint("StaticFieldLeak")
private val db = FirebaseFirestore.getInstance()

class RegistroActivity : AppCompatActivity() {
    lateinit var binding11 : ActivityResgistroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding11 = ActivityResgistroBinding.inflate(layoutInflater)
        setContentView(binding11.root)

        binding11.alreadyHaveAccount.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@RegistroActivity,
                MainActivity::class.java))
        })
        setup()
    }

    private fun setup() {
        //canviem el títol de l'aplicació
        title = "Autenticació"
        //clic al botó inscriu-te
        binding11.btnRegister.setOnClickListener {
            val nombre: String = binding11.inputUsername.getText().toString()
            val email: String = binding11.inputEmail.getText().toString()
            val password: String = binding11.inputPassword.getText().toString()
            val confirmPass: String = binding11.inputConfirmPassword.getText().toString()
            if (nombre.isEmpty() || nombre.length < 2) {
                showError(binding11.inputUsername, "Nombre no valido")
            } else if (email.isEmpty() || !email.contains("@")) {
                showError(binding11.inputEmail, "Email no valido")
            } else if (password.isEmpty() || password.length < 7) {
                showError(binding11.inputPassword, "Clave no valida minimo 7 caracteres")
            } else if (confirmPass.isEmpty() || confirmPass != password) {
                showError(binding11.inputConfirmPassword, "Clave no valida, no coincide.")
            } else {
                val numero = 0
                db.collection("Datos usuarios")
                    .document(binding11.inputEmail.text.toString()).set(
                    hashMapOf("Nombre" to nombre,
                        "Permisos" to numero)
                )

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding11.inputEmail.text.toString(),
                    binding11.inputPassword.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showHome(it.result?.user?.email ?: "", MainActivity.ProviderType.BASIC)
                    } else {
                        showAlert()
                    }
                }
            }
        }
    }



    fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Usuario no registrado")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    //l'autenticació funciona correctament
    fun showHome(email: String, provider: MainActivity.ProviderType){
        val homeIntent = Intent(this, MainActivity::class.java).apply{
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }
    fun showError(input: EditText, s: String) {
        input.error = s
        input.requestFocus()
    }

}