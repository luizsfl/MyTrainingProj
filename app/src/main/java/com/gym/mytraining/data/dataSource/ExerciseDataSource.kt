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

interface ExerciseDataSource {
    fun getAllExercise(training: Training): Flow<List<Exercise>>
    fun delete(exercise: Exercise): Flow<Exercise>

//    fun deleteEntregaSimples(training: Training): Flow<Training>
//    fun updateEntregaSimples(training: Training): Flow<Training>
}
class ExerciseDataSourceImp (
    private val autenticacaFirestore: FirebaseFirestore = ConfiguracaoFirebase.getFirebaseFirestore(),
    private val autenticacao: FirebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao(),
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
):ExerciseDataSource {

    override fun getAllExercise(training: Training): Flow<List<Exercise>> {
        return callbackFlow  {

            val idTraining = training.idTraining

            autenticacaFirestore.collection("exercise")
                .whereEqualTo("idTraining",idTraining)
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
            awaitClose{
                close()
            }
        }.flowOn(dispatcher)
    }

    override fun delete(item: Exercise): Flow<Exercise> {
        return callbackFlow  {

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
            awaitClose{
                close()
            }
        }.flowOn(dispatcher)
    }

//    fun updateEntregaSimples(entrega: EntregaSimples): Flow<EntregaSimples> {
//        return this.addEntregaSimples(entrega)
//    }


}