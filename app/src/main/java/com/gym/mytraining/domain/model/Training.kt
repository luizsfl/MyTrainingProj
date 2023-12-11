package com.gym.mytraining.domain.model

import java.sql.Timestamp
import java.text.SimpleDateFormat

data class Training(
    val idTraining:String = "",
    val idUsuario:String = "",
    val name:String = "",
    val description:String = "",
    val date:Timestamp = Timestamp(System.currentTimeMillis()),
):java.io.Serializable{
    fun dateToString(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        return dateFormat.format(this.date)
    }
}