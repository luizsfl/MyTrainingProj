package com.gym.mytraining.domain.model

import java.sql.Timestamp

data class Training(
    val idTraining:String = "",
    val idUsuario:String = "",
    val name:String = "",
    val description:String = "",
    //val date:Timestamp = Timestamp(System.currentTimeMillis()),
):java.io.Serializable