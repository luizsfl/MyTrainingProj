package com.gym.mytraining.domain.useCase.training

import com.gym.mytraining.data.repository.TrainingRepository
import com.gym.mytraining.domain.model.Training

class TrainingDeleteUseCase(
    private val trainingRepository: TrainingRepository,
) {
    operator fun invoke(item: Training) = trainingRepository.delete(item)
}