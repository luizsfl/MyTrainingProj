package com.gym.mytraining.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Utils() {

    private var _atualizaVersin = MutableLiveData<Long>()
    var atualizaVersin: LiveData<Long> = _atualizaVersin

    private var _typeAds = MutableLiveData<Long>()
    var typeAds: LiveData<Long> = _typeAds
    var teste = false

    fun getTypeAdsAdsense(){

        var db = Firebase.firestore

        db.collection("ADSENSE")
            .document("TIPO_ADSENSE")
            .get()
            .addOnSuccessListener { document ->
                document?.let {
                    val currentValue = it.data?.get("TIPO")
                    if(teste){
                        _typeAds.postValue(0)
                    }else{
                        _typeAds.postValue((currentValue as Long) ?: 1)
                    }
                }
            }
            .addOnFailureListener { exception ->
                _typeAds.postValue( 0)
            }
    }

    fun getVersionAppAtual(){
        var db = Firebase.firestore

        db.collection("VERSAO")
            .document("VERSAOATUAL")
            .get()
            .addOnSuccessListener { document ->
                document?.let {
                    val currentValueVersion = it.data?.get("VERSAO")
                    _atualizaVersin.postValue((currentValueVersion as Long) ?: 0)
                }
            }
            .addOnFailureListener { exception ->
                _atualizaVersin.postValue(0)
            }
    }
}