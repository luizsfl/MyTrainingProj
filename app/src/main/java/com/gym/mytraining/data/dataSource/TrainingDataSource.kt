package com.gym.mytraining.data.dataSource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.gym.mytraining.data.Config.ConfiguracaoFirebase
import com.gym.mytraining.domain.model.Exercise
import com.gym.mytraining.domain.model.Training
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface TraningDataSource {
    fun insert(training: Training,listExercise:List<Exercise>): Flow<String>
    fun getAllTraining(): Flow<List<Training>>
//    fun deleteEntregaSimples(training: Training): Flow<Training>
//    fun updateEntregaSimples(training: Training): Flow<Training>
}
class TraningDataSourceImp (
    private val autenticacaFirestore: FirebaseFirestore = ConfiguracaoFirebase.getFirebaseFirestore(),
    private val autenticacao: FirebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao(),
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
):TraningDataSource {

     fun insert2(training: Training): Flow<String>
            = callbackFlow {
        try {
            val idUsuario = autenticacao.currentUser?.uid.toString()

            val newTraining = training.copy(idUsuario=idUsuario)

            autenticacaFirestore.collection("training")
                .add(newTraining)
                .addOnFailureListener {
                    trySend(error("addTraining ${it.message.toString()}"))
                }.addOnSuccessListener { result ->
                    trySend(result.id)
                }
        } catch (e: Exception) {
            trySend(error("addtraining ${e.message.toString()}"))
        }
        awaitClose{
            close()
        }
    }.flowOn(dispatcher)

    override fun insert(training: Training,listExercise:List<Exercise>): Flow<String> {
        return flow {
            try {

                var messengerErro = ""

                val idUsuario= autenticacao.currentUser?.uid.toString()

                val newTraining = training.copy(idUsuario=idUsuario)

                autenticacaFirestore.collection("training")
                    .add(newTraining)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            //create exercicios.
                            val idTraining = it.result.id

                            for(exercise in listExercise){

                                val newExercise = exercise.copy(idTraining=idTraining)

                                autenticacaFirestore.collection("exercise")
                                    .add(newExercise)
                                    .addOnFailureListener {
                                        messengerErro = it.message.toString()
                                    }
                            }

                        }else{
                            messengerErro = it.exception.toString()
                        }

                    }.addOnFailureListener {
                        messengerErro = it.toString()
                    }

                if(messengerErro.isEmpty()){
                    emit("training 1") // training
                }else{
                    emit(error("training"+messengerErro))
                }
            } catch (e: Exception) {
                emit(error("training"+e.toString()))
            }
        }.flowOn(dispatcher)
    }

    override fun getAllTraining(): Flow<List<Training>> {
        return callbackFlow  {
            val idUsuario = autenticacao.currentUser?.uid.toString()

            autenticacaFirestore.collection("training")
                .whereEqualTo("idUsuario",idUsuario)
                .get()
                .addOnSuccessListener { result ->

                    val listResponse = mutableListOf<Training>()

                    for (document in result) {

                        val training = document.toObject(Training::class.java)!!

                        val newTraining = training.copy(idTraining = document.id)

                        listResponse.add(newTraining)
                    }

                    trySend(listResponse)

                }
                .addOnFailureListener {
                    val messengerErro = "getEntregaRota ${it.message.toString()}"
                    trySend(error(messengerErro))
                }
            awaitClose{
                close()
            }
        }.flowOn(dispatcher)
    }

//
//    fun deleteEntregaSimples(entrega: EntregaSimples): Flow<EntregaSimples> {
//        return callbackFlow  {
//
//            autenticacaFirestore.collection("entregaSimples")
//                .document(entrega.idDocument)
//                .delete()
//                .addOnSuccessListener { result ->
//                    trySend(entrega)
//                }
//                .addOnFailureListener {
//                    val messengerErro = "deleteEntregaSimples ${it.message.toString()}"
//                    trySend(error(messengerErro))
//                }
//            awaitClose{
//                close()
//            }
//        }.flowOn(dispatcher)
//    }
//
//    fun updateEntregaSimples(entrega: EntregaSimples): Flow<EntregaSimples> {
//        return this.addEntregaSimples(entrega)
//    }


}

