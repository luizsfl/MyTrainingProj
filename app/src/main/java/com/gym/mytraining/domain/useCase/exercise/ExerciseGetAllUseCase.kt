package com.gym.mytraining.domain.useCase.exercise

import com.gym.mytraining.data.repository.ExerciseRepository
import com.gym.mytraining.domain.model.Training

class ExerciseGetAllUseCase(
    private val exerciseRepository: ExerciseRepository
) {
    operator fun invoke(training: Training) = exerciseRepository.gettAllExercise(training)
}