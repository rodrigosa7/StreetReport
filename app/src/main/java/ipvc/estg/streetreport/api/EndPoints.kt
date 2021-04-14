package ipvc.estg.streetreport.api


import retrofit2.Call
import retrofit2.http.*

interface EndPoints {

    @GET("reports")
    fun getReports(): Call<List<Report>>


}