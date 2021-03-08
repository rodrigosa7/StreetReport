package ipvc.estg.streetreport.adapter

import android.icu.text.MessageFormat.format
import android.text.format.DateFormat.format
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ipvc.estg.streetreport.R
import ipvc.estg.streetreport.dataclass.Note
import kotlinx.android.synthetic.main.recycler_line.view.*
import java.lang.String.format
import java.text.DateFormat
import java.text.MessageFormat.format

class LineAdapter(val list: ArrayList<Note>): RecyclerView.Adapter<LineViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineViewHolder {

        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.recycler_line, parent, false)
        return LineViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: LineViewHolder, position: Int) {
        val currentPlace = list[position]

        holder.name.text = currentPlace.name
        holder.desc.text = currentPlace.desc
        holder.data.text = DateFormat.getDateInstance().format(currentPlace.data)
    }

}

class LineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    val name = itemView.name
    val desc = itemView.desc
    val data = itemView.data

}