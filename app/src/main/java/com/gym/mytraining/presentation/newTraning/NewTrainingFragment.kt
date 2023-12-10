package com.gym.mytraining.presentation.newTraning

import android.content.DialogInterface
import android.net.Uri
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
import com.gym.mytraining.databinding.FragmentNewTraningBinding
import com.gym.mytraining.domain.model.Exercise
import com.gym.mytraining.domain.model.Training
import com.gym.mytraining.presentation.newLogin.NewLoginFragmentDirections
import com.gym.mytraining.presentation.viewState.ViewStateTraining
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.sql.Timestamp
class NewTrainingFragment : Fragment() {

    private var _binding: FragmentNewTraningBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NewTrainingViewModel by viewModel()
    private var training : Training? = null
    private val args = navArgs<NewTrainingFragmentArgs>()
    private var typeScreen : Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewTraningBinding.inflate(inflater, container, false)
        val root: View = binding.root

        training = args.value.training

        training?.let {
            //Editar
            if(typeScreen==1){
                setDadosTela(it)
                binding.btInsert.setText("Editar Treino")
            }else if(typeScreen == 2){
                //Visualizar
                setDadosTela(it,true)
                binding.btInsert.setText("Cadastrar Treino")
            }
        }

        (activity as AppCompatActivity).supportActionBar?.hide()


        binding.btInsert.setOnClickListener {

            val txName = binding.tiNome.text.toString()
            val txDescription = binding.tiDescription.text.toString()

            if(typeScreen == 0){
                val dateTimeStamp = Timestamp(System.currentTimeMillis())
                val training = Training(idTraining = "",
                     idUsuario = "",
                     name= txName,
                     description = txDescription,
                     //date = dateTimeStamp,
                )

                val listExercise = mutableListOf<Exercise>()
                listExercise.add(Exercise("asdasdasd","assdddddddd","ssssss",image =  Uri.parse(""),"ddddddasdsa"))

                insertTraining(training,listExercise)

            }
        }

        viewModel.viewStateTraining.observe(viewLifecycleOwner) { viewState ->
            when (viewState) {
                is ViewStateTraining.Loading -> showLoading(viewState.loading)
                is ViewStateTraining.Sucess -> sucess(viewState.idTraining)
                is ViewStateTraining.Failure -> showErro(viewState.messengerError)
                else -> {}
            }
        }

        binding.ivVoltar.setOnClickListener {
            findNavController().navigateUp()
        }

        return root
    }

    private fun showLoading(isLoading: Boolean) {
     //   binding.progressBar.isVisible = isLoading
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

    fun sucess(training: String){

        showLoading(false)

        val builder = android.app.AlertDialog.Builder(requireContext())
        with(builder)
        {
            setTitle("Treino Criado com sucesso")
            setCancelable(false) //não fecha quando clicam fora do dialog
            setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                val action =  NewTrainingFragmentDirections.actionNewTraningFragmentToTraningFragment()
                findNavController().navigate(action)
            })
            show()
        }
    }

    fun insertTraining(training: Training, listExercise:List<Exercise>){
        viewModel.insert(training,listExercise)
    }


    private fun setDadosTela(training: Training,bloqueio :Boolean = false){
//        binding.tiInKmPercorrido.setText(entregaSimples.totalKm.toString())
//        binding.tiInValorKmInformado.setText(entregaSimples.valorInformado.toString())
//        binding.tiOutraDespesa.setText(entregaSimples.valorDespExtra.toString())
//        binding.tiValorTipo.setText(entregaSimples.valorTpCalc.toString())
//        if(entregaSimples.tipoCalc >=0){
//            (binding.tiOpcao.editText as? AutoCompleteTextView)?.selectItem(items[entregaSimples.tipoCalc],entregaSimples.tipoCalc)
//            binding.tiValorTipo.visibility = View.VISIBLE
//        }
//        tipoCalculo = entregaSimples.tipoCalc
//        binding.tvValorKm.text = "Valor calculado R$: ${entregaSimples.valorEntregaCalculado}"
//
//        if(bloqueio){
//            binding.tiInKmPercorrido.isEnabled = false
//            binding.tiInValorKmInformado.isEnabled = false
//            binding.tiOutraDespesa.isEnabled = false
//            binding.tiValorTipo.isEnabled = false
//            binding.tiOpcao.isEnabled = false
//        }

    }

    fun exercc(){
        
    }

}

