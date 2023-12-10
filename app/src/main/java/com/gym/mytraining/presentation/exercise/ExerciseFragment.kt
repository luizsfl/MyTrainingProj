package com.gym.mytraining.presentation.exercise

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.gym.mytraining.R
import com.gym.mytraining.databinding.FragmentExerciseBinding
import com.gym.mytraining.domain.model.Exercise
import com.gym.mytraining.domain.model.Training
import com.gym.mytraining.presentation.adapter.ExerciseAdapter
import com.gym.mytraining.presentation.viewState.ViewStateExercise
import org.koin.androidx.viewmodel.ext.android.viewModel

class ExerciseFragment : Fragment() {

    private var _binding: FragmentExerciseBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExerciseViewModel by viewModel()
    private val args = navArgs<ExerciseFragmentArgs>()
    private var training: Training ? = null

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

        rotaAdapter.onItemClick = {
            changeExercise(requireContext(),it,TypeOperation.VIEW)
        }
        rotaAdapter.onItemClickVisualizar = {
            changeExercise(requireContext(),it,TypeOperation.VIEW)
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
    }

    private fun changeExercise(contextTela: Context,exercise: Exercise,viewExercise:TypeOperation) {

        val builder = AlertDialog.Builder(contextTela!!)
        val view: View
        val inflater =
            contextTela!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.layout_insert_exercise, null)

        val tiTitleScreenExercise = view.findViewById<TextView>(R.id.tvTitleExercise)
        val tiName = view.findViewById<TextInputEditText>(R.id.tiNomeExercise)
        val tiObservation = view.findViewById<TextInputEditText>(R.id.tiObservationExercise)
        val observationView =  if (exercise.observation.isEmpty()) " " else exercise.observation

        tiName.setText(exercise.name)
        tiObservation.setText(observationView)

        if(viewExercise == TypeOperation.VIEW){
            tiTitleScreenExercise.text = getString(R.string.title_screen_exercise_view)
            tiName.isEnabled = false
            tiObservation.isEnabled = false
        }else if(viewExercise == TypeOperation.EDIT) {
            tiTitleScreenExercise.text = getString(R.string.title_screen_exercise_update)
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
                        //image = Uri.parse(""),
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