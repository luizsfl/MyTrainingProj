package com.gym.mytraining.domain.useCase.exercise

import com.gym.mytraining.data.repository.ExerciseRepository
import com.gym.mytraining.domain.model.Exercise
import com.gym.mytraining.domain.model.Training

class ExerciseDeleteUseCase(
    private val exerciseRepository: ExerciseRepository
) {
    operator fun invoke(exercise: Exercise) = exerciseRepository.delete(exercise)
}