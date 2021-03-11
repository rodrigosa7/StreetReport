package ipvc.estg.streetreport.adapter

import android.content.Context
import android.content.Intent
import android.media.Image
import android.os.Build.ID
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import ipvc.estg.streetreport.EditNote
import ipvc.estg.streetreport.R
import ipvc.estg.streetreport.entities.Note
import ipvc.estg.streetreport.viewmodel.NoteViewModel


const val TITULO = "NAME"
const val DESC = "DESC"
const val ID = "ID"



class NoteAdapter internal constructor(context: Context, private val interID:EnviarInfo): RecyclerView.Adapter<NoteAdapter.NoteViewHolder>(){
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var notes = emptyList<Note>()

    interface EnviarInfo {
        fun passarID(id: Int?)
    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val noteTitle: TextView = itemView.findViewById(R.id.name)
        val noteDesc: TextView = itemView.findViewById(R.id.desc)
        val noteData: TextView = itemView.findViewById(R.id.data)

        val editbtn: ImageButton = itemView.findViewById(R.id.edit)
        val removebtn: ImageButton = itemView.findViewById(R.id.remove)
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
        val id = current.id


        holder.editbtn.setOnClickListener{
            val context=holder.noteTitle.context
            val name= holder.noteTitle.text.toString()
            val desc= holder.noteDesc.text.toString()

            val intent = Intent( context, EditNote::class.java).apply {
                putExtra(TITULO, name )
                putExtra(DESC, desc )
                putExtra( ID,id)
            }
            context.startActivity(intent)
        }

        holder.removebtn.setOnClickListener {
            val name = holder.noteTitle.text.toString()
            interID.passarID(id)
        }





    }



    internal fun setNotes(notes: List<Note>){
        this.notes = notes
        notifyDataSetChanged()
    }

    override fun getItemCount() = notes.size

}