package ipvc.estg.streetreport.api

data class Report(
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val descricao: String,
    val foto: String,
    val utilizador_id: Int,
    val tipo_id: String,
    val nomeTipo: String,
    val nomeUser: String
)