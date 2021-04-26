package ipvc.estg.streetreport

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import ipvc.estg.streetreport.adapter.ReportAdapter
import ipvc.estg.streetreport.api.EndPoints
import ipvc.estg.streetreport.api.Report
import ipvc.estg.streetreport.api.ServiceBuilder
import kotlinx.android.synthetic.main.activity_list_reports.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListReports : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_reports)

        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getReports()
        call.enqueue(object : Callback<List<Report>> {
            override fun onResponse(call: Call<List<Report>>, response: Response<List<Report>>) {

                if (response.isSuccessful) {

                    reportRecycler.apply {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(this@ListReports)
                        adapter = ReportAdapter(response.body()!!)
                    }
                } else {
                    val err = response.code()
                    Log.d("ERRO", err.toString())
                    Toast.makeText(this@ListReports, "Deu mal", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Report>>, t: Throwable) {
                Toast.makeText(this@ListReports, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}