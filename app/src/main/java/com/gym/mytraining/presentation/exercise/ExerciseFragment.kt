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
    private var training: Training? = null
    private val args = navArgs<ExerciseFragmentArgs>()
    private lateinit var rotaAdapter: ExerciseAdapter
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

//        binding.faNew.setOnClickListener {
//            val action =  TrainingFragmentDirections.actionTraningFragmentToNewTraningFragment()
//            findNavController().navigate(action)
//        }

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
    //
//    private fun showSucess(entregaSimples: EntregaSimples){
//        val builder = android.app.AlertDialog.Builder(requireContext())
//        with(builder)
//        {
//            setTitle("Entrega simples criada com sucesso!!")
//            setCancelable(false) //não fecha quando clicam fora do dialog
//            setPositiveButton("OK") { dialog, which ->
//                val action =  CalculoSimplesFragmentDirections.actionCalculoSimplesFragmentToListaEntregaSimplesFragment()
//                findNavController().navigate(action)
//            }
//            show()
//        }
//    }
//
    private fun showLoading(isLoading: Boolean) {
        //binding.carregamento.isVisible = isLoading
    }
    //
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
//            val action =  TrainingFragmentDirections.actionTraningFragmentToExerciseFragment(it)
//            findNavController().navigate(action)
        }
        rotaAdapter.onItemClickVisualizar = {
//            val action =  ListaEntregaRotaFragmentDirections.actionListaEntregaRotaFragmentToDadosRotaFragment2(it)
//            findNavController().navigate(action)
        }

        rotaAdapter.onItemClickEditar = {
            updateExercise(requireContext(),it)
        }

        rotaAdapter.onItemClickExcluir = {
            deletExercise(requireContext(),it)
        }

        binding.recyclerview.adapter = rotaAdapter
        showLoading(false)
    }

    private fun deletExercise(contextScreen : Context,exercise: Exercise){

        val builder = AlertDialog.Builder(contextScreen!!)

        builder.setTitle("Deseja realmente excluir : ${exercise.name}? ")

        builder.setPositiveButton("Sim") { dialog, which ->
                viewModel.deleteExercise(exercise)
        }

        builder.setNegativeButton("Não", null)

        builder.create()

        builder.show()

    }


    private fun success(){
        getAllExercise(training!!)
    }


    private fun updateExercise(contextTela: Context,exercise: Exercise) {

        val builder = AlertDialog.Builder(contextTela!!)
        val view: View
        val inflater =
            contextTela!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.layout_insert_exercise, null)

        val tiTitleScreenExercise = view.findViewById<TextView>(R.id.tvTitleExercise)
        val tiName = view.findViewById<TextInputEditText>(R.id.tiNomeExercise)
        val tiObservation = view.findViewById<TextInputEditText>(R.id.tiObservationExercise)

        tiTitleScreenExercise.text = getString(R.string.title_screen_exercise_update)

        tiName.setText(exercise.name)
        tiObservation.setText(exercise.observation)

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

                    viewModel.updateExercise(updExercise)

                    dialog.dismiss()

                }
            }
        }

        dialog.show()

    }
}