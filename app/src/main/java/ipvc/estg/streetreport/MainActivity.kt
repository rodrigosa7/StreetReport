package ipvc.estg.streetreport

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //testar
    }

    fun abrirNotas(view: View) {
        val intent = Intent(this, Notes::class.java)
        startActivity(intent)
    }
}
