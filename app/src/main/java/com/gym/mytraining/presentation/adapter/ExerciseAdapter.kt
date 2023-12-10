package com.gym.mytraining.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gym.mytraining.databinding.ItemExerciseBinding
import com.gym.mytraining.databinding.ItemTrainingBinding
import com.gym.mytraining.domain.model.Exercise
import com.gym.mytraining.domain.model.Training

class ExerciseAdapter(private val dataSet: List<Exercise>) :
    RecyclerView.Adapter<ExerciseAdapter.ViewHolder>() {

    var onItemClick : ((Exercise)-> Unit)? = null
    var onItemClickExcluir : ((Exercise)-> Unit)? = null
    var onItemClickEditar : ((Exercise)-> Unit)? = null
    var onItemClickVisualizar : ((Exercise)-> Unit)? = null

    class ViewHolder(val binding: ItemExerciseBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Exercise){
            binding.title.text = item.name
            binding.observation.text = item.observation
           // binding.date.text = item.date.toString()
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val itemFilterAdapter = ItemExerciseBinding.inflate(inflater,viewGroup,false)

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