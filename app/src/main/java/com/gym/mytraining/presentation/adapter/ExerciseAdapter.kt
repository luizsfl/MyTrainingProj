package com.gym.mytraining.presentation.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.gym.mytraining.R
import com.gym.mytraining.databinding.ItemExerciseBinding
import com.gym.mytraining.domain.model.Exercise

class ExerciseAdapter(private val dataSet: List<Exercise>) :
    RecyclerView.Adapter<ExerciseAdapter.ViewHolder>() {

    var onItemClick : ((Exercise,Int)-> Unit)? = null
    var onItemClickExcluir : ((Exercise,Int)-> Unit)? = null
    var onItemClickEditar : ((Exercise,Int)-> Unit)? = null
    var onItemClickVisualizar : ((Exercise,Int)-> Unit)? = null

    class ViewHolder(val binding: ItemExerciseBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Exercise){
            binding.title.text = context.getString(R.string.name_exercise, item.name)
            binding.observation.text = context.getString(R.string.observation_exercise, item.observation)

            if(item.deleted){
                binding.container1.setBackgroundColor(Color.DKGRAY)
            }

            Firebase.storage.reference.child("${item.idExercise}.png").
            downloadUrl.addOnSuccessListener { Uri->
                Glide.with(context)
                    .load(Uri.toString())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.loading)
                    .into(binding.appCompatImageView)
            }

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
            onItemClick?.invoke(dataSet[position],position)
        }
        viewHolder.binding.ivVisualizar.setOnClickListener {
            onItemClickVisualizar?.invoke(dataSet[position],position)
        }

        viewHolder.binding.ivExcluir.setOnClickListener {
            onItemClickExcluir?.invoke(dataSet[position],position)
        }

        viewHolder.binding.ivEditar.setOnClickListener {
            onItemClickEditar?.invoke(dataSet[position],position)
        }

    }

    override fun getItemCount() = dataSet.size

}