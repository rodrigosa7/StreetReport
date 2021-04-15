package ipvc.estg.streetreport

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import ipvc.estg.streetreport.api.EndPoints
import ipvc.estg.streetreport.api.Login
import ipvc.estg.streetreport.api.ServiceBuilder
import ipvc.estg.streetreport.api.User
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref: SharedPreferences = getSharedPreferences(
            getString(R.string.sharedPref), Context.MODE_PRIVATE)

        val user: String? = sharedPref.getString(R.string.userlogged.toString(), null)

        if(user != null){
            val intent = Intent(this, ReportMapa::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun abrirNotas(view: View) {
        val intent = Intent(this, Notes::class.java)
        startActivity(intent)
    }

    fun abrirReports(view: View) {
        val intent = Intent(this, ReportMapa::class.java )
        startActivity(intent)
    }

    fun login(view: View) {
        val intent = Intent(this, ReportMapa::class.java)
        val user = findViewById<EditText>(R.id.usernamelogin).text.toString()
        val pass = findViewById<EditText>(R.id.passwordlogin).text.toString()
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.login(user,pass)
        val sharedPref: SharedPreferences = getSharedPreferences(
            getString(R.string.sharedPref), Context.MODE_PRIVATE)

        val erro = getString(R.string.emptyField)
        if(TextUtils.isEmpty(usernamelogin.text)) {
            usernamelogin.error = erro

        }else if(TextUtils.isEmpty(passwordlogin.text)) {
                passwordlogin.error = erro
        }
        else{
        call.enqueue(object : Callback<Login>{
            override fun onResponse(call: Call<Login>, response: Response<Login>) {

                if (response.isSuccessful) {
                    if (response.body()!!.status){
                        with(sharedPref.edit()) {
                            putString(R.string.userlogged.toString(), response.body()!!.username)
                            commit()
                        }
                        startActivity(intent)
                        finish()
                }else{
                        Toast.makeText(this@MainActivity, response.body()!!.MSG, Toast.LENGTH_SHORT).show()
                    }
            }

            }
            override fun onFailure(call: Call<Login>, t: Throwable) {
                Toast.makeText(this@MainActivity, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
        }

    }
}
