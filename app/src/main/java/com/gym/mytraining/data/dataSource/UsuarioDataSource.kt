package com.gym.mytraining.data.dataSource

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.gym.mytraining.data.Config.ConfiguracaoFirebase
import com.gym.mytraining.domain.model.Usuario
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface UsuarioDataSource {
    fun addUsuario(usuario: Usuario): Flow<Usuario>
    fun verificarUserLogado(): Flow<Boolean>
}

class UsuarioDataSourceImp(
        private val autenticacao: FirebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao(),
        private val autenticacaoFirestore: FirebaseFirestore = ConfiguracaoFirebase.getFirebaseFirestore(),
        private val dispatcher: CoroutineDispatcher = Dispatchers.IO
    ):UsuarioDataSource {

        private var logadoCadastro = false

        override fun addUsuario(usuario: Usuario): Flow<Usuario> {
            return flow {
                try {

                    var messengerErro = ""

                    autenticacao.createUserWithEmailAndPassword(
                        usuario.email,usuario.senha
                    ).addOnCompleteListener{
                        if (it.isSuccessful) {

                            //cadastrar o email e senha no fire auth
                            //   var  idUsuario = Base64Custom.codificarBase64(usuario.email)
                            val idUsuario = autenticacao.currentUser

                            usuario.idUsuario = idUsuario?.uid.toString()

                            autenticacaoFirestore.collection("usuarios")
                                .document(usuario.idUsuario)
                                .set(usuario)
                                .addOnFailureListener {
                                    messengerErro = it.message.toString()
                                }

                        }else{
                            messengerErro = it.exception.toString()
                        }
                    }.addOnFailureListener {
                        messengerErro = when {
                            it is FirebaseAuthWeakPasswordException ->  "Digite uma senha com no minimo 6 caracteres"
                            it is FirebaseAuthUserCollisionException -> "E-mail já cadastrado"
                            it is FirebaseNetworkException -> "Usuário sem acesso a internet"
                            else -> "Erro ao cadastrar"+it.toString()
                        }

                    }

                    if(messengerErro.isEmpty()){
                        emit(usuario)
                    }else{
                        emit(error("UserDao1"+messengerErro))
                    }
                } catch (e: Exception) {
                    emit(error("UserDao"+e.toString()))
                }
            }.flowOn(dispatcher)
        }

    override fun verificarUserLogado(): Flow<Boolean>{
            return flow {
                try {

                    val usuarioLogado = autenticacao.currentUser

                    emit(usuarioLogado!=null || logadoCadastro)

                } catch (e: Exception) {
                    emit(error("VerificarUserLogado"+e.toString()))
                }
            }.flowOn(dispatcher)
        }
    }
