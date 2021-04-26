package ipvc.estg.streetreport

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import ipvc.estg.streetreport.api.*
import kotlinx.android.synthetic.main.activity_add_report.*
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*


class AddReport : AppCompatActivity() {
    private lateinit var tipos: List<Tipo>
    val REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_report)

        val spinner = findViewById<Spinner>(R.id.spinnerTipos)
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.tipos,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        val button = findViewById<Button>(R.id.confirmarBtn)
        val erro = getString(R.string.emptyField)


        button.setOnClickListener {
            //imagem
            val verificar = findViewById<ImageView>(R.id.imagem).drawable
            if (verificar != null) {
                val imagem = findViewById<ImageView>(R.id.imagem).drawable.toBitmap()

                val file = convertBitmapToFile("url", imagem)
                val imgFileRequest: RequestBody =
                    RequestBody.create(MediaType.parse("image/*"), file)
                val image: MultipartBody.Part =
                    MultipartBody.Part.createFormData("imagem", file.name, imgFileRequest)


                val desc = findViewById<EditText>(R.id.inputDescReport).text.toString()
                val descEnviar: RequestBody =
                    RequestBody.create(MediaType.parse("multipart/form-data"), desc)
                val desced = findViewById<EditText>(R.id.inputDescReport)
                val lat = intent.getParcelableExtra<LatLng>("localizacao")!!.latitude
                val latEnviar: RequestBody =
                    RequestBody.create(MediaType.parse("multipart/form-data"), lat.toString())

                val long = intent.getParcelableExtra<LatLng>("localizacao")!!.longitude
                val longEnviar: RequestBody =
                    RequestBody.create(MediaType.parse("multipart/form-data"), long.toString())
                val request = ServiceBuilder.buildService(EndPoints::class.java)

                val sharedPref: SharedPreferences = getSharedPreferences(
                    getString(R.string.sharedPref), Context.MODE_PRIVATE
                )
                val user: Int = sharedPref.getInt(R.string.userlogged.toString(), 0)
                val userEnviar =
                    RequestBody.create(MediaType.parse("multipart/form-data"), user.toString())

                val tipo = spinner.selectedItem
                val tipoEnviar =
                    RequestBody.create(MediaType.parse("multipart/form-data"), tipo.toString())

                val call = request.createReport(
                    descEnviar,
                    latEnviar,
                    longEnviar,
                    userEnviar,
                    tipoEnviar,
                    image
                )
                //button.setOnClickListener {

                when {


                    TextUtils.isEmpty(desced.text) -> {
                        desced.error = erro


                    }

                    else -> {
                        call.enqueue(object : Callback<OutputPostReport> {
                            override fun onResponse(
                                call: Call<OutputPostReport>,
                                response: Response<OutputPostReport>
                            ) {

                                if (response.isSuccessful) {
                                    Toast.makeText(
                                        this@AddReport,
                                        getString(R.string.reportadd),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent = Intent(this@AddReport, ReportMapa::class.java)
                                    //verificar na stack de atividades se já existe esta atividade
                                    //se existir limpa e cria de novo
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    finish()

                                } else {

                                    Toast.makeText(
                                        this@AddReport,
                                        response.code(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onFailure(call: Call<OutputPostReport>, t: Throwable) {
                                Toast.makeText(
                                    this@AddReport,
                                    "${t.message}",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        })
                    }
                }


                // }
            } else {
                Toast.makeText(this@AddReport, R.string.selectImage, Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun convertBitmapToFile(fileName: String, bitmap: Bitmap): File {
        //create a file to write bitmap data
        val file = File(this@AddReport.cacheDir, fileName)
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

    fun chooseImage(view: View) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            imagem.setImageURI(data?.data) // handle chosen image


        }
    }

}