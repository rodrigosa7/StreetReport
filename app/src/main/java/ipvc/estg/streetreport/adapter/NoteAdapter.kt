package ipvc.estg.streetreport.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ipvc.estg.streetreport.R
import ipvc.estg.streetreport.entities.Note
import java.text.DateFormat

class NoteAdapter internal constructor(context: Context): RecyclerView.Adapter<NoteAdapter.NoteViewHolder>(){
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var notes = emptyList<Note>() // Cached copy of cities

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val noteTitle: TextView = itemView.findViewById(R.id.name)
        val noteDesc: TextView = itemView.findViewById(R.id.desc)
        val noteData: TextView = itemView.findViewById(R.id.data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {

        val itemView = inflater.inflate(R.layout.recycler_line, parent, false)
        return NoteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val current = notes[position]

        holder.noteTitle.text = current.name
        holder.noteDesc.text = current.desc
        holder.noteData.text = current.data

    }

    internal fun setNotes(notes: List<Note>){
        this.notes = notes
        notifyDataSetChanged()
    }
    override fun getItemCount() = notes.size

}