package com.hector.speechtotextkotlin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.hector.speechtotextkotlin.databinding.ActivityMainBinding
import com.huawei.hms.mlsdk.asr.MLAsrConstants
import com.huawei.hms.mlsdk.asr.MLAsrListener
import com.huawei.hms.mlsdk.asr.MLAsrRecognizer
import com.huawei.hms.mlsdk.common.MLApplication

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var mSpeechRecognizer: MLAsrRecognizer

    val AUDIO_CODE = 7

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MLApplication.getInstance().apiKey = "CgB6e3x9RDr+gJha0mFRQo0rGt0T490rgnJNUYO70DdIgmSx2ye8icYW8v/xtmj5UF0dfCGmQW58+o3ikMWt6qxU"

        initSpeechRecognition()


        binding.buttonListening.setOnClickListener {
            requestAudioPermission()
        }
    }

    private fun initSpeechRecognition(){
        mSpeechRecognizer = MLAsrRecognizer.createAsrRecognizer(this)

        initializeCallback()
    }

    private fun requestAudioPermission() {
        val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        ) {
            startSpeechRecognition()
        } else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)
            ) {
                ActivityCompat.requestPermissions(this, permissions, AUDIO_CODE)
                return
            }
        }
    }

    private fun startSpeechRecognition() {
        // Create an Intent to set parameters.
        val mSpeechRecognizerIntent = Intent(MLAsrConstants.ACTION_HMS_ASR_SPEECH)
        // Use Intent for recognition parameter settings.
        // Set the language that can be recognized to English. If this parameter is not set, English is recognized by default. Example: "zh-CN": Chinese; "en-US": English; "fr-FR": French; "es-ES": Spanish; "de-DE": German; "it-IT": Italian
        mSpeechRecognizerIntent.putExtra(MLAsrConstants.LANGUAGE, MLAsrConstants.LAN_ES_ES)
                // Set to return the recognition result along with the speech. If you ignore the setting, this mode is used by default. Options are as follows:
                // MLAsrConstants.FEATURE_WORDFLUX: Recognizes and returns texts through onRecognizingResults.
                // MLAsrConstants.FEATURE_ALLINONE: After the recognition is complete, texts are returned through onResults.
                .putExtra(MLAsrConstants.FEATURE, MLAsrConstants.FEATURE_WORDFLUX) // Set the application scenario. MLAsrConstants.SCENES_SHOPPING indicates shopping, which is supported only for Chinese. Under this scenario, recognition for the name of Huawei products has been optimized.
                .putExtra(MLAsrConstants.SCENES, MLAsrConstants.SCENES_SHOPPING)
        // Start speech recognition.
        mSpeechRecognizer.startRecognizing(mSpeechRecognizerIntent)
    }

    private fun initializeCallback() {
        mSpeechRecognizer.setAsrListener(object : MLAsrListener {
            override fun onResults(result: Bundle?) {
                // Text data of ASR. This API is not running in the main thread, and the return result is processed in the sub-thread.
                val text = result?.getString(MLAsrRecognizer.RESULTS_RECOGNIZED) ?: ""
                Log.i("ASRFragment", "result is $text")
                binding.textViewResult.text = text
            }

            override fun onRecognizingResults(partialResult: Bundle?) {
                // Receive the recognized text from MLAsrRecognizer. This API is not running in the main thread, and the return result is processed in the sub-thread.
                val text = partialResult?.getString("results_recognizing")
                Log.i("ASRFragment", "partialResult is $text")
                binding.textViewResult.text = text
            }

            override fun onError(errorCode: Int, errorMessage: String?) {
                // Called when an error occurs in recognition. This API is not running in the main thread, and the return result is processed in the sub-thread.
                Log.e("ASRFragment", "errorCode is $errorCode")
                Log.e("ASRFragment", "errorMessage is $errorMessage")
                binding.textViewResult.text = errorMessage
            }

            override fun onStartListening() {
                // The recorder starts to receive speech.
                Log.i("startListening", "onStartListening");
                binding.textViewResult.text = "Starting of listening"
            }

            override fun onStartingOfSpeech() {
                // The user starts to speak, that is, the speech recognizer detects that the user starts to speak.
                binding.textViewResult.text = "Starting of speech"
            }

            override fun onVoiceDataReceived(p0: ByteArray?, p1: Float, p2: Bundle?) {
                // Return the original PCM stream and audio power to the user. This API is not running in the main thread, and the return result is processed in the sub-thread.
                Log.i("onVoiceDataReceived", "");
            }

            override fun onState(p0: Int, p1: Bundle?) {
                // Notify the app status change. This API is not running in the main thread, and the return result is processed in the sub-thread.
                Log.i("onResults", p1.toString());
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            AUDIO_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()

                    startSpeechRecognition()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }


}