package es.vaquero.raul.icosiankotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

import com.google.firebase.firestore.*
import es.vaquero.raul.icosiankotlin.databinding.ActivityJefeBinding

class JefeActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var bindingJefe : ActivityJefeBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var rutaArrayList: ArrayList<Rutes>
    private lateinit var myAdapter: MyAdapter
    private lateinit var db : FirebaseFirestore
    private lateinit var map: GoogleMap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingJefe = ActivityJefeBinding.inflate(layoutInflater)
        setContentView(bindingJefe.root)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        rutaArrayList = arrayListOf()

        myAdapter = MyAdapter(rutaArrayList)

        recyclerView.adapter = myAdapter

        EventChangeListener()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val cole = LatLng(41.49109224245079, 2.0396451346114075)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cole, 15f))
    }

    private fun EventChangeListener(){
        db = FirebaseFirestore.getInstance()
        db.collection("Rutas")
            .addSnapshotListener(object : EventListener<QuerySnapshot>{
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null){
                        return
                    }
                    for(dc : DocumentChange in value?.documentChanges!!){
                        if(dc.type == DocumentChange.Type.ADDED){
                            rutaArrayList.add(dc.document.toObject(Rutes::class.java))
                            getUserData(rutaArrayList)
                        }
                    }

                    myAdapter.notifyDataSetChanged()
                }
            })
    }


    fun getUserData(rutaArrayList: ArrayList<Rutes>){
        var adapter = MyAdapter(rutaArrayList)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : MyAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val intent = Intent(this@JefeActivity, CargaMapaActivity::class.java)
                intent.putExtra("CodigoRuta", rutaArrayList[position].codigoRuta)
                startActivity(intent)
            }
        })

    }





}