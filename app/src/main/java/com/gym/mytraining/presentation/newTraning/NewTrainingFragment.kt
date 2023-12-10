package com.gym.mytraining.presentation.newTraning

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.gym.mytraining.R
import com.gym.mytraining.databinding.FragmentNewTraningBinding
import com.gym.mytraining.domain.model.Exercise
import com.gym.mytraining.domain.model.Training
import com.gym.mytraining.presentation.adapter.ExerciseAdapter

import com.gym.mytraining.presentation.traning.TrainingFragmentDirections
import com.gym.mytraining.presentation.viewState.ViewStateTraining
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.sql.Timestamp
class NewTrainingFragment : Fragment() {


    private var _binding: FragmentNewTraningBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NewTrainingViewModel by viewModel()
    private var training: Training? = null
    private val args = navArgs<NewTrainingFragmentArgs>()
    private lateinit var typeScreen: TypeOperation

    private val listExercise = mutableListOf<Exercise>()

    enum class TypeOperation {
        EDIT, VIEW, INSERT
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewTraningBinding.inflate(inflater, container, false)
        val root: View = binding.root

        training = args.value.training
        typeScreen = args.value.typeScreen

        training?.let {
            if (typeScreen == TypeOperation.EDIT) {
                setDadosTela(it)
                binding.btInsert.setText(getString(R.string.bt_edit_training))
                binding.tvTitleTraining.setText(getString(R.string.bt_edit_training))
            } else if (typeScreen == TypeOperation.INSERT) {
                setDadosTela(it, true)
                binding.btInsert.setText(getString(R.string.bt_new_training))
            }
        }

        (activity as AppCompatActivity).supportActionBar?.hide()


        binding.btInsert.setOnClickListener {

            val txName = binding.tiNome.text.toString()

            if(txName.isNullOrEmpty()) {
                binding.tiNome.error = getString(R.string.training_name_empty)
            }else{
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
                }
            }
        }


        binding.tvInsertExercise.setOnClickListener {
            insertExercise(requireContext())
        }

        binding.ivVoltar.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.viewStateTraining.observe(viewLifecycleOwner) { viewState ->
            when (viewState) {
                is ViewStateTraining.Loading -> showLoading(viewState.loading)
                is ViewStateTraining.Success ->  success()
                is ViewStateTraining.Failure -> showErro(viewState.messengerError)
                else -> {}
            }
        }


        return root
    }

    private fun showLoading(isLoading: Boolean) {
        //   binding.progressBar.isVisible = isLoading
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


    private fun setDadosTela(training: Training, bloqueio: Boolean = false) {
        binding.tiNome.setText(training.name)

        val description = if(training.description.isNullOrEmpty()) " " else training.description

        binding.tiDescription.setText(description)

    }

    private fun insertExercise(contextTela: Context) {

        val builder = AlertDialog.Builder(contextTela!!)
        val view: View
        val inflater =
            contextTela!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.layout_insert_exercise, null)


        builder.setView(view)

        builder.setPositiveButton(getString(R.string.ok),) { dialog, which -> }

        builder.setNegativeButton(getString(R.string.bt_cancel_dialog), null)

        val dialog = builder.create()

        dialog.setOnShowListener {
            val button = dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {

                val tiName = view.findViewById<TextInputEditText>(R.id.tiNomeExercise)
                val tiObservation = view.findViewById<TextInputEditText>(R.id.tiObservationExercise)

                if(tiName.text.isNullOrEmpty()){
                    val erro = getString(R.string.training_name_empty)
                    tiName.error = erro
                }else{

                    val observation =  if (tiObservation.text.toString().isEmpty()) "" else tiObservation.text.toString()

                    val exercise = Exercise(
                        name = tiName.text.toString(),
                       //image = Uri.parse(""),
                        observation = observation
                    )

                    listExercise.add(exercise)

                    setAdapter(listExercise)

                    dialog.dismiss()

                }
            }
        }

        dialog.show()

    }

    private fun setAdapter(listResponse: List<Exercise>) {
        val adapter = ExerciseAdapter(listResponse)

        adapter.onItemClick = {
//            val action =  TrainingFragmentDirections.actionTraningFragmentToExerciseFragment(it)
//            findNavController().navigate(action)
        }
        adapter.onItemClickVisualizar = {
//            val action =  ListaEntregaRotaFragmentDirections.actionListaEntregaRotaFragmentToDadosRotaFragment2(it)
//            findNavController().navigate(action)
        }

        adapter.onItemClickEditar = {
//            val action =  ListaEntregaRotaFragmentDirections.actionListaEntregaRotaFragmentToDadosVeiculoFragment(2,it)
//            findNavController().navigate(action)
        }

        adapter.onItemClickExcluir = {
            // excluirEntrega(requireContext(),it)
        }

        binding.recyclerviewExercise.adapter = adapter
        showLoading(false)
    }

}
