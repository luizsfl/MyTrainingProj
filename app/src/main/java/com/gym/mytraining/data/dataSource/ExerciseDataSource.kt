package com.gym.mytraining.data.dataSource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.dataObjects
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

interface ExerciseDataSource {
    fun getAllExercise(training: Training): Flow<List<Exercise>>
    fun delete(exercise: Exercise): Flow<Exercise>
    fun update(exercise: Exercise): Flow<Exercise>
    fun insert(exercise: Exercise): Flow<Exercise>
}

class ExerciseDataSourceImp (
    private val autenticacaFirestore: FirebaseFirestore = ConfiguracaoFirebase.getFirebaseFirestore(),
    private val autenticacao: FirebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao(),
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
):ExerciseDataSource {

    override fun getAllExercise(training: Training): Flow<List<Exercise>> {
        return callbackFlow {

            val idTraining = training.idTraining

            autenticacaFirestore.collection("exercise")
                .whereEqualTo("idTraining", idTraining)
                .get()
                .addOnSuccessListener { result ->

                    val listResponse = mutableListOf<Exercise>()

                    for (document in result) {

                        val exercise = document.toObject(Exercise::class.java)!!

                        val newTraining = exercise.copy(idExercise = document.id)

                        listResponse.add(newTraining)
                    }

                    trySend(listResponse)

                }
                .addOnFailureListener {
                    val messengerErro = "getAllExercise ${it.message.toString()}"
                    trySend(error(messengerErro))
                }
            awaitClose {
                close()
            }
        }.flowOn(dispatcher)
    }

    override fun delete(item: Exercise): Flow<Exercise> {
        return callbackFlow {

            autenticacaFirestore.collection("exercise")
                .document(item.idExercise)
                .delete()
                .addOnSuccessListener { result ->
                    trySend(item)
                }
                .addOnFailureListener {
                    val messengerErro = "DeleteExercise ${it.message.toString()}"
                    trySend(error(messengerErro))
                }
            awaitClose {
                close()
            }
        }.flowOn(dispatcher)
    }

    override fun insert(item: Exercise): Flow<Exercise> {
        return flow {
            try {
                var exercise = Exercise()
                var messengerErro = ""

                autenticacaFirestore.collection("exercise")
                    .add(item)
                    .addOnFailureListener {
                        messengerErro = it.message.toString()
                    }
                    .addOnCompleteListener {
                        if (it.isSuccessful) {

                            exercise = item.copy(idExercise = it.result.id)

                        } else {
                            messengerErro = it.exception.toString()
                        }
                    }
                if (messengerErro.isEmpty())
                    emit(exercise)
                else {
                    emit(error("InsertExercise_1" + messengerErro))
                }

            } catch (e: Exception) {
                emit(error("InsertExercise_1 ${e.message.toString()}"))
            }
        }.flowOn(dispatcher)
    }
    override fun update(item: Exercise): Flow<Exercise> {
        return callbackFlow {
            autenticacaFirestore.collection("exercise")
                .document(item.idExercise)
                .set(item)
                .addOnSuccessListener { result ->
                    trySend(item)
                }
                .addOnFailureListener {
                    val messengerErro = "DeleteExercise ${it.message.toString()}"
                    trySend(error(messengerErro))
                }
            awaitClose {
                close()
            }
        }.flowOn(dispatcher)
    }
}