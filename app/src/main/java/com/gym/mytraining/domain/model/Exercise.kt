package com.gym.mytraining.domain.model

import android.net.Uri

data class Exercise(
    val idExercise:String,
    val idTraining:String,
    val name:String,
    val image:Uri,
    val observation:String,
):java.io.Serializable