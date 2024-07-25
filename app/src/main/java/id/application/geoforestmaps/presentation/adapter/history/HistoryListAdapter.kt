package id.application.geoforestmaps.presentation.adapter.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.application.core.domain.model.History
import id.application.geoforestmaps.databinding.ItemHistoryDataBinding

class HistoryListAdapter : RecyclerView.Adapter<HistoryListAdapter.ViewHolder>(){
    private val dataDiffer = AsyncListDiffer(this, object : DiffUtil.ItemCallback<History>(){
        override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {
            return oldItem.title == newItem.title &&
                    oldItem.image == newItem.image && oldItem.description == newItem.description &&
                    oldItem.time == newItem.time && oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {
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
        fun bind(data : History){
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

    fun setData(data : List<History>){
        dataDiffer.submitList(data)
        notifyItemRangeChanged(0,data.size)
    }
}