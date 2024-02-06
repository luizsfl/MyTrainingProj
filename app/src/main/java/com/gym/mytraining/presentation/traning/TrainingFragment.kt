package com.gym.mytraining.presentation.traning

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.gym.mytraining.R
import com.gym.mytraining.databinding.FragmentTraningBinding
import com.gym.mytraining.domain.model.Training
import com.gym.mytraining.presentation.MainActivity
import com.gym.mytraining.presentation.Utils
import com.gym.mytraining.presentation.adapter.TrainingAdapter
import com.gym.mytraining.presentation.newTraning.NewTrainingFragment
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

        (activity as MainActivity).chama()

        (activity as AppCompatActivity).supportActionBar?.hide()

        getAllTraining()

        binding.faNew.setOnClickListener {
            val action =  TrainingFragmentDirections.actionTraningFragmentToNewTraningFragment()
            findNavController().navigate(action)
        }

        binding.ivLogout.setOnClickListener {
            logout()
        }

        viewModel.viewStateTraining.observe(viewLifecycleOwner) { viewState ->
            when (viewState) {
                is ViewStateTraining.Loading    -> showLoading(viewState.loading)
                is ViewStateTraining.Success    -> getAllTraining()
                is ViewStateTraining.SucessList -> setAdapter(viewState.list)
                is ViewStateTraining.Failure    -> showErro(viewState.messengerError)
                else -> {}
            }
        }
        return root
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut();
        val action = TrainingFragmentDirections.actionTraningFragmentToLoginFragment()
        findNavController().navigate(action)
    }

    private fun getAllTraining(){
        viewModel.getAll()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
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
            val action =  TrainingFragmentDirections.actionTraningFragmentToExerciseFragment(it)
            findNavController().navigate(action)
        }

        rotaAdapter.onItemClickVisualizar = {
            val action =  TrainingFragmentDirections.actionTraningFragmentToExerciseFragment(it)
            findNavController().navigate(action)
        }

        rotaAdapter.onItemClickEditar = {
            val action =  TrainingFragmentDirections.actionTraningFragmentToNewTraningFragment(it,NewTrainingFragment.TypeOperation.EDIT)
            findNavController().navigate(action)
        }

        rotaAdapter.onItemClickExcluir = {
            deletTraining(requireContext(),it)
        }


        binding.recyclerview.adapter = rotaAdapter
        showLoading(false)
    }

    private fun deletTraining(contextScreen : Context, item: Training){

        val builder = AlertDialog.Builder(contextScreen!!)

        builder.setTitle(getString(R.string.confirm_delet, item.name))

        builder.setPositiveButton(getString(R.string.yes)) { dialog, which ->
            viewModel.deleteTraining(item)
        }

        builder.setNegativeButton(getString(R.string.no), null)

        builder.create()

        builder.show()

    }

}