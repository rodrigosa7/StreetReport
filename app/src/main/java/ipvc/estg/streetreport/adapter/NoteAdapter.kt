package ipvc.estg.streetreport.adapter

import android.content.Context
import android.content.Intent
import android.os.Build.ID
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ipvc.estg.streetreport.EditNote
import ipvc.estg.streetreport.R
import ipvc.estg.streetreport.entities.Note
import java.text.DateFormat

const val TITULO = "NAME"
const val DESC = "DESC"
const val ID = "ID"
class NoteAdapter internal constructor(context: Context): RecyclerView.Adapter<NoteAdapter.NoteViewHolder>(){
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var notes = emptyList<Note>()



    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val noteTitle: TextView = itemView.findViewById(R.id.name)
        val noteDesc: TextView = itemView.findViewById(R.id.desc)
        val noteData: TextView = itemView.findViewById(R.id.data)

        val editbtn: Button = itemView.findViewById(R.id.edit)
        val removebtn: Button = itemView.findViewById(R.id.remove)
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
        var id = current.id


        holder.editbtn.setOnClickListener{
            val context=holder.noteTitle.context
            val tit= holder.noteTitle.text.toString()
            val descricao= holder.noteDesc.text.toString()

            val intent = Intent( context, EditNote::class.java).apply {
                putExtra(TITULO, tit )
                putExtra(DESC, descricao )
                putExtra( ID,id)
            }
            context.startActivity(intent)
        }





    }

    internal fun setNotes(notes: List<Note>){
        this.notes = notes
        notifyDataSetChanged()
    }

    override fun getItemCount() = notes.size

}