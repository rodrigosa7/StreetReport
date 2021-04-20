package ipvc.estg.streetreport.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ipvc.estg.streetreport.R
import ipvc.estg.streetreport.api.Report

class ReportAdapter(val reports: List<Report>): RecyclerView.Adapter<ReportViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.report_line, parent, false)
        return ReportViewHolder(view)
    }

    override fun getItemCount(): Int {
        return reports.size
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        return holder.bind(reports[position])
    }
}

class ReportViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
    private val reportDesc: TextView = itemView.findViewById(R.id.reportDesc)
    private val reportURL:TextView = itemView.findViewById(R.id.reportURL)

    fun bind(report: Report) {
        reportDesc.text = report.descricao
        reportURL.text = report.imagem
    }

}
