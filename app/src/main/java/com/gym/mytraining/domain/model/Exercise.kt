package com.gym.mytraining.domain.model

import android.net.Uri

data class Exercise(
    val idExercise:String = "",
    val idTraining:String = "",
    val name:String = "",
    //val image:Uri = Uri.parse(""),
    val observation:String = "",
    val deleted :Boolean = false,
):java.io.Serializable