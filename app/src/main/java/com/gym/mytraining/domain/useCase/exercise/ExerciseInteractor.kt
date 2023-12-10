package com.gym.mytraining.domain.useCase.exercise

import com.gym.mytraining.domain.model.Exercise
import com.gym.mytraining.domain.model.Training
import kotlinx.coroutines.flow.Flow

interface ExerciseInteractor {
    fun getAll(training: Training): Flow<List<Exercise>>
    fun delete(exercise: Exercise): Flow<Exercise>
    fun update(exercise: Exercise): Flow<Exercise>

}

class ExerciseInteractorImp(
    private val exerciseGetAllUseCase: ExerciseGetAllUseCase,
    private val exerciseDeleteUseCase: ExerciseDeleteUseCase,
    private val exerciseUpdateUseCase: ExerciseUpdateUseCase,
) : ExerciseInteractor {
    override fun getAll(training:Training) = exerciseGetAllUseCase.invoke(training)
    override fun delete(exercise: Exercise) = exerciseDeleteUseCase.invoke(exercise)
    override fun update(exercise: Exercise) = exerciseUpdateUseCase.invoke(exercise)
}