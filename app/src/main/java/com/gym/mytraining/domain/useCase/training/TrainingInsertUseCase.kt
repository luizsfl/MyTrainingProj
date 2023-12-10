package com.gym.mytraining.domain.useCase.training

import com.gym.mytraining.data.repository.TrainingRepository
import com.gym.mytraining.domain.model.Exercise
import com.gym.mytraining.domain.model.Training

class TrainingInsertUseCase(
    private val trainingRepository: TrainingRepository,
) {
    operator fun invoke(training: Training,listExercise:List<Exercise>) = trainingRepository.insert(training,listExercise)
}