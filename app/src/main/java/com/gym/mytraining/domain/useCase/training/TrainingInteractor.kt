package com.gym.mytraining.domain.useCase.training

import com.gym.mytraining.domain.model.Exercise
import com.gym.mytraining.domain.model.Training
import kotlinx.coroutines.flow.Flow

interface TrainingInteractor {
    fun insert(training: Training,listExercise:List<Exercise>): Flow<String>
    fun getAll(): Flow<List<Training>>
}
class TrainingInteractorImp(
    private val trainingInsertUseCase: TrainingInsertUseCase,
    private val trainingGetAllUseCase: TrainingGetAllUseCase,
) : TrainingInteractor {
    override fun insert(training: Training, listExercise:List<Exercise>) = trainingInsertUseCase.invoke(training,listExercise)
    override fun getAll()= trainingGetAllUseCase.invoke()
}