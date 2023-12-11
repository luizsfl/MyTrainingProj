package com.gym.mytraining.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gym.mytraining.R
import com.gym.mytraining.databinding.ItemTrainingBinding
import com.gym.mytraining.domain.model.Training

class TrainingAdapter(private val dataSet: List<Training>) :
    RecyclerView.Adapter<TrainingAdapter.ViewHolder>() {

    var onItemClick : ((Training)-> Unit)? = null
    var onItemClickExcluir : ((Training)-> Unit)? = null
    var onItemClickEditar : ((Training)-> Unit)? = null
    var onItemClickVisualizar : ((Training)-> Unit)? = null

    class ViewHolder(val binding: ItemTrainingBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Training){
            binding.title.text = context.getString(R.string.name_training, item.name)
            binding.description.text =
                context.getString(R.string.description_training, item.description)
           // binding.date.text = item.date.toString()
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val itemFilterAdapter = ItemTrainingBinding.inflate(inflater,viewGroup,false)

        return ViewHolder(itemFilterAdapter,viewGroup.context)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(dataSet[position])

        viewHolder.binding.lcDados.setOnClickListener {
            onItemClick?.invoke(dataSet[position])
        }
        viewHolder.binding.ivVisualizar.setOnClickListener {
            onItemClickVisualizar?.invoke(dataSet[position])
        }

        viewHolder.binding.ivExcluir.setOnClickListener {
            onItemClickExcluir?.invoke(dataSet[position])
        }

        viewHolder.binding.ivEditar.setOnClickListener {
            onItemClickEditar?.invoke(dataSet[position])
        }

    }

    override fun getItemCount() = dataSet.size

}