package com.gym.mytraining.presentation.exercise

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.gym.mytraining.R
import com.gym.mytraining.databinding.FragmentExerciseBinding
import com.gym.mytraining.domain.model.Exercise
import com.gym.mytraining.domain.model.Training
import com.gym.mytraining.presentation.adapter.ExerciseAdapter
import com.gym.mytraining.presentation.newTraning.NewTrainingFragmentDirections
import com.gym.mytraining.presentation.viewState.ViewStateExercise
import org.koin.androidx.viewmodel.ext.android.viewModel

class ExerciseFragment : Fragment() {

    private var _binding: FragmentExerciseBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExerciseViewModel by viewModel()
    private val args = navArgs<ExerciseFragmentArgs>()
    private var training: Training ? = null
    private var imageUri = Uri.parse("")
    private lateinit var view: View

    private lateinit var rotaAdapter: ExerciseAdapter
    enum class TypeOperation {
        EDIT, VIEW, INSERT
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExerciseBinding.inflate(inflater, container, false)
        val root: View = binding.root

        (activity as AppCompatActivity).supportActionBar?.hide()

        training = args.value.training

        val titleTraining = "${getString(R.string.title_training_in_exercise)} ${this.training!!.name}"

        binding.txtTitulo.text = titleTraining

        getAllExercise(training!!)

        binding.faNewExercise.setOnClickListener {
            val newExercise = Exercise().copy(idTraining = training!!.idTraining)

            changeExercise(requireContext(),newExercise,TypeOperation.INSERT)
        }

        binding.ivVoltar.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.viewStateExercise.observe(viewLifecycleOwner) { viewState ->
            when (viewState) {
                is ViewStateExercise.Loading -> showLoading(viewState.loading)
                is ViewStateExercise.SuccessList -> setAdapter(viewState.list)
                is ViewStateExercise.Success -> success()
                is ViewStateExercise.Failure -> showErro(viewState.messengerError)
                else -> {}
            }
        }

        return root
    }

    private fun getAllExercise(training: Training){
        viewModel.getAll(training)
    }

    private fun showLoading(isLoading: Boolean) {
        //binding.carregamento.isVisible = isLoading
    }

    private fun showErro(text: String) {
        var view = binding.root.rootView
        val snackBarView = Snackbar.make(view, text , Snackbar.LENGTH_LONG)
        view = snackBarView.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.CENTER
        view.layoutParams = params
        snackBarView.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
        snackBarView.show()

        showLoading(false)
    }

    private fun setAdapter(listResponse: List<Exercise>) {

        rotaAdapter = ExerciseAdapter(listResponse)

        if (listResponse.size== 0){
            binding.txtSemEntregas.visibility = View.VISIBLE
        }else{
            binding.txtSemEntregas.visibility = View.GONE
        }

        rotaAdapter.onItemClick = {exercise, _ ->
            changeExercise(requireContext(),exercise,TypeOperation.VIEW)
        }
        rotaAdapter.onItemClickVisualizar = {exercise, _ ->
            changeExercise(requireContext(),exercise,TypeOperation.VIEW)
        }


        rotaAdapter.onItemClickEditar = { exercise, _ ->
            changeExercise(requireContext(),exercise,TypeOperation.EDIT)
        }

        rotaAdapter.onItemClickExcluir = { exercise, _ ->
            deletExercise(requireContext(),exercise)
        }

        binding.recyclerview.adapter = rotaAdapter
        showLoading(false)
    }

    private fun deletExercise(contextScreen : Context,exercise: Exercise){

        val builder = AlertDialog.Builder(contextScreen!!)

        builder.setTitle(getString(R.string.confirm_delet, exercise.name))

        builder.setPositiveButton(getString(R.string.yes)) { dialog, which ->
                viewModel.deleteExercise(exercise)
        }

        builder.setNegativeButton(getString(R.string.no), null)

        builder.create()

        builder.show()

    }

    private fun success(){
        getAllExercise(training!!)

        showLoading(false)

        val builder = android.app.AlertDialog.Builder(requireContext())
        with(builder)
        {
            setTitle(getString(R.string.operation_success))
            setCancelable(false) //não fecha quando clicam fora do dialog
            setPositiveButton(getString(R.string.ok)) { _, _ -> }
            show()
        }
    }

    fun openSomeActivityForResult() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        imagePickerActivityResult.launch(galleryIntent)
    }

    private var imagePickerActivityResult: ActivityResultLauncher<Intent> =

        registerForActivityResult( ActivityResultContracts.StartActivityForResult()) { result ->
            if (result != null) {
                imageUri = result.data?.data

                val appCompatImageView = view.findViewById<AppCompatImageView>(R.id.appCompatImageView)
                appCompatImageView.setImageURI(imageUri)
            }
        }

    private fun changeExercise(contextTela: Context,exercise: Exercise,viewExercise:TypeOperation) {

        val builder = AlertDialog.Builder(contextTela!!)
        val inflater =
            contextTela!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.layout_insert_exercise, null)

        val tiTitleScreenExercise = view.findViewById<TextView>(R.id.tvTitleExercise)
        val tiName = view.findViewById<TextInputEditText>(R.id.tiNomeExercise)
        val tiObservation = view.findViewById<TextInputEditText>(R.id.tiObservationExercise)
        val observationView =  if (exercise.observation.isEmpty()) " " else exercise.observation
        val btnSelectPhoto = view.findViewById<AppCompatButton>(R.id.btnSelectPhoto)
        val appCompatImageView = view.findViewById<AppCompatImageView>(R.id.appCompatImageView)


        if(!exercise.image.toString().isEmpty()){
            imageUri = exercise.image
        }

        btnSelectPhoto.setOnClickListener {
            openSomeActivityForResult()
        }

        tiName.setText(exercise.name)

        if(viewExercise == TypeOperation.VIEW){
            tiTitleScreenExercise.text = getString(R.string.title_screen_exercise_view)
            tiName.isEnabled = false
            tiObservation.isEnabled = false
            btnSelectPhoto.isEnabled = false

            if(!exercise.image.toString().isEmpty()) {
                Glide.with(contextTela)
                    .load(exercise.image)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.loading)
                    .into(appCompatImageView)

            }

        }else if(viewExercise == TypeOperation.EDIT) {

            tiTitleScreenExercise.text = getString(R.string.title_screen_exercise_update)
            val observationView =  if (exercise.observation.isEmpty()) " " else exercise.observation
            tiObservation.setText(observationView)

            if(!exercise.image.toString().isEmpty()) {
                Glide.with(contextTela)
                    .load(exercise.image)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.loading)
                    .into(appCompatImageView)
            }

        }else if(viewExercise == TypeOperation.INSERT) {
            tiTitleScreenExercise.text = getString(R.string.title_screen_exercise_new)
        }

        builder.setView(view)

        builder.setPositiveButton("OK") { dialog, which -> }

        builder.setNegativeButton("Cancel", null)

        val dialog = builder.create()

        dialog.setOnShowListener {
            val button = dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {

                if(tiName.text.isNullOrEmpty()){
                    val errorExerciseEmpty = getString(R.string.exercise_name_empty)
                    tiName.error = errorExerciseEmpty
                }else{
                    val observation =  if (tiObservation.text.toString().isEmpty()) "" else tiObservation.text.toString()
                    val updExercise = exercise.copy(
                        name = tiName.text.toString(),
                        observation = observation,
                        image = imageUri,
                    )

                    if (viewExercise == TypeOperation.EDIT){
                        viewModel.updateExercise(updExercise)
                    }else if (viewExercise == TypeOperation.INSERT){
                        viewModel.insertExercise(updExercise)
                    }

                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }


}