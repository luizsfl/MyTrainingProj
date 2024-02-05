package com.gym.mytraining.data.dataSource

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.gym.mytraining.domain.model.Usuario
import com.gym.mytraining.presentation.viewState.ViewStateUsuario
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface UsuarioDataSource {
    fun addUsuario(usuario: Usuario): Flow<ViewStateUsuario>
    fun verificarUserLogado(): Flow<Boolean>
}

class UsuarioDataSourceImp(
    private val autenticacao: FirebaseAuth = FirebaseAuth.getInstance(),
    private val autenticacaoFirestore: FirebaseFirestore = Firebase.firestore,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
):UsuarioDataSource {

        override fun addUsuario(usuario: Usuario): Flow<ViewStateUsuario> {
            return callbackFlow {
                try {
                    autenticacao.createUserWithEmailAndPassword(
                        usuario.email,usuario.senha
                    ).addOnCompleteListener{
                        if (it.isSuccessful) {
                            val idUsuario = autenticacao.currentUser
                            usuario.idUsuario = idUsuario?.uid.toString()
                            setUser(usuario)
                        }
                    }.addOnFailureListener {
                        val messengerErro = identifyErrors(it)
                        trySend(ViewStateUsuario.Failure(messengerErro))
                    }
                } catch (e: Exception) {
                    trySend(error("${e.message.toString()}"))
                }
                awaitClose {
                    close()
                }
            }.flowOn(dispatcher)
        }

    private fun ProducerScope<ViewStateUsuario>.setUser(
        usuario: Usuario
    ) {
        autenticacaoFirestore.collection("usuarios")
            .document(usuario.idUsuario)
            .set(usuario)
            .addOnSuccessListener {
                trySend(ViewStateUsuario.SucessoUsuario(usuario))
            }
            .addOnFailureListener {
                trySend(ViewStateUsuario.Failure(it.message.toString()))
            }
    }

    override fun verificarUserLogado(): Flow<Boolean>{
            return flow {
                try {
                    val usuarioLogado = autenticacao.currentUser

                    emit(usuarioLogado!=null)

                } catch (e: Exception) {
                    emit(error("VerificarUserLogado"+e.toString()))
                }
            }.flowOn(dispatcher)
        }

    private fun identifyErrors(it: java.lang.Exception): String {
        val messengerErro = when (it) {
            is FirebaseAuthWeakPasswordException -> "Digite uma senha com no minimo 6 caracteres"
            is FirebaseAuthUserCollisionException -> "E-mail já cadastrado"
            is FirebaseNetworkException -> "Usuário sem acesso a internet"
            is FirebaseAuthInvalidCredentialsException ->  "Erro nos dados: "+it.message.toString()
            else -> "Erro ao cadastrar" + it.message.toString()
        }
        return messengerErro
    }
}
