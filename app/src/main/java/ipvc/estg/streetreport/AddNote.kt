package ipvc.estg.streetreport

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText

class AddNote : AppCompatActivity() {

    private lateinit var editTitle: EditText
    private lateinit var editDesc: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        editTitle = findViewById(R.id.inputDescReport)
        editDesc = findViewById(R.id.inputDesc)
        val erro = getString(R.string.emptyField)
        val button = findViewById<Button>(R.id.confirmbtn)
        button.setOnClickListener{
            val replyIntent = Intent()
          when {
              TextUtils.isEmpty(editTitle.text) -> {
                editTitle.error = erro
                //setResult(Activity.RESULT_CANCELED, replyIntent)

              }
              TextUtils.isEmpty(editDesc.text) -> {
                editDesc.error = erro
              }
              else -> {
                replyIntent.putExtra(EXTRA_REPLY_TITULO, editTitle.text.toString())
                replyIntent.putExtra(EXTRA_REPLY_DESC, editDesc.text.toString())
                setResult(Activity.RESULT_OK, replyIntent)
                finish()
              }
          }

        }
    }
    companion object {
        const val EXTRA_REPLY_TITULO = "titulo"
        const val EXTRA_REPLY_DESC = "desc"

    }
}
