package ipvc.estg.streetreport

import android.app.Activity
import android.content.Intent
import android.os.Build.ID
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.text.TextUtils
import android.view.View
import android.widget.EditText
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


        findViewById<EditText>(R.id.inputName).setText(nome)
        findViewById<EditText>(R.id.inputDesc).setText(desc)

        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)

    }

    fun editarNota(view: View) {
        noteTitle = findViewById(R.id.inputName)
        noteDesc = findViewById(R.id.inputDesc)
        var message3 = intent.getIntExtra(ID, 0)
        val replyIntent = Intent()
        if (TextUtils.isEmpty(noteTitle.text) || TextUtils.isEmpty(noteDesc.text))  {
            setResult(Activity.RESULT_CANCELED, replyIntent)
        } else {
            val nota = Note(id = message3, name = noteTitle.text.toString(), desc = noteDesc.text.toString(), data = DateFormat.getDateInstance().format(
                Date()))
            noteViewModel.editNote(nota)

        }
        finish()

    }

}