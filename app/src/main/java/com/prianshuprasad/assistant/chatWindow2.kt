package com.prianshuprasad.assistant

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_chat_window.*
import java.io.IOException
import java.lang.Thread.sleep
import java.util.*


class chatWindow2 : AppCompatActivity(), TextToSpeech.OnInitListener, RecognitionListener {

    lateinit var  speechIntent: Intent
    private lateinit var speechRecognizer: SpeechRecognizer
    private var tts: TextToSpeech? = null
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>


    private lateinit var mAdapter:adapter
    lateinit var ll:LinearLayoutManager
    var usernamei:String?=""
    private lateinit var rcview:RecyclerView

    var MessageArray: ArrayList<messageData> = arrayListOf(    )


    private var mFileName: String? = null

    // constant for storing audio permission
    val REQUEST_AUDIO_PERMISSION_CODE = 1
    private var mRecorder: MediaRecorder? = null
    private var mPlayer: MediaPlayer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_window)

        tts = TextToSpeech(this, this)

        val settings = applicationContext.getSharedPreferences("Userdata", 0)

        usernamei = settings.getString("username", "Unknown")

        MessageArray.add(messageData("Welcome $usernamei",0))


        Handler().postDelayed({
            speakOut("Welcome $usernamei")

        },1000)


        rcview= findViewById(R.id.recyclerView)





        ll= LinearLayoutManager(this)
        ll.stackFromEnd = true     // items gravity sticks to bottom
        ll.reverseLayout = false

        recyclerView.layoutManager= ll

//        mAdapter =  adapter(this)

        recyclerView.adapter= mAdapter

        mAdapter.updatenews(MessageArray)

        edittext1.hint= "Hello $usernamei"

        mic.setOnClickListener {
            speechRecognizer.startListening(speechIntent)

            Handler().postDelayed({
                speechRecognizer.stopListening()
            },5000)
//            Talk()
        }






        //
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED  )
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.RECORD_AUDIO),1)

        }
        speechRecognizer= SpeechRecognizer.createSpeechRecognizer(this);


        speechIntent= Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)


//        intent.putExtra(
//            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
//        )
//
//
//        intent.putExtra(
//            RecognizerIntent.EXTRA_LANGUAGE,
//            Locale.getDefault()
//        )
//
//
//        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak")


        // recorder******************************************************************************

speechRecognizer.setRecognitionListener(this)

speechRecognizer.startListening(speechIntent)

        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,"US-en");
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
















    }



    fun API(str:String):String{




        runOnUiThread {

            while(true)
            {
                if(!tts!!.isSpeaking)
                    break;
                sleep(1000)
            }
            MessageArray.add(messageData("Reply from API",0))
            mAdapter.updatenews(MessageArray)
            speakOut("Reply from API");



        }
        return "Reply from API"
    }


    fun Talk()
    {
        runOnUiThread {
            while(true)
            {
                if(!tts!!.isSpeaking)
                    break;
                sleep(1000)
            }


            speakOut("say Something");
            sleep(1000)
            while(true)
            {
                if(!tts!!.isSpeaking)
                    break;
                sleep(1000)
            }

            try {
                startActivityForResult(speechIntent, 1)
            } catch (e: Exception) {
                // on below line we are displaying error message in toast
                Toast.makeText(
                    this, " " + e.message, Toast.LENGTH_SHORT).show()
            }


        }

    }









    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // in this method we are checking request
        // code with our result code.
        if (requestCode == 1) {
            // on below line we are checking if result code is ok
            if (resultCode == RESULT_OK && data != null) {

                // in that case we are extracting the
                // data from our array list
                val res: ArrayList<String> =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<String>

                MessageArray.add(messageData(res[0],1))
                mAdapter.updatenews(MessageArray)
                API(res[0])


            }
        }

    }


    // Text to Specch
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.getDefault())

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language not supported!")
                Toast.makeText(this,"Something BAd Occured", Toast.LENGTH_LONG).show()
            } else {
//                btnSpeak!!.isEnabled = true
            }
        }
    }
    private fun speakOut(str:String) {
        val text = str;
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null,"")
    }

    public override fun onDestroy() {
        // Shutdown TTS when
        // activity is destroyed
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }

    fun onitemclicked(messageData: messageData) {

    }

    override fun onResume() {
        super.onResume()
//        Handler().postDelayed({
//
//            Handler().postDelayed({
//                Talk()
//            },2000)
//        },1000)



    }

    fun scrolltoPos( x:Int)
    {
        rcview.scrollToPosition(x)
    }








    private fun startRecording() {
        // check permission method is used to check
        // that the user has granted permission
        // to record nd store the audio.
        if (CheckPermissions()) {

            // setbackgroundcolor method will change
            // the background color of text view.


            // we are here initializing our filename variable
            // with the path of the recorded audio file.
            mFileName = Environment.getExternalStorageDirectory().absolutePath
            mFileName += "/AudioRecording.3gp"

            // below method is used to initialize
            // the media recorder class
            mRecorder = MediaRecorder()

            // below method is used to set the audio
            // source which we are using a mic.
            mRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)

            // below method is used to set
            // the output format of the audio.
            mRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)

            // below method is used to set the
            // audio encoder for our recorded audio.
            mRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            // below method is used to set the
            // output file location for our recorded audio
            mRecorder!!.setOutputFile(mFileName)
            try {
                // below method will prepare
                // our audio recorder class
                mRecorder!!.prepare()
            } catch (e: IOException) {
                Log.e("TAG", "prepare() failed")
            }
            // start method will start
            // the audio recording.
            mRecorder!!.start()

        } else {
            // if audio recording permissions are
            // not granted by user below method will
            // ask for runtime permission for mic and storage.
            RequestPermissions()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // this method is called when user will
        // grant the permission for audio recording.
        when (requestCode) {
            REQUEST_AUDIO_PERMISSION_CODE -> if (grantResults.size > 0) {
                val permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (permissionToRecord && permissionToStore) {
                    Toast.makeText(this,
                        "Permission Granted",
                        Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this,
                        "Permission Denied",
                        Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun CheckPermissions(): Boolean {
        // this method is used to check permission
        val result =
            ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val result1 =
            ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }

    private fun RequestPermissions() {
        // this method is used to request the
        // permission for audio recording and storage.
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_AUDIO_PERMISSION_CODE)
    }


    fun playAudio() {

        // for playing our recorded audio
        // we are using media player class.
        mPlayer = MediaPlayer()
        try {
            // below method is used to set the
            // data source which will be our file name
            mPlayer!!.setDataSource(mFileName)

            // below method will prepare our media player
            mPlayer!!.prepare()

            // below method will start our media player.
            mPlayer!!.start()

        } catch (e: IOException) {
            Log.e("TAG", "prepare() failed")
        }
    }

    fun pauseRecording() {

        mRecorder!!.stop()

        // below method will release
        // the media recorder class.
        mRecorder!!.release()
        mRecorder = null

    }

    fun pausePlaying() {
        // this method will release the media player
        // class and pause the playing of our recorded audio.
        mPlayer!!.release()
        mPlayer = null

    }

    override fun onReadyForSpeech(params: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun onBeginningOfSpeech() {

        TODO("Not yet implemented")
    }

    override fun onRmsChanged(rmsdB: Float) {
        TODO("Not yet implemented")
    }

    override fun onBufferReceived(buffer: ByteArray?) {
        TODO("Not yet implemented")
    }

    override fun onEndOfSpeech() {
        TODO("Not yet implemented")
    }

    override fun onError(error: Int) {
        TODO("Not yet implemented")
    }

    override fun onResults(results: Bundle?) {
        val matches = results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        var text = ""
        for (result in matches!!) text = """
     $result
     
     """.trimIndent()

        // text is result

        MessageArray.add(messageData(text,0))
        mAdapter.updatenews(MessageArray)

        TODO("Not yet implemented")
    }

    override fun onPartialResults(partialResults: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
        TODO("Not yet implemented")
    }


}

