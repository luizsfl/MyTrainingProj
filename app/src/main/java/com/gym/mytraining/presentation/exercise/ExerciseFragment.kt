package com.gym.mytraining.presentation.exercise

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.gym.mytraining.R
import com.gym.mytraining.databinding.FragmentExerciseBinding
import com.gym.mytraining.databinding.FragmentTraningBinding
import com.gym.mytraining.domain.model.Exercise
import com.gym.mytraining.domain.model.Training
import com.gym.mytraining.presentation.adapter.ExerciseAdapter
import com.gym.mytraining.presentation.adapter.TrainingAdapter
import com.gym.mytraining.presentation.newTraning.NewTrainingFragmentArgs
import com.gym.mytraining.presentation.traning.TrainingFragmentDirections
import com.gym.mytraining.presentation.traning.TrainingViewModel
import com.gym.mytraining.presentation.viewState.ViewStateExercise
import com.gym.mytraining.presentation.viewState.ViewStateTraining
import org.koin.androidx.viewmodel.ext.android.viewModel


class ExerciseFragment : Fragment() {

    private var _binding: FragmentExerciseBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExerciseViewModel by viewModel()
    private var training: Training? = null
    private val args = navArgs<ExerciseFragmentArgs>()
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

        getAllTraining(training!!)

//        binding.faNew.setOnClickListener {
//            val action =  TrainingFragmentDirections.actionTraningFragmentToNewTraningFragment()
//            findNavController().navigate(action)
//        }

        viewModel.viewStateExercise.observe(viewLifecycleOwner) { viewState ->
            when (viewState) {
                is ViewStateExercise.Loading -> showLoading(viewState.loading)
                is ViewStateExercise.SuccessList -> setAdapter(viewState.list)
                is ViewStateExercise.Failure -> showErro(viewState.messengerError)
                else -> {}
            }
        }


        return root
    }

    private fun getAllTraining(training: Training){
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
        val rotaAdapter = ExerciseAdapter(listResponse)

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
//            val action =  ListaEntregaRotaFragmentDirections.actionListaEntregaRotaFragmentToDadosVeiculoFragment(2,it)
//            findNavController().navigate(action)
        }

        rotaAdapter.onItemClickExcluir = {
            // excluirEntrega(requireContext(),it)
        }


        binding.recyclerview.adapter = rotaAdapter
        showLoading(false)
    }

}