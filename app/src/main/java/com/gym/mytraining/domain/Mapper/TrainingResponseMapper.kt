package com.gym.mytraining.domain.Mapper

import com.gym.mytraining.data.model.TrainingResponse
import com.gym.mytraining.domain.model.Training
import java.sql.Timestamp

fun TrainingResponse.toTraining() =
    Training(
        idTraining = this.idTraining,
        idUsuario  = this.idUsuario,
        name  = this.name,
        description  = this.description,
        date   = Timestamp(this.date.time),
    )