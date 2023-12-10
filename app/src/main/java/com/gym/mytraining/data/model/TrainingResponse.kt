package com.gym.mytraining.data.model

import java.sql.Timestamp

data class TrainingResponse(
    val idTraining:String= "",
    val idUsuario:String= "",
    val name:String= "",
    val description:String= "",
    val date: Timestamp = Timestamp(System.currentTimeMillis()),
)