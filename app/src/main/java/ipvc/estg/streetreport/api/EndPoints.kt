package ipvc.estg.streetreport.api


import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface EndPoints {

    @GET("reports")
    fun getReports(): Call<List<Report>>

    @GET("reports/{id}")
    fun getReportByID(@Path("id") id: String): Call<Report>

    @GET("reports/tipo/{tipo}")
    fun getReportByTipo(@Path("tipo") id: String): Call<List<Report>>

    @DELETE("reports/{id}")
    fun deleteReportByID(@Path("id") id: String): Call<OutputPostReport>

    @FormUrlEncoded
    @POST("utilizador")
    fun login(@Field("username") first: String?,@Field("password") second: String?): Call<Login>

    @GET("tipo")
    fun getTipos(): Call<List<Tipo>>

    @Multipart
    @POST("reports")
    fun createReport(
        @Part("desc") desc: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("long") long: RequestBody,
        @Part("user_id") user_id: RequestBody,
        @Part("tipo_id") tipo_id: RequestBody,
        @Part imagem: MultipartBody.Part
    ): Call<OutputPostReport>

    @Multipart
    @POST("reports/{id}")
    fun updateReport(
        @Path("id") id: String,
        @Part("desc") desc: RequestBody,
        @Part("user_id") user_id: RequestBody,
        @Part imagem: MultipartBody.Part
    ): Call<OutputPostReport>
}