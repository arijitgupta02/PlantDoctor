package com.example.plantdoctor

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.plantdoctor.databinding.HistoryItemBinding
import com.example.plantdoctor.ScanHistoryItem

class ScanHistoryAdapter(
    private val context: Context,
    private var historyList: List<ScanHistoryItem>
) : RecyclerView.Adapter<ScanHistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(val binding: HistoryItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = HistoryItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun getItemCount(): Int = historyList.size

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = historyList[position]
        holder.binding.historyPrediction.text = item.prediction
        holder.binding.historyConfidence.text = "Confidence: ${item.confidence}"
        holder.binding.historyTimestamp.text = android.text.format.DateFormat.format("dd MMM yyyy • hh:mm a", item.timestamp)
        holder.binding.historyImage.setImageURI(Uri.parse(item.imagePath))
    }

    fun updateData(newList: List<ScanHistoryItem>) {
        historyList = newList
        notifyDataSetChanged()
    }
}
