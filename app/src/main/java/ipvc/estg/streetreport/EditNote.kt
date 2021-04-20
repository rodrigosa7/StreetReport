package ipvc.estg.streetreport

import android.content.Intent
import android.os.Build.ID
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import ipvc.estg.streetreport.adapter.DESC
import ipvc.estg.streetreport.adapter.TITULO
import ipvc.estg.streetreport.entities.Note
import ipvc.estg.streetreport.viewmodel.NoteViewModel
import java.text.DateFormat
import java.util.*

class EditNote : AppCompatActivity() {

    private lateinit var noteTitle: EditText
    private lateinit var noteDesc: EditText
    private lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        val nome = intent.getStringExtra(TITULO)
        val desc = intent.getStringExtra(DESC)

        val replyIntent = Intent()
        findViewById<EditText>(R.id.inputDescReport).setText(nome)
        findViewById<EditText>(R.id.inputDesc).setText(desc)

        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)

    }

    fun editarNota(view: View) {
      val erro:String = getString(R.string.emptyField)
        noteTitle = findViewById(R.id.inputDescReport)
        noteDesc = findViewById(R.id.inputDesc)
        var message3 = intent.getIntExtra(ID, 0)
        val replyIntent = Intent()
      when {
        TextUtils.isEmpty(noteTitle.text) -> {
          noteTitle.error = erro
          //setResult(Activity.RESULT_CANCELED, replyIntent)

        }
        TextUtils.isEmpty(noteDesc.text) -> {
          noteDesc.error = erro
        }
        else -> {
          val nota = Note(id = message3, name = noteTitle.text.toString(), desc = noteDesc.text.toString(), data = DateFormat.getDateInstance().format(
            Date()))
          noteViewModel.editNote(nota)
          Toast.makeText(applicationContext, R.string.noteUpdated, Toast.LENGTH_LONG).show()
          finish()
        }
      }
    }

}
