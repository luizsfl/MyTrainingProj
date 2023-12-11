package com.gym.mytraining.domain.Mapper

import android.net.Uri
import com.gym.mytraining.data.model.ExerciseResponse
import com.gym.mytraining.domain.model.Exercise

fun ExerciseResponse.toExercise() =
    Exercise(
        idExercise = this.idExercise,
        idTraining = this.idTraining,
        name = this.name,
        image = Uri.parse("https://firebasestorage.googleapis.com/v0/b/mytraining-6ca4c.appspot.com/o/${this.idExercise}.png?alt=media"),
        observation = this.observation,
    )