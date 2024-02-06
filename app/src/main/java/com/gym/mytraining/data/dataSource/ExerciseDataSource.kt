package com.gym.mytraining.data.dataSource

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.gym.mytraining.data.model.ExerciseResponse
import com.gym.mytraining.domain.Mapper.toExercise
import com.gym.mytraining.domain.model.Exercise
import com.gym.mytraining.domain.model.Training
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn

interface ExerciseDataSource {
    fun insert(exercise: Exercise): Flow<Exercise>
    fun getAllExercise(training: Training): Flow<List<Exercise>>
    fun delete(exercise: Exercise): Flow<Exercise>
    fun update(exercise: Exercise): Flow<Exercise>
}

class ExerciseDataSourceImp (
    private val autenticacaFirestore: FirebaseFirestore = Firebase.firestore,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
):ExerciseDataSource {
    override fun insert(item: Exercise): Flow<Exercise> {
        return callbackFlow {
            try {
                autenticacaFirestore.collection("exercise")
                    .add(item)
                    .addOnSuccessListener {

                        val exercise = item.copy(idExercise = it.id)

                        uploadImage(exercise)
                    }
                    .addOnFailureListener {
                        trySend(error(it.message.toString()))
                    }
            } catch (e: Exception) {
                trySend(error("${e.message.toString()}"))
            }

            awaitClose {
                close()
            }
        }.flowOn(dispatcher)
    }

    override fun getAllExercise(training: Training): Flow<List<Exercise>> {
        return callbackFlow {

            val idTraining = training.idTraining

            autenticacaFirestore.collection("exercise")
                .whereEqualTo("idTraining", idTraining)
                .get()
                .addOnSuccessListener { result ->

                    val listResponse = mutableListOf<Exercise>()

                    convertResponseToExercise(result, listResponse)

                    trySend(listResponse)

                }
                .addOnFailureListener {
                    trySend(error(it.message.toString()))
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
                    trySend(error(it.message.toString()))
                }
            awaitClose {
                close()
            }
        }.flowOn(dispatcher)
    }


    override fun update(item: Exercise): Flow<Exercise> {
        return callbackFlow {
            autenticacaFirestore.collection("exercise")
                .document(item.idExercise)
                .set(item)
                .addOnSuccessListener { result ->
                    uploadImage(item)
                }
                .addOnFailureListener {
                    trySend(error(it.message.toString()))
                }
            awaitClose {
                close()
            }
        }.flowOn(dispatcher)
    }

    private fun ProducerScope<Exercise>.uploadImage(
        exercise: Exercise
    ) {
        if (!existImage(exercise) &&
            !imagemCadastrada(exercise)
            ) {

            val mStorageRef = FirebaseStorage.getInstance().reference
            val uploadTask = mStorageRef.child("${exercise.idExercise}.png").putFile(exercise.image)
            uploadTask
                .addOnSuccessListener {
                    it.uploadSessionUri
                    trySend(exercise)
                }
                .addOnFailureListener {
                    trySend(error(it.message.toString()))
                }
        }else{
            trySend(exercise)
        }
    }

    private fun imagemCadastrada(exercise: Exercise) =
        exercise.image.toString().contains("https://firebasestorage.googleapis.com")

    private fun existImage(exercise: Exercise) =
        exercise.image.toString().isEmpty()

    private fun convertResponseToExercise(
        result: QuerySnapshot,
        listResponse: MutableList<Exercise>
    ) {
        for (document in result) {

            val exerciseResponse = document.toObject(ExerciseResponse::class.java)!!

            val newTraining = exerciseResponse.copy(idExercise = document.id).toExercise()

            listResponse.add(newTraining)
        }
    }
}