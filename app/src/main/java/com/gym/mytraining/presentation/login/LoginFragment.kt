package com.gym.mytraining.presentation.login

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.gym.mytraining.databinding.FragmentLoginBinding
import com.gym.mytraining.presentation.viewState.ViewStateUsuario
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        val root: View = binding.root

        (activity as AppCompatActivity).supportActionBar?.hide()

        loginViewModel.VerificarUserLogado()

        binding.txtTelaCadastro.setOnClickListener{
            val action =  LoginFragmentDirections.actionLoginFragmentToNewLoginFragment()
            findNavController().navigate(action)
        }

        loginViewModel.viewStateUsuario.observe(viewLifecycleOwner) { viewState ->
            when (viewState) {
                is ViewStateUsuario.Loading -> showLoading(viewState.loading)
                is ViewStateUsuario.Logado  -> usuarioLogado(viewState.usuarioLogado)
                is ViewStateUsuario.Failure -> showErro(viewState.messengerError)
                else -> {}
            }
        }

        binding.tvPrecisaAjuda.setOnClickListener {
            showAlertDialogSendMensagem(requireContext())
        }

        binding.btEntrar.setOnClickListener {
            val email = binding.editLogin.text.toString()
            val senha = binding.editSenha.text.toString()
            var mernsagem_erro = binding.mensageErro
            if (email.isEmpty() || senha.isEmpty())
            {
                mernsagem_erro.setText("Prencha os campos de e-mail e senha")
            }else{
                AutenticarUsuário(email,senha,mernsagem_erro)
            }
        }
        return root
    }

    private fun AutenticarUsuário(email:String,senha:String,mernsagem_erro: TextView){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,senha).addOnCompleteListener {
            if (it.isSuccessful){
                IrParaTelaPrincipal()
            }else{
                var erro = it.exception

                when {
                    erro is FirebaseAuthInvalidCredentialsException -> mernsagem_erro.setText("E-mail ou senha estão incorretos")
                    erro is FirebaseNetworkException -> mernsagem_erro.setText("Sem conexão com internet")
                    erro is FirebaseAuthInvalidUserException -> mernsagem_erro.setText("E-mail não cadastrado")
                    else -> mernsagem_erro.setText("Erro ao tentar logar"+it.exception)
                }
            }
        }
    }

    private fun IrParaTelaPrincipal(){
        val builder = AlertDialog.Builder(requireContext())
        with(builder)
        {
            setTitle("Login realizado com sucesso")
            setCancelable(false) //não fecha quando clicam fora do dialog
            setPositiveButton("OK") { dialog, which ->
                val action =  LoginFragmentDirections.actionLoginFragmentToTraningFragment()
                findNavController().navigate(action)
            }
            show()
        }
    }
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
    }

    private fun usuarioLogado(logado:Boolean){
        if(logado){
            val action =  LoginFragmentDirections.actionLoginFragmentToTraningFragment()
            findNavController().navigate(action)
        }
        showLoading(false)
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

    private fun showAlertDialogSendMensagem(contextTela: Context) {

        val builder = AlertDialog.Builder(contextTela)
        builder.setTitle("Envie uma mensagem, dúvida ou melhoria")

        val input = EditText(contextTela)
        input.setHint("Digite aqui")
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, which -> }

        builder.setNegativeButton("Cancel", null)

        val dialog = builder.create()

        dialog.setOnShowListener {
            val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {

                var message = input.text.toString()

                if(message.isEmpty()){
                    val erro = "Digite a sua mensagem, dúvida ou melhoria. Campo ficou em branco"
                    input.error = erro
                }else{
                    val destinatario = "luizsfl+mytraining@hotmail.com"
                    val assunto = "Mensagem,duvida ou melhoria"

                    sendEmail(requireContext(),destinatario,assunto, message)
                    dialog.dismiss()

                }
            }
        }

        dialog.show()
    }

    private  fun  sendEmail(contextTela: Context, recipient: String, subject: String, message: String) {
        val mIntent = Intent(Intent.ACTION_SEND)

        mIntent.setPackage("com.google.android.gm");

        mIntent.data = Uri.parse( "mailto:" )
        mIntent.type = "text/plain"

        mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        mIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        mIntent.putExtra(Intent.EXTRA_TEXT, message)

        try {
            startActivity(Intent.createChooser(mIntent, "Choose Email Client..." ))
        }
        catch (e: Exception){
            Toast.makeText( contextTela , e.message, Toast.LENGTH_LONG).show()
        }

    }


}