package com.gym.mytraining.data.model

import android.net.Uri

class ListExerciseResponse(
    val idExercise:String,
    val idTraining:String,
    val name:String,
    val image: Uri,
    val observation:String,
)