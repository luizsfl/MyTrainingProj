package com.gym.mytraining.data.dataSource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.gym.mytraining.data.model.TrainingResponse
import com.gym.mytraining.domain.Mapper.toTraining
import com.gym.mytraining.domain.model.Exercise
import com.gym.mytraining.domain.model.Training
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn

interface TrainingDataSource {
    fun insert(training: Training,listExercise:List<Exercise>): Flow<String>
    fun getAllTraining(): Flow<List<Training>>
    fun delete(item: Training): Flow<Training>
    fun update(training: Training,listExercise:List<Exercise>): Flow<String>
}

class TrainingDataSourceImp (
    private val autenticacaFirestore: FirebaseFirestore = Firebase.firestore,
    private val autenticacao: FirebaseAuth = FirebaseAuth.getInstance(),
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
):TrainingDataSource {

    override fun insert(training: Training,listExercise:List<Exercise>): Flow<String> {
        return callbackFlow {
            try {

                val idUsuario= autenticacao.currentUser?.uid.toString()
                var newTraining = training.copy(idUsuario=idUsuario)

                autenticacaFirestore.collection("training")
                    .add(newTraining)
                    .addOnSuccessListener {

                            val newTraining = newTraining.copy(idTraining=it.id)

                            for(position in listExercise.indices ){

                                val exercise = listExercise.get(position)

                                val newExercise = exercise.copy(idTraining=newTraining.idTraining)
                                changeExercise(newExercise, newTraining, position, listExercise)

                            }

                        if(listExercise.size==0){
                            trySend(newTraining.idTraining)
                        }

                    }.addOnFailureListener {
                        trySend(error("${it.message.toString()}"))
                    }
            } catch (e: Exception) {
                trySend(error("${e.message.toString()}"))
            }

            awaitClose {
                close()
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

                    convertResponseToTraining(result, listResponse)

                    trySend(listResponse)

                }
                .addOnFailureListener {
                    trySend(error(it.message.toString()))
                }
            awaitClose{
                close()
            }
        }.flowOn(dispatcher)
    }


    override fun delete(item: Training): Flow<Training> {
        return callbackFlow {
            try {
                autenticacaFirestore.collection("training")
                    .document(item.idTraining)
                    .delete()
                    .addOnSuccessListener { result ->
                        trySend(item)
                    }
                    .addOnFailureListener {
                        trySend(error(it.message.toString()))
                    }
            }catch (e:Exception){
                trySend(error("${e.message.toString()}"))
            }

            awaitClose {
                close()
            }
        }.flowOn(dispatcher)
    }

    override fun update(item: Training,listExercise:List<Exercise>): Flow<String> {
        return callbackFlow {
            try {
                autenticacaFirestore.collection("training")
                    .document(item.idTraining)
                    .set(item)
                    .addOnSuccessListener {
                            for(position in listExercise.indices ){

                                val exercise = listExercise.get(position)

                                changeExercise(exercise, item, position, listExercise)

                            }

                        if(listExercise.isEmpty()){
                            trySend(item.idTraining)
                        }

                    }.addOnFailureListener {
                        trySend(error(it.message.toString()))
                    }

                awaitClose {
                    close()
                }

            } catch (e: Exception) {
                trySend(error(e.message.toString()))
            }
        }.flowOn(dispatcher)
    }

    private fun ProducerScope<String>.changeExercise(
        exercise: Exercise,
        item: Training,
        position: Int,
        listExercise: List<Exercise>
    ) {
        if (addSomenteEmTela(exercise)) {
            if (!exercise.deleted) {
                val newExercise = exercise.copy(idTraining = item.idTraining)

                autenticacaFirestore.collection("exercise")
                    .add(newExercise)
                    .addOnFailureListener {
                        trySend(error(it.message.toString()))
                    }
                    .addOnSuccessListener {
                        val newExercise = exercise.copy(idExercise = it.id)
                        uploadImage(newExercise, item, position == listExercise.lastIndex)
                    }
            } else {
                if (ultimoRegistro(position, listExercise)) {
                    trySend(item.idTraining)
                }
            }
        } else {
            if (exercise.deleted) {
                autenticacaFirestore.collection("exercise")
                    .document(exercise.idExercise)
                    .delete()
                    .addOnFailureListener {
                        trySend(error(it.message.toString()))
                    }.addOnSuccessListener {
                        if (position == listExercise.lastIndex) {
                            trySend(item.idTraining)
                        }
                    }
            } else {
                autenticacaFirestore.collection("exercise")
                    .document(exercise.idExercise)
                    .set(exercise)
                    .addOnFailureListener {
                        trySend(error(it.message.toString()))
                    }
                    .addOnSuccessListener {
                        uploadImage(exercise, item, position == listExercise.lastIndex)
                    }
            }
        }
    }

    private fun ultimoRegistro(
        position: Int,
        listExercise: List<Exercise>
    ) = position == listExercise.lastIndex

    private fun addSomenteEmTela(exercise: Exercise) =
        exercise.idExercise.isNullOrEmpty()

    private fun ProducerScope<String>.uploadImage(
        exercise: Exercise,
        training: Training,
        lastImag :Boolean,
    ) {
        if (!existImagem(exercise) &&
            !imagemCadastrada(exercise)
            ) {
            val mStorageRef = FirebaseStorage.getInstance().reference
            val uploadTask = mStorageRef.child("${exercise.idExercise}.png").putFile(exercise.image)
                .addOnSuccessListener {
                    if(lastImag){
                        trySend(training.idTraining)
                    }
                }
            uploadTask.addOnFailureListener {
                trySend(error(it.message.toString()))
            }
        }else{
            if(lastImag){
                trySend(training.idTraining)
            }
        }
    }

    private fun imagemCadastrada(exercise: Exercise) =
        exercise.image.toString().contains("https://firebasestorage.googleapis.com")

    private fun existImagem(exercise: Exercise) =
        exercise.image.toString().isEmpty()

    private fun convertResponseToTraining(
        result: QuerySnapshot,
        listResponse: MutableList<Training>
    ) {
        for (document in result) {

            val training = document.toObject(TrainingResponse::class.java)!!

            val newTraining = training.copy(idTraining = document.id).toTraining()

            listResponse.add(newTraining)
        }
    }
}

