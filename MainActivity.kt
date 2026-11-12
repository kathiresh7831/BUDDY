package com.kathir.buddy

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import java.util.Locale

class MainActivity : Activity(), TextToSpeech.OnInitListener {
    private lateinit var status: TextView
    private var tts: TextToSpeech? = null
    private val speechRequest = 101
    private val micRequest = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)
        status = TextView(this).apply {
            text = "Buddy is ready. Tap Listen and speak."
            textSize = 20f
            setPadding(20, 30, 20, 30)
        }
        val listen = Button(this).apply {
            text = "Listen / பேசுங்க"
            setOnClickListener { checkMicrophone() }
        }
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 40, 24, 24)
            addView(status)
            addView(listen)
        }
        setContentView(layout)
    }

    private fun checkMicrophone() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), micRequest)
        } else listen()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == micRequest && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) listen()
    }

    private fun listen() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ta-IN")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to Buddy")
        }
        try {
            @Suppress("DEPRECATION")
            startActivityForResult(intent, speechRequest)
        } catch (e: Exception) {
            status.text = "Speech recognition unavailable on this device."
        }
    }

    @Deprecated("Using platform speech activity for prototype")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != speechRequest || resultCode != RESULT_OK) return
        val heard = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull() ?: return
        val normalized = heard.lowercase(Locale.ROOT)
        val reply = when {
            "buddy" in normalized || "படி" in normalized -> "சொல்லுங்க கதிர்! என்ன உதவி வேணும்?"
            "time" in normalized || "நேரம்" in normalized -> "இப்போ நேரம் " + java.text.SimpleDateFormat("h:mm a", Locale.ENGLISH).format(java.util.Date())
            else -> "கதிர், நீங்க சொன்னது: $heard"
        }
        status.text = "You: $heard\nBuddy: $reply"
        tts?.speak(reply, TextToSpeech.QUEUE_FLUSH, null, "buddy-response")
    }

    override fun onInit(statusCode: Int) {
        if (statusCode == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.forLanguageTag("ta-IN"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                status.text = "Install a Tamil text-to-speech voice in Android settings."
            }
        }
    }
    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}
