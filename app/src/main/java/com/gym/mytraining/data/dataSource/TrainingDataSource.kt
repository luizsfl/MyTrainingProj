package com.gym.mytraining.data.dataSource

import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.gym.mytraining.data.Config.ConfiguracaoFirebase
import com.gym.mytraining.data.model.TrainingResponse
import com.gym.mytraining.domain.Mapper.toTraining
import com.gym.mytraining.domain.model.Exercise
import com.gym.mytraining.domain.model.Training
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.Date

interface TrainingDataSource {
    fun insert(training: Training,listExercise:List<Exercise>): Flow<String>
    fun getAllTraining(): Flow<List<Training>>

    fun delete(item: Training): Flow<Training>

    fun update(training: Training,listExercise:List<Exercise>): Flow<String>

}
class TrainingDataSourceImp (
    private val autenticacaFirestore: FirebaseFirestore = ConfiguracaoFirebase.getFirebaseFirestore(),
    private val autenticacao: FirebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao(),
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
):TrainingDataSource {

    override fun insert(training: Training,listExercise:List<Exercise>): Flow<String> {
        return flow {
            try {

                val idUsuario= autenticacao.currentUser?.uid.toString()
                val newTraining = training.copy(idUsuario=idUsuario)
                var messengerErro = ""
                var idTraining = ""

                autenticacaFirestore.collection("training")
                    .add(newTraining)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            //create exercicios.
                            idTraining = it.result.id

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
                // training
                if(messengerErro.isEmpty())
                    emit(idTraining)
                else{
                    emit(error("InsertTraining_1"+messengerErro))
                }
            } catch (e: Exception) {
                emit(error("InsertTraining_2"+e.toString()))
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

                        val training = document.toObject(TrainingResponse::class.java)!!

                        val newTraining = training.copy(idTraining = document.id).toTraining()

                        listResponse.add(newTraining)
                    }

                    trySend(listResponse)

                }
                .addOnFailureListener {
                    val messengerErro = "GetAllTreining ${it.message.toString()}"
                    trySend(error(messengerErro))
                }
            awaitClose{
                close()
            }
        }.flowOn(dispatcher)
    }


    override fun delete(item: Training): Flow<Training> {
        return callbackFlow {

            autenticacaFirestore.collection("training")
                .document(item.idTraining)
                .delete()
                .addOnSuccessListener { result ->
                    trySend(item)
                }
                .addOnFailureListener {
                    val messengerErro = "DeleteTraining ${it.message.toString()}"
                    trySend(error(messengerErro))
                }
            awaitClose {
                close()
            }
        }.flowOn(dispatcher)
    }

    override fun update(item: Training,listExercise:List<Exercise>): Flow<String> {
        return flow {
            try {
                var messengerErro = ""
                var idTraining = ""

                autenticacaFirestore.collection("training")
                    .document(item.idTraining)
                    .set(item)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {

                            for(exercise in listExercise){

                                if(exercise.idExercise.isNullOrEmpty()){
                                    if(!exercise.deleted){
                                        val newExercise = exercise.copy(idTraining=item.idTraining)
                                        autenticacaFirestore.collection("exercise")
                                            .add(newExercise)
                                            .addOnFailureListener {
                                                messengerErro = it.message.toString()
                                            }
                                            .addOnSuccessListener {

                                                val newExercise = exercise.copy(idExercise = it.id)

                                                uploadImage(newExercise)
                                            }
                                    }
                                }else{
                                    if(exercise.deleted){
                                        autenticacaFirestore.collection("exercise")
                                            .document(exercise.idExercise)
                                            .delete()
                                            .addOnFailureListener {
                                                messengerErro = it.message.toString()
                                            }
                                    }else{
                                        autenticacaFirestore.collection("exercise")
                                            .document(exercise.idExercise)
                                            .set(exercise)
                                            .addOnFailureListener {
                                                messengerErro = it.message.toString()
                                            }
                                            .addOnSuccessListener {
                                                uploadImage(exercise)
                                            }
                                    }
                                }

                            }

                        }else{
                            messengerErro = it.exception.toString()
                        }

                    }.addOnFailureListener {
                        messengerErro = it.toString()
                    }
                if(messengerErro.isEmpty())
                    emit(idTraining)
                else{
                    emit(error("UpdateTraining_1"+messengerErro))
                }
            } catch (e: Exception) {
                emit(error("UpdateTraining_2"+e.toString()))
            }
        }.flowOn(dispatcher)
    }


    private fun uploadImage(item:Exercise) {
        if(!item.image.toString().isEmpty()){
            val mStorageRef = FirebaseStorage.getInstance().reference
            val uploadTask = mStorageRef.child("${item.idExercise}.png").putFile(item.image)
            uploadTask.addOnSuccessListener {

            }.addOnFailureListener {
                Log.e("Frebase_uploadImage", it.message.toString())
            }
        }
    }
}

