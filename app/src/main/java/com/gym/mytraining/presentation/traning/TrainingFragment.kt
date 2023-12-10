package com.gym.mytraining.presentation.traning

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.gym.mytraining.R
import com.gym.mytraining.databinding.FragmentTraningBinding
import com.gym.mytraining.domain.model.Training
import com.gym.mytraining.presentation.adapter.TrainingAdapter
import com.gym.mytraining.presentation.viewState.ViewStateTraining
import org.koin.androidx.viewmodel.ext.android.viewModel

class TrainingFragment : Fragment() {

    private var _binding: FragmentTraningBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TrainingViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTraningBinding.inflate(inflater, container, false)
        val root: View = binding.root

        (activity as AppCompatActivity).supportActionBar?.hide()

        getAllTraining()

        binding.faNew.setOnClickListener {
            val action =  TrainingFragmentDirections.actionTraningFragmentToNewTraningFragment()
            findNavController().navigate(action)
        }

        viewModel.viewStateTraining.observe(viewLifecycleOwner) { viewState ->
            when (viewState) {
                is ViewStateTraining.Loading -> showLoading(viewState.loading)
                is ViewStateTraining.SucessList -> setAdapter(viewState.list)
                is ViewStateTraining.Failure -> showErro(viewState.messengerError)
                else -> {}
            }
        }


        return root
    }

    private fun getAllTraining(){
        viewModel.getAll()
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

    private fun setAdapter(listResponse: List<Training>) {
        val rotaAdapter = TrainingAdapter(listResponse)

        if (listResponse.size== 0){
            binding.txtSemEntregas.visibility = View.VISIBLE
            binding.txtTitulo.visibility = View.GONE
        }else{
            binding.txtSemEntregas.visibility = View.GONE
            binding.txtTitulo.visibility = View.VISIBLE
        }

        rotaAdapter.onItemClick = {
//            val action =  ListaEntregaRotaFragmentDirections.actionListaEntregaRotaFragmentToDadosRotaFragment2(it)
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