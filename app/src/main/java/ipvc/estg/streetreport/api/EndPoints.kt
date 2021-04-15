package ipvc.estg.streetreport.api


import retrofit2.Call
import retrofit2.http.*

interface EndPoints {

    @GET("reports")
    fun getReports(): Call<List<Report>>

    @FormUrlEncoded
    @POST("utilizador")
    fun login(@Field("username") first: String?,@Field("password") second: String?): Call<Login>

}