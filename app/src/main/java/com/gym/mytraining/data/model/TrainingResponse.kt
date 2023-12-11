package com.gym.mytraining.data.model

import java.util.Date

data class TrainingResponse(
    val idTraining:String= "",
    val idUsuario:String= "",
    val name:String= "",
    val description:String= "",
    val date: Date = Date(),
)