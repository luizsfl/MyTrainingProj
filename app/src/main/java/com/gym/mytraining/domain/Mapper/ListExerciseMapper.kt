package com.gym.mytraining.domain.Mapper

import com.gym.mytraining.data.model.ListExerciseResponse
import com.gym.mytraining.domain.model.Exercise

fun ListExerciseResponse.toListExercise() =
    Exercise(
        idExercise = this.idExercise,
        idTraining = this.idTraining,
        name = this.name,
       // image = this.image,
        observation = this.observation
    )