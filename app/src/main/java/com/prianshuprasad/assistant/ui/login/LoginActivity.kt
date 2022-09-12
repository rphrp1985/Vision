package com.prianshuprasad.assistant.ui.login

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.prianshuprasad.assistant.CloudString
import com.prianshuprasad.assistant.R
import com.prianshuprasad.assistant.chatWindow
import com.prianshuprasad.assistant.chatWindow2
import com.prianshuprasad.assistant.databinding.ActivityLoginBinding
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Thread.sleep
import java.util.*


class LoginActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    private lateinit var speechRecognizer:SpeechRecognizer
    private var tts: TextToSpeech? = null
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var username:EditText
    private lateinit var password:EditText;
    private lateinit var login:Button
    private lateinit var loading:ProgressBar
    lateinit var  speechIntent:Intent
   private lateinit var storage:SharedPreferences
   private lateinit var Editor:SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        storage = applicationContext.getSharedPreferences("Userdata", 0)
        Editor = storage.edit()
//        editor.putInt("homeScore", 5)

        val settings = applicationContext.getSharedPreferences("Userdata", 0)

        val homeScore = settings.getString("username", "")



        if(homeScore?.length!=0)
        {
            val intent = Intent(this,chatWindow::class.java)
            startActivity(intent)
            finish()

        }

//        Toast.makeText(this,"$homeScore",Toast.LENGTH_LONG).show()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


         username = binding.username
         password = binding.password
         login = binding.login
         loading = binding.loading



        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
//            login.isEnabled = loginState.isDataValid
               login.isEnabled= true

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

//            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(RESULT_OK)

            //Complete and destroy login activity once successful
            finish()
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.login(username.text.toString(), password.text.toString())
            }
        }

        tts = TextToSpeech(this, this)




        //
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED  )
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.RECORD_AUDIO),1)

        }
 speechRecognizer= SpeechRecognizer.createSpeechRecognizer(this);

         speechIntent= Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)


        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )


        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault()
        )


        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak")




login.setOnClickListener {
    loginHelpUsername()
}



loginHelpUsername()

clickable.setOnClickListener {
    loginHelpUsername()
}








    }

    // Login

    fun loginHelpUsername()
    {
        runOnUiThread {

            while (true) {
                if (!tts!!.isSpeaking)
                    break;
                Thread.sleep(1000)
            }

            speakOut("What is your username?");
            sleep(1000)


            while (true) {
                if (!tts!!.isSpeaking)
                    break;
                Thread.sleep(1000)
            }
           Handler().postDelayed({
               try {
                   startActivityForResult(speechIntent, 1)
               } catch (e: Exception) {
                   // on below line we are displaying error message in toast
                   Toast.makeText(
                       this, " " + e.message, Toast.LENGTH_SHORT).show()
               }
           },1000)
        }

    }
    fun loginHelpPassword()
    {
    runOnUiThread {
    while (true) {
        if (!tts!!.isSpeaking)
            break;
        sleep(1000)
    }

    speakOut("What is your Password?");
        sleep(1000)

        while(true)
        {
            if(!tts!!.isSpeaking)
                break;
            sleep(1000)
        }

        Handler().postDelayed({
            try {
                startActivityForResult(speechIntent, 2)
            } catch (e: Exception) {
                // on below line we are displaying error message in toast
                Toast.makeText(
                    this, " " + e.message, Toast.LENGTH_SHORT).show()
            }

        },1000)

}
    }



// Speech to Text
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

                var pUsername="";

                for (i in res[0]){
                    if(i==' ') continue;
                    pUsername+=i;

                }


                username.setText(pUsername.toLowerCase())
                loginHelpPassword()

            }
        }
    if (requestCode == 2) {
        // on below line we are checking if result code is ok
        if (resultCode == RESULT_OK && data != null) {

            // in that case we are extracting the
            // data from our array list
            val res: ArrayList<String> =
                data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<String>

            if(res[0].length<=5)
            {
                speakOut("Invalid Password");
                loginHelpPassword()
                return;
            }


            var pPass="";

            for (i in res[0]){
                if(i==' ') continue;
                pPass+=i;
            }

            password.setText(pPass.toLowerCase())


            val db= FirebaseFirestore.getInstance()
            val Emailcollection= db.collection("Authentication");

            GlobalScope.launch {

                val document = Emailcollection.document("${username.text.toString()}").get().await()


                if(document.exists()){
                    val pass= document.toObject(CloudString::class.java)!!


                    if(pass.str.equals(password.text.toString())){

                        Editor.putString("username",username.text.toString())
                        Editor.putString("password",password.text.toString())
                        Editor.apply();
                      openChat()


                    }

                    else{

                 speakOut("Login Failed");

                 loginHelpUsername()

                    }


                }else
                {

                 speakOut("Login Failed");


                }


            }


        }
    }
    }

    fun show(txt:String)
    {
        Toast.makeText(this,"$txt",Toast.LENGTH_LONG).show()
    }

    //open chatwindpw

    fun openChat(){
        val intent = Intent(this, chatWindow::class.java)
        startActivity(intent)
        finish()

    }




    // Text to Specch
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.getDefault())

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language not supported!")
                Toast.makeText(this,"Something BAd Occured",Toast.LENGTH_LONG).show()
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







    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==1)
        {
           if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
           {
               Toast.makeText(this,"Permission Granted",Toast.LENGTH_LONG).show()
           }else
           {
               Toast.makeText(this,"Permission Denied",Toast.LENGTH_LONG).show()
           }

        }
    }


    override fun onResume() {
        super.onResume()



    }





}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}

