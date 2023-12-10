package com.gym.mytraining.data.repository

import com.gym.mytraining.data.dataSource.TrainingDataSource
import com.gym.mytraining.domain.model.Exercise
import com.gym.mytraining.domain.model.Training
import kotlinx.coroutines.flow.Flow

interface TrainingRepository {
    fun insert(training: Training,listExercise:List<Exercise>): Flow<String>
    fun getAllTraining():Flow<List<Training>>
    fun delete(item: Training): Flow<Training>

}

class TrainingRepositoryImp(
    private val trainingDataSource: TrainingDataSource
): TrainingRepository {
    override fun insert(training: Training,listExercise:List<Exercise>) = trainingDataSource.insert(training,listExercise)
    override fun getAllTraining() = trainingDataSource.getAllTraining()
    override fun delete(item: Training) = trainingDataSource.delete(item)
}