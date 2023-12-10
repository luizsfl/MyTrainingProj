package com.gym.mytraining.domain.useCase.training

import com.gym.mytraining.data.repository.TrainingRepository

class TrainingGetAllUseCase(
    private val trainingRepository: TrainingRepository
) {
    operator fun invoke() = trainingRepository.getAllTraining()
}