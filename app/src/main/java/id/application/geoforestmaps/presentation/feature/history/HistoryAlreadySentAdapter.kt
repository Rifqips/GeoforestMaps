package id.application.geoforestmaps.presentation.feature.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.application.core.domain.model.HistoryAlreadySent
import id.application.geoforestmaps.databinding.ItemHistoryDataBinding

class HistoryAlreadySentAdapter : RecyclerView.Adapter<HistoryAlreadySentAdapter.ViewHolder>(){
    private val dataDiffer = AsyncListDiffer(this, object : DiffUtil.ItemCallback<HistoryAlreadySent>(){
        override fun areItemsTheSame(oldItem: HistoryAlreadySent, newItem: HistoryAlreadySent): Boolean {
            return oldItem.title == newItem.title &&
                    oldItem.image == newItem.image && oldItem.description == newItem.description &&
                    oldItem.time == newItem.time && oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: HistoryAlreadySent, newItem: HistoryAlreadySent): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataDiffer.currentList[position])
    }

    class ViewHolder(private val binding: ItemHistoryDataBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data : HistoryAlreadySent){
            with(binding){
                ivItemHistory.setImageResource(data.image)
                tvTitleItemHistory.text = data.title
                tvDescItemHistory.text = data.description
                tvTimeItemHistory.text = data.time
                tvDateItemHistory.text = data.date
            }
        }

    }

    override fun getItemCount(): Int {
        return dataDiffer.currentList.size
    }

    fun setData(data : List<HistoryAlreadySent>){
        dataDiffer.submitList(data)
        notifyItemRangeChanged(0,data.size)
    }
}