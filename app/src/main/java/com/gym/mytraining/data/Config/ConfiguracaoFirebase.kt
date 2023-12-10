package com.gym.mytraining.data.Config

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ConfiguracaoFirebase {
    companion object {
        private lateinit var autenticacao: FirebaseAuth
        private lateinit var firebase: DatabaseReference
        private lateinit var firestore: FirebaseFirestore

        //retorna instancia do firebaseauth
        fun getFirebaseAutenticacao(): FirebaseAuth {
            autenticacao = FirebaseAuth.getInstance()

            return autenticacao
        }

        //retorna a instancia do FirebaseDataBase
        fun getFirebaseDataBse(): DatabaseReference {
            firebase = FirebaseDatabase.getInstance().getReference()

            return firebase
        }

        fun getFirebaseFirestore(): FirebaseFirestore {
            firestore = Firebase.firestore
            return firestore
        }
    }
}