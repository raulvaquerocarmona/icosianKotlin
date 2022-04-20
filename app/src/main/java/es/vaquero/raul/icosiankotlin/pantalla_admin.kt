package es.vaquero.raul.icosiankotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth
import es.vaquero.raul.icosiankotlin.databinding.ActivityPantallaAdminBinding

lateinit var binding2: ActivityPantallaAdminBinding
class pantalla_admin : AppCompatActivity() {

    inline fun delay(delay: Long, crossinline completion: () -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed({
            completion()
        }, delay)
    }
    enum class ProviderType {
        BASIC
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding2 = ActivityPantallaAdminBinding.inflate(layoutInflater)
        setContentView(binding2.root)

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
    }

    private fun setup(email: String, provider: String) {
        binding2.button3.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            //tornem a la pantalla d'inici
            onBackPressed()
        }
    }
}