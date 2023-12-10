package com.gym.mytraining.domain.useCase.training

import com.gym.mytraining.domain.model.Exercise
import com.gym.mytraining.domain.model.Training
import kotlinx.coroutines.flow.Flow

interface TrainingInteractor {
    fun insert(training: Training,listExercise:List<Exercise>): Flow<String>
    fun getAll(): Flow<List<Training>>

    fun delete(item: Training): Flow<Training>

}
class TrainingInteractorImp(
    private val trainingInsertUseCase: TrainingInsertUseCase,
    private val trainingGetAllUseCase: TrainingGetAllUseCase,
    private val trainingDeleteUseCase: TrainingDeleteUseCase,
    ) : TrainingInteractor {
    override fun insert(training: Training, listExercise:List<Exercise>) = trainingInsertUseCase.invoke(training,listExercise)
    override fun getAll() = trainingGetAllUseCase.invoke()

    override fun delete(item: Training) = trainingDeleteUseCase.invoke(item)

}