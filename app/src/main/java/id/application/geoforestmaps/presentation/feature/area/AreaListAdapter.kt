package id.application.geoforestmaps.presentation.feature.area

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.application.core.model.Dashboard
import id.application.geoforestmaps.databinding.ItemBlokDataBinding

class AreaListAdapter: RecyclerView.Adapter<AreaListAdapter.ViewHolder>()  {
    private val dataDiffer = AsyncListDiffer(this, object : DiffUtil.ItemCallback<Dashboard>(){
        override fun areItemsTheSame(oldItem: Dashboard, newItem: Dashboard): Boolean {
            return oldItem.name == newItem.name &&
                    oldItem.image == newItem.image

        }

        override fun areContentsTheSame(oldItem: Dashboard, newItem: Dashboard): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBlokDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataDiffer.currentList[position])
    }

    class ViewHolder(private val binding: ItemBlokDataBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data : Dashboard){
            with(binding){
                ivImage.setImageResource(data.image)
                tvTitle.text = data.name
            }
        }
    }

    override fun getItemCount(): Int {
        return dataDiffer.currentList.size
    }

    fun setData(data : List<Dashboard>){
        dataDiffer.submitList(data)
        notifyItemRangeChanged(0,data.size)
    }
}
