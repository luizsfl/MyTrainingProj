package com.gym.mytraining.data.repository

import com.gym.mytraining.data.dataSource.ExerciseDataSource
import com.gym.mytraining.domain.model.Exercise
import com.gym.mytraining.domain.model.Training
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    fun gettAllExercise(training: Training): Flow<List<Exercise>>
}

class ExerciseRepositoryImp(
    private val exerciseDataSource: ExerciseDataSource,
): ExerciseRepository {
    override fun gettAllExercise(training: Training) = exerciseDataSource.getAllExercise(training = training)
}