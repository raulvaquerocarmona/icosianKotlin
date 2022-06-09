package es.vaquero.raul.icosiankotlin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView



class MyAdapter(private val rutaList : ArrayList<Rutes>): RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(Position: Int)
    }


    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item,
        parent, false)
        return MyViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: MyAdapter.MyViewHolder, position: Int) {
        val ruta : Rutes = rutaList[position]
        holder.Ruta.text = ruta.Ruta
        holder.codigoRuta.text = ruta.codigoRuta
    }

    override fun getItemCount(): Int {
       return rutaList.size
    }

    public class MyViewHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView){
        val Ruta : TextView = itemView.findViewById(R.id.tvRuta)
        val codigoRuta : TextView = itemView.findViewById(R.id.tvCodRuta)
        init {
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }

    }
}