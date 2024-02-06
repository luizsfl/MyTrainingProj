package com.gym.mytraining.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContentProviderCompat.requireContext
import com.facebook.ads.AdSize
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.gym.mytraining.R
import com.gym.mytraining.databinding.ActivityMainBinding
import com.gym.mytraining.databinding.FragmentTraningBinding

class MainActivity : AppCompatActivity() {

    private val utils =  Utils()
    private var adView: AdView? = null
    lateinit var mAdView : AdView

    lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root)

    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {

        utils.atualizaVersin.observe(this){ version ->
            val pInfo = this.packageManager
                .getPackageInfo(this.packageName, 0)

            val versionCode: Int
            versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pInfo.longVersionCode.toInt()
            } else {
                pInfo.versionCode
            }

            if(version > versionCode ){
                sendPlayStore()
            }

        }

        utils.typeAds.observe(this){
            var adsenseChose = it
            var testAdsemse = ""

            if(adsenseChose == 0.toLong() ){
                adsenseChose = 2
                testAdsemse = "IMG_16_9_APP_INSTALL#"
            }

            if(adsenseChose == 1.toLong() ){
                MobileAds.initialize(this) {}
                mAdView = binding.adViewHome
                mAdView.visibility = View.VISIBLE
                val adRequest = AdRequest.Builder().build()
                mAdView.loadAd(adRequest)
            }else if(adsenseChose == 2.toLong() ){

                val adContainer = binding.adfeViewHome

                if(adContainer.visibility == View.GONE){
                    adContainer.visibility = View.VISIBLE
                    val adFacView = com.facebook.ads.AdView(this, testAdsemse+"740075444744251_740076161410846", AdSize.BANNER_HEIGHT_50)

                    adContainer.addView(adFacView)

                    adFacView.loadAd()
                }

            }
        }

        return super.onCreateView(name, context, attrs)

    }

    private fun sendPlayStore(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Atenção!!!")
        builder.setMessage("O seu aplicativo esta desatualizado.Clique em sim para atualizar")
        builder.setPositiveButton("Sim"){dialog, which ->
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=${this.packageName}")
                    )
                )
            } catch (anfe: Exception) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=${this.packageName}")
                    )
                )
            }
        }

        builder.setCancelable(false)

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun chama(){

        utils.getTypeAdsAdsense()

        utils.getVersionAppAtual()

    }

}