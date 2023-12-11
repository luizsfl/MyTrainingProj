package com.gym.mytraining.presentation.newTraning

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.gym.mytraining.R
import com.gym.mytraining.databinding.FragmentNewTraningBinding
import com.gym.mytraining.domain.model.Exercise
import com.gym.mytraining.domain.model.Training
import com.gym.mytraining.presentation.adapter.ExerciseAdapter
import com.gym.mytraining.presentation.exercise.ExerciseFragment
import com.gym.mytraining.presentation.exercise.ExerciseViewModel
import com.gym.mytraining.presentation.viewState.ViewStateExercise
import com.gym.mytraining.presentation.viewState.ViewStateTraining
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.sql.Timestamp

class NewTrainingFragment : Fragment() {

    private var _binding: FragmentNewTraningBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NewTrainingViewModel by viewModel()
    private val viewModelExercise: ExerciseViewModel by viewModel()
    private var training: Training? = null
    private val args = navArgs<NewTrainingFragmentArgs>()
    private lateinit var typeScreen: TypeOperation
    private lateinit var view: View
    private var imageUri = Uri.parse("")
    private val listExercise = mutableListOf<Exercise>()

    enum class TypeOperation {
        EDIT, INSERT
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewTraningBinding.inflate(inflater, container, false)
        val root: View = binding.root

        training = args.value.training
        typeScreen = args.value.typeScreen

        if (typeScreen == TypeOperation.INSERT) {
            binding.btInsert.setText(getString(R.string.bt_new_training))
        }else if (typeScreen == TypeOperation.EDIT) {
            training?.let {
                observeExercise()
                setDadosTela(it)
                viewModelExercise.getAll(training!!)
                binding.btInsert.setText(getString(R.string.bt_edit_training))
                binding.tvTitleTraining.setText(getString(R.string.bt_edit_training))
            }
        }

        (activity as AppCompatActivity).supportActionBar?.hide()

        listerners()

        observeTraining()

        return root
    }

    private fun listerners() {
        binding.btInsert.setOnClickListener {

            val txName = binding.tiNome.text.toString()

            if (txName.isNullOrEmpty()) {
                binding.tiNome.error = getString(R.string.training_name_empty)
            } else {
                val txDescription = binding.tiDescription.text.toString()

                if (typeScreen == TypeOperation.INSERT) {
                    val dateTimeStamp = Timestamp(System.currentTimeMillis())
                    val training = Training(
                        idTraining = "",
                        idUsuario = "",
                        name = txName,
                        description = txDescription,
                        //date = dateTimeStamp,
                    )
                    insertTraining(training, listExercise)
                }else if (typeScreen == TypeOperation.EDIT) {

                    val dateTimeStamp = Timestamp(System.currentTimeMillis())

                    val training = training!!.copy(
                        name = txName,
                        description = txDescription,
                        //date = dateTimeStamp,
                        )

                    updateTraining(training, listExercise)
                }
            }
        }

        binding.tvInsertExercise.setOnClickListener {
            changeExercise(contextTela =  requireContext(),exercise = Exercise(),viewExercise = ExerciseFragment.TypeOperation.INSERT, position = -1)
        }

        binding.ivVoltar.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeTraining() {
        viewModel.viewStateTraining.observe(viewLifecycleOwner) { viewState ->
            when (viewState) {
                is ViewStateTraining.Loading -> showLoading(viewState.loading)
                is ViewStateTraining.Success -> success()
                is ViewStateTraining.Failure -> showErro(viewState.messengerError)
                else -> {}
            }
        }
    }

    private fun observeExercise() {
        viewModelExercise.viewStateExercise.observe(viewLifecycleOwner) { viewState ->
            when (viewState) {
                is ViewStateExercise.Loading -> showLoading(viewState.loading)
                is ViewStateExercise.SuccessList ->successListExercise(viewState.list)
                is ViewStateExercise.Success -> success()
                is ViewStateExercise.Failure -> showErro(viewState.messengerError)
                else -> {}
            }
        }
    }

    private fun successListExercise(listResponse:List<Exercise>) {
        listExercise.addAll(listResponse)
        setAdapter(listResponse)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
        binding.btInsert.isVisible = !isLoading
    }

    private fun showErro(text: String) {
        var view = binding.root.rootView
        val snackBarView = Snackbar.make(view, text, Snackbar.LENGTH_LONG)
        view = snackBarView.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.CENTER
        view.layoutParams = params
        snackBarView.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
        snackBarView.show()

        showLoading(false)
    }

    fun success() {

        showLoading(false)

        val builder = android.app.AlertDialog.Builder(requireContext())
        with(builder)
        {
            setTitle(getString(R.string.operation_success))
            setCancelable(false) //não fecha quando clicam fora do dialog
            setPositiveButton(getString(R.string.ok), DialogInterface.OnClickListener { dialog, which ->
                val action =
                    NewTrainingFragmentDirections.actionNewTraningFragmentToTraningFragment()
                findNavController().navigate(action)
            })
            show()
        }
    }

    private fun insertTraining(training: Training, listExercise: List<Exercise>) {
        viewModel.insert(training, listExercise)
    }

    private fun updateTraining(training: Training, listExercise: List<Exercise>) {
        viewModel.update(training, listExercise)
    }


    private fun setDadosTela(training: Training, bloqueio: Boolean = false) {
        binding.tiNome.setText(training.name)

        val description = if(training.description.isNullOrEmpty()) " " else training.description

        binding.tiDescription.setText(description)

    }

    private fun setAdapter(listResponse: List<Exercise>) {
        val adapter = ExerciseAdapter(listResponse)

        adapter.onItemClick = { exercise, position ->
            changeExercise(requireContext(),exercise, ExerciseFragment.TypeOperation.VIEW,position)
        }
        adapter.onItemClickVisualizar = { exercise, position ->
            changeExercise(requireContext(),exercise, ExerciseFragment.TypeOperation.VIEW,position)
        }

        adapter.onItemClickEditar = { exercise, position ->
            changeExercise(requireContext(),exercise, ExerciseFragment.TypeOperation.EDIT,position)
        }

        adapter.onItemClickExcluir = { _, position ->
            val itemDeleted = listExercise.get(position).copy(deleted = !listExercise.get(position).deleted)
            listExercise.set(position,itemDeleted)
            setAdapter(listExercise)
        }

        binding.recyclerviewExercise.adapter = adapter
        showLoading(false)
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

    private fun changeExercise(contextTela: Context,exercise: Exercise,viewExercise: ExerciseFragment.TypeOperation,position:Int) {

        val builder = AlertDialog.Builder(contextTela!!)
        val inflater = contextTela!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        view = inflater.inflate(R.layout.layout_insert_exercise, null)
        val tiTitleScreenExercise = view.findViewById<TextView>(R.id.tvTitleExercise)
        val tiName = view.findViewById<TextInputEditText>(R.id.tiNomeExercise)
        val tiObservation = view.findViewById<TextInputEditText>(R.id.tiObservationExercise)
        val btnSelectPhoto = view.findViewById<AppCompatButton>(R.id.btnSelectPhoto)
        val appCompatImageView = view.findViewById<AppCompatImageView>(R.id.appCompatImageView)

        if(!exercise.image.toString().isEmpty()){
            imageUri = exercise.image
        }

        btnSelectPhoto.setOnClickListener {

            openSomeActivityForResult()

        }

        tiName.setText(exercise.name)

        if(viewExercise == ExerciseFragment.TypeOperation.VIEW){
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

        }else if(viewExercise == ExerciseFragment.TypeOperation.EDIT) {
            val observationView =  if (exercise.observation.isEmpty()) " " else exercise.observation
            tiObservation.setText(observationView)
            tiTitleScreenExercise.text = getString(R.string.title_screen_exercise_update)

            if(!exercise.image.toString().isEmpty()) {
                Glide.with(contextTela)
                    .load(exercise.image)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.loading)
                    .into(appCompatImageView)
            }

        }else if(viewExercise == ExerciseFragment.TypeOperation.INSERT) {
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


                    if (viewExercise == ExerciseFragment.TypeOperation.EDIT){

                        val updExercise = exercise.copy(
                            name = tiName.text.toString(),
                            observation = observation,
                            image = imageUri,
                        )

                        listExercise.set(position,updExercise)
                        setAdapter(listExercise)

                    }else if (viewExercise == ExerciseFragment.TypeOperation.INSERT){

                        val observation =  if (tiObservation.text.toString().isEmpty()) "" else tiObservation.text.toString()

                        val exercise = Exercise(
                            name = tiName.text.toString(),
                            image = imageUri,
                            observation = observation
                        )

                        listExercise.add(exercise)

                        setAdapter(listExercise)

                    }

                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }
}

