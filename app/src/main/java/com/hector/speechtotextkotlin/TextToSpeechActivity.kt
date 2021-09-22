package com.hector.speechtotextkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Pair
import com.huawei.hms.mlsdk.tts.*

class TextToSpeechActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    lateinit var engine: MLTtsEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_to_speech)

        initEngine()
    }

    fun initEngine(){
        val cc = MLTtsConfig()
            .setLanguage(MLTtsConstants.TTS_LAN_DE_DE)
            .setPerson(MLTtsConstants.TTS_SPEAKER_FEMALE_EN)
            .setSpeed(1.0f)
            .setVolume(1.0f)
        engine = MLTtsEngine(cc)

        engine.setTtsCallback(object : MLTtsCallback {
            override fun onError(s: String, mlTtsError: MLTtsError) {
                Log.d(TAG, "onError $s")
            }

            override fun onWarn(s: String, mlTtsWarn: MLTtsWarn) {
                Log.d(TAG, "onWarn $s")
            }

            override fun onRangeStart(s: String, i: Int, i1: Int) {
                Log.d(TAG, "onRangeStart $s")
            }

            override fun onAudioAvailable(s: String, mlTtsAudioFragment: MLTtsAudioFragment, i: Int, pair: Pair<Int, Int>, bundle: Bundle) {
                Log.d(TAG, "onAudioAvailable $s")
            }

            override fun onEvent(s: String, i: Int, bundle: Bundle) {
                Log.d(TAG, "onEvent $s")
            }
        })
    }

    fun speechText(textToSpeech: String) {
        engine.speak(textToSpeech, MLTtsEngine.QUEUE_APPEND)
    }
}