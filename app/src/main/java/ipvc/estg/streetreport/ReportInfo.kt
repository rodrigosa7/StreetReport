package ipvc.estg.streetreport

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.maps.GoogleMap
import com.squareup.picasso.Picasso
import ipvc.estg.streetreport.adapter.TITULO
import ipvc.estg.streetreport.api.EndPoints
import ipvc.estg.streetreport.api.OutputPostReport
import ipvc.estg.streetreport.api.Report
import ipvc.estg.streetreport.api.ServiceBuilder
import kotlinx.android.synthetic.main.activity_add_report.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

class ReportInfo : AppCompatActivity() {
    val REQUEST_CODE = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_info)
        val ID = intent.getStringExtra(ID)
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getReportByID(ID!!)


        call.enqueue(object : Callback<Report> {
            override fun onResponse(call: Call<Report>, response: Response<Report>) {

                if (response.isSuccessful) {

                    val report = response.body()!!


                    findViewById<TextView>(R.id.inputDescReport).text = report.descricao
                    findViewById<TextView>(R.id.textTipoInput).text = report.nomeTipo
                    findViewById<TextView>(R.id.reportedbyuser).text = report.nomeUser
                    val imagem = findViewById<ImageView>(R.id.imagem)


                    Picasso.get()
                        .load("http://10.0.2.2:81/uploads/" + report.foto + ".png")
                        .into(imagem)
                    val btnDelete = findViewById<Button>(R.id.deleteReport)
                    val btnEdit = findViewById<Button>(R.id.editReport)
                    val sharedPref: SharedPreferences = getSharedPreferences(
                        getString(R.string.sharedPref), Context.MODE_PRIVATE
                    )

                    val user: Int = sharedPref.getInt(R.string.userlogged.toString(), 0)
                    if (user != report.utilizador_id) {
                        btnDelete.visibility = View.INVISIBLE
                        btnEdit.visibility = View.INVISIBLE
                    }
                    listeners()


                } else {

                    Toast.makeText(this@ReportInfo, response.code(), Toast.LENGTH_SHORT).show()
                }
            }


            override fun onFailure(call: Call<Report>, t: Throwable) {
                Toast.makeText(this@ReportInfo, "${t.message}", Toast.LENGTH_SHORT).show()

            }
        })


        val editview = findViewById<EditText>(R.id.inputDescReportEdit)
        editview.visibility = View.INVISIBLE
        val uploadbtn = findViewById<Button>(R.id.buttonUpload)
        uploadbtn.visibility = View.INVISIBLE

        val savebtn = findViewById<Button>(R.id.saveReport)
        savebtn.visibility = View.INVISIBLE

        val cancelbtn = findViewById<Button>(R.id.cancel_buttonReport)
        cancelbtn.visibility = View.INVISIBLE

        val editbtn = findViewById<Button>(R.id.editReport)

        val btnDelete = findViewById<Button>(R.id.deleteReport)

        val textDesc = findViewById<TextView>(R.id.inputDescReport)


    }

    private fun listeners() {
        val editbtn = findViewById<Button>(R.id.editReport)

        val btnDelete = findViewById<Button>(R.id.deleteReport)

        val textDesc = findViewById<TextView>(R.id.inputDescReport)
        val editview = findViewById<EditText>(R.id.inputDescReportEdit)
        val uploadbtn = findViewById<Button>(R.id.buttonUpload)
        val savebtn = findViewById<Button>(R.id.saveReport)
        val cancelbtn = findViewById<Button>(R.id.cancel_buttonReport)
        val imagem = findViewById<ImageView>(R.id.imagem)
        val deletebtn = findViewById<Button>(R.id.deleteReport)

        editbtn.setOnClickListener {
            cancelbtn.visibility = View.VISIBLE
            savebtn.visibility = View.VISIBLE
            editview.visibility = View.VISIBLE
            uploadbtn.visibility = View.VISIBLE
            editbtn.visibility = View.INVISIBLE
            btnDelete.visibility = View.INVISIBLE
            textDesc.visibility = View.INVISIBLE

            editview.setText(textDesc.text)


        }
        uploadbtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE)

        }
        cancelbtn.setOnClickListener {
            cancelbtn.visibility = View.INVISIBLE
            savebtn.visibility = View.INVISIBLE
            editview.visibility = View.INVISIBLE
            uploadbtn.visibility = View.INVISIBLE
            editbtn.visibility = View.VISIBLE
            btnDelete.visibility = View.VISIBLE
            textDesc.visibility = View.VISIBLE
        }
        savebtn.setOnClickListener {
            val erro: String = getString(R.string.emptyField)

            val request = ServiceBuilder.buildService(EndPoints::class.java)
            //user
            val sharedPref: SharedPreferences = getSharedPreferences(
                getString(R.string.sharedPref), Context.MODE_PRIVATE
            )
            val user: Int = sharedPref.getInt(R.string.userlogged.toString(), 0)
            val userEnviar =
                RequestBody.create(MediaType.parse("multipart/form-data"), user.toString())
            //desc
            val descnova = editview.text
            val descenviar =
                RequestBody.create(MediaType.parse("multipart/form-data"), descnova.toString())

            //imagem
            val verificar = findViewById<ImageView>(R.id.imagem).drawable
            if (verificar != null) {

                val imagem = findViewById<ImageView>(R.id.imagem).drawable.toBitmap()
                val file = convertBitmapToFile("url", imagem)
                val imgFileRequest: RequestBody =
                    RequestBody.create(MediaType.parse("image/*"), file)
                val image: MultipartBody.Part =
                    MultipartBody.Part.createFormData("imagem", file.name, imgFileRequest)
                val id = intent.getStringExtra(ID)
                val call = request.updateReport(id!!, descenviar, userEnviar, image)
                when {
                    TextUtils.isEmpty(editview.text) -> {
                        editview.error = erro
                        //setResult(Activity.RESULT_CANCELED, replyIntent)

                    }

                    else -> {


                        call.enqueue(object : Callback<OutputPostReport> {
                            override fun onResponse(
                                call: Call<OutputPostReport>,
                                response: Response<OutputPostReport>
                            ) {

                                if (response.isSuccessful) {
                                    Toast.makeText(
                                        this@ReportInfo,
                                        getString(R.string.reportadd),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent = Intent(this@ReportInfo, ReportMapa::class.java)
                                    //verificar na stack de atividades se já existe esta atividade
                                    //se existir limpa e cria de novo
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    finish()

                                } else {

                                    Toast.makeText(
                                        this@ReportInfo,
                                        response.code(),
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            }

                            override fun onFailure(call: Call<OutputPostReport>, t: Throwable) {
                                Toast.makeText(this@ReportInfo, "${t.message}", Toast.LENGTH_SHORT)
                                    .show()
                                Log.d("ERRO", "${t.message}")
                            }
                        })
                    }

                }
            } else {
                Toast.makeText(this@ReportInfo, R.string.selectImage, Toast.LENGTH_LONG).show()
            }
        }

        deletebtn.setOnClickListener {
            val request = ServiceBuilder.buildService(EndPoints::class.java)
            val id = intent.getStringExtra(ID)
            val call = request.deleteReportByID(id!!)
            call.enqueue(object : Callback<OutputPostReport> {
                override fun onResponse(
                    call: Call<OutputPostReport>,
                    response: Response<OutputPostReport>
                ) {

                    if (response.isSuccessful) {

                        val intent = Intent(this@ReportInfo, ReportMapa::class.java)
                        //verificar na stack de atividades se já existe esta atividade
                        //se existir limpa e cria de novo
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()

                    } else {

                        Toast.makeText(this@ReportInfo, response.code(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<OutputPostReport>, t: Throwable) {
                    Toast.makeText(this@ReportInfo, "${t.message}", Toast.LENGTH_SHORT).show()
                    Log.d("ERRO", "${t.message}")
                }
            })
        }
    }

    private fun convertBitmapToFile(fileName: String, bitmap: Bitmap): File {
        //create a file to write bitmap data
        val file = File(this@ReportInfo.cacheDir, fileName)
        file.createNewFile()

        //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, bos)
        val bitMapData = bos.toByteArray()

        //write the bytes in file
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        try {
            fos?.write(bitMapData)
            fos?.flush()
            fos?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            imagem.setImageURI(data?.data) // handle chosen image


        }
    }
}