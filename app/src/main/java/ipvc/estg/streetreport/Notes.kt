package ipvc.estg.streetreport

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import ipvc.estg.streetreport.adapter.LineAdapter
import ipvc.estg.streetreport.dataclass.Note
import kotlinx.android.synthetic.main.activity_notes.*
import java.util.*
import kotlin.collections.ArrayList

class Notes : AppCompatActivity() {
    private lateinit var myList: ArrayList<Note>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        myList = ArrayList<Note>()

        for(i in 0 until 100){
            myList.add(Note("Nota $i", "Descrição $i", Date()))
        }
        recycler_view.adapter = LineAdapter(myList)
        recycler_view.layoutManager = LinearLayoutManager(this)
    }
}