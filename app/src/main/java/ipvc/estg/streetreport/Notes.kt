package ipvc.estg.streetreport

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ipvc.estg.streetreport.adapter.NoteAdapter
import ipvc.estg.streetreport.entities.Note
import ipvc.estg.streetreport.viewmodel.NoteViewModel
import kotlinx.android.synthetic.main.activity_notes.*
import java.text.DateFormat
import java.util.*

class Notes : AppCompatActivity(), NoteAdapter.EnviarInfo {

    private lateinit var noteViewModel: NoteViewModel
    private val newWordActivityRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        //recycler_view
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val adapter = NoteAdapter(this, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        //view model
        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
        noteViewModel.allNotes.observe(this, Observer { notes ->
            notes?.let { adapter.setNotes(it) }
        })


    }


    fun addNote(view: View) {
        val intent = Intent(this, AddNote::class.java)
        startActivityForResult(intent, newWordActivityRequestCode)
    }

    override fun passarID(id: Int?) {
        noteViewModel.deleteByID(id)
        Toast.makeText(applicationContext, R.string.noteDeleted, Toast.LENGTH_LONG).show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newWordActivityRequestCode && resultCode == Activity.RESULT_OK) {
            val ptitulo = data?.getStringExtra(AddNote.EXTRA_REPLY_TITULO)
            val pdesc = data?.getStringExtra(AddNote.EXTRA_REPLY_DESC)

            if (ptitulo != null && pdesc != null) {
                val note = Note(
                    name = ptitulo,
                    desc = pdesc,
                    data = DateFormat.getDateInstance().format(Date())
                )
                noteViewModel.insert(note)

                Toast.makeText(applicationContext, R.string.inserted, Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(applicationContext, R.string.notinserted, Toast.LENGTH_LONG).show()
        }
    }
}
