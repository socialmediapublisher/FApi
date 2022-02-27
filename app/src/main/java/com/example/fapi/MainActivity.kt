package com.example.fapi

import com.facebook.CallbackManager.Factory.create
/*import com.facebook.login.LoginResult.accessToken
import com.facebook.AccessToken.token
import com.facebook.FacebookException.toString
import com.facebook.CallbackManager.onActivityResult
import com.facebook.GraphRequest.parameters
import com.facebook.GraphRequest.executeAsync*/
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import android.widget.ViewFlipper
import android.widget.TextView
import com.facebook.CallbackManager
import com.facebook.login.widget.LoginButton
import android.webkit.WebView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseUser
import android.os.Bundle
import com.example.fapi.R
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode
import android.view.View.OnFocusChangeListener
import android.annotation.SuppressLint
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.example.fapi.MainActivity
import com.google.firebase.database.DatabaseError
import com.facebook.FacebookCallback
import com.facebook.login.LoginResult
import com.google.firebase.auth.AuthResult
import android.widget.Toast
import com.facebook.FacebookException
import android.webkit.WebSettings
import android.webkit.WebViewClient
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.android.volley.toolbox.StringRequest
import com.android.volley.VolleyError
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Button
import com.android.volley.Request
import com.facebook.GraphRequest
import com.facebook.AccessToken
import org.json.JSONObject
import com.facebook.GraphResponse
import com.facebook.AccessTokenTracker
import com.facebook.login.LoginManager
import kotlin.Throws
import org.json.JSONTokener
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpGet
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.auth.BasicScheme
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.auth.UsernamePasswordCredentials
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.CharEncoding
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.auth.FirebaseAuth as FirebaseAuth1

class MainActivity : AppCompatActivity() {
    var myRef: DatabaseReference? = null
    private val TAG = "InstagramAPI"
    var vf: ViewFlipper? = null
    var texter: TextView? = null
    private var mAuth: FirebaseAuth1? = null
    private var callbackManager: CallbackManager? = null
    private var loginButton: LoginButton? = null
    private var myWebView: WebView? = null
    var b = false
    var a: String? = null
    private var id: String? = null
    private var datePick: TextInputEditText? = null
    private var startTimePick: TextInputEditText? = null
    private var endTimePick: TextInputEditText? = null
    private var database: FirebaseDatabase? = null
    private var plus: Button? = null
    private var goBack: Button? = null
    private var validate: Button? = null
    private var currentUser: FirebaseUser? = null
    private var reasonPicked: TextInputEditText? = null
    private var userToken = "0"
    var finalToken : String = "";
    @SuppressLint("SetJavaScriptEnabled", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        callbackManager = create()
        texter = findViewById(R.id.textView)

        datePick = findViewById(R.id.date_pick)
        with(datePick) {
            StrictMode.setThreadPolicy(policy)
            callbackManager = create()
            texter = findViewById(R.id.textView)

            this?.setOnFocusChangeListener(OnFocusChangeListener { view, b ->
                if (b) {
                    val ma = MaterialDatePicker.Builder.datePicker()
                            .setTitleText("Select date").setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                            .build()
                    ma.show(supportFragmentManager, "t")

                    ma.addOnPositiveButtonClickListener(MaterialPickerOnPositiveButtonClickListener<Long?> { selection ->
                        val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                        utc.timeInMillis = selection!!
                        val format = SimpleDateFormat("yyyy-MM-dd")
                        val formatted = format.format(utc.time)
                        setText(formatted)
                    })
                }
            })
        }
        startTimePick = findViewById(R.id.start_time_pick)
        startTimePick?.setOnFocusChangeListener(OnFocusChangeListener { view, b ->
            if (b) {
                var ma: MaterialTimePicker? = null
                try {
                    ma = MaterialTimePicker.Builder::class.java.newInstance().setTimeFormat(TimeFormat.CLOCK_24H).setTitleText("Starting Time").build()
                    ma.show(supportFragmentManager, "t")
                    val finalMa: MaterialTimePicker = ma
                    ma.addOnPositiveButtonClickListener(View.OnClickListener {
                        var time: String
                        val hour = finalMa.hour
                        time = if (hour < 10) "0$hour" else "" + hour
                        val mins = finalMa.minute
                        time = if (mins < 10) "$time:0$mins" else "$time:$mins"
                        startTimePick?.setText(time)
                    })
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: InstantiationException) {
                    e.printStackTrace()
                }
            }
        })
        endTimePick = findViewById(R.id.end_time_pick)
        endTimePick?.setOnFocusChangeListener(OnFocusChangeListener { view, b ->
            if (b) {
                var ma: MaterialTimePicker? = null
                try {
                    ma = MaterialTimePicker.Builder::class.java.newInstance().setTimeFormat(TimeFormat.CLOCK_24H).setTitleText("Ending Time").build()
                    ma!!.show(supportFragmentManager, "t")
                    val finalMa: MaterialTimePicker = ma as MaterialTimePicker
                    ma!!.addOnPositiveButtonClickListener {
                        var time: String
                        val hour = finalMa.hour
                        time = if (hour < 10) "0$hour" else "" + hour
                        val mins = finalMa.minute
                        time = if (mins < 10) "$time:0$mins" else "$time:$mins"
                        endTimePick?.setText(time)
                    }
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: InstantiationException) {
                    e.printStackTrace()
                }
            }
        })
        reasonPicked = findViewById(R.id.reason_tags_pick)
        vf = findViewById<View>(R.id.vf) as ViewFlipper
        plus = findViewById(R.id.button5)
        goBack = findViewById(R.id.button6)
        validate = findViewById(R.id.button4)
        goBack?.setOnClickListener(View.OnClickListener { vf!!.displayedChild = vf!!.displayedChild - 1 })
        plus?.setOnClickListener(View.OnClickListener { vf!!.displayedChild = vf!!.displayedChild + 1 })
        validate?.setOnClickListener(View.OnClickListener {
            if (currentUser == null) {
                //currentUser = mAuth.getCurrentUser();
                vf!!.displayedChild = 0
                Log.d("null", "null")
            } else if (datePick?.getText().toString() !== "" && startTimePick?.getText().toString() !== "" && endTimePick?.getText().toString() !== "") {
                myRef!!.setValue("Booking/User_" + currentUser!!.uid + "/" + datePick?.getText() + "_" + startTimePick + "_" + endTimePick + "_" + reasonPicked?.getText())
                vf!!.displayedChild = vf!!.displayedChild - 1
            }
        })
        database = FirebaseDatabase.getInstance()
        myRef = database!!.getReference("classroombooker-default-rtdb")
        myRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue(String::class.java)
                Log.d(TAG, "Value is: $value")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
        mAuth = FirebaseAuth1.getInstance()
        if (currentUser != null) {
            //reload();
        }
        loginButton = findViewById<View>(R.id.login_button) as LoginButton
        loginButton?.setReadPermissions(Arrays.asList("public_profile, email"))
        loginButton?.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(lloginResult: LoginResult?) {
                if(lloginResult != null) {
                    val loginResult = lloginResult;
                    Log.d("Test", "Nice!")
                    userToken = loginResult.accessToken.token;
                    finalToken?.let {
                        mAuth!!.signInWithCustomToken(this@MainActivity.userToken)
                                .addOnCompleteListener(this@MainActivity) { task ->
                                    if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithCustomToken:success")
                                        val usdfer = mAuth!!.currentUser
                                        //updateUI(user)
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithCustomToken:failure", task.exception)
                                        Toast.makeText(baseContext, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show()
                                        //updateUI(null)
                                    }
                                }
                    }
                }
                /*val lastCustomToken = mAuth!!.createCustomToken(userToken)
                mAuth!!.createCustomToken(lastCustomToken)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(this@MainActivity, "Authentication Succeeded.",
                                        Toast.LENGTH_SHORT).show()
                                Log.d(TAG, "signInWithCustomToken:success")
                                //currentUser = mAuth!!.currentUser
                                vf!!.showNext()
                            }
                            else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithCustomToken:failure", task.exception)
                                Toast.makeText(this@MainActivity, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show()
                            }
                        }
                }*/


            }

            override fun onCancel() {
                userToken = "0"
                Log.d("Test", "Nah :/")
                myWebView!!.loadUrl("https://api.instagram.com/oauth/authorize" +
                        "?client_id=1079924572594402" +
                        "&client_secret=a3f31b213283bf291160e3a1a0efb24e" +
                        "&redirect_uri=https://localhost:8081/" +
                        "&scope=user_profile,user_media" +
                        "&response_type=code")
            }

            override fun onError(error: FacebookException) {
                Log.d("Test", "Error$error")
                userToken = "0"
            }
        })

        myWebView = findViewById(R.id.webview)
        //byte[] postData = new byte[10];
        var my_code: String
        val postData = "code="
        myWebView?.postUrl("https://api.instagram.com/oauth/authorize" +
                "?client_id=1079924572594402" +
                "&client_secret=a3f31b213283bf291160e3a1a0efb24e" +
                "&redirect_uri=https://socialmediapublisher.github.io/" +
                "&scope=user_profile,user_media" +
                "&response_type=code", postData.toByteArray())
        Log.d("sdfsdf", postData)
        val settings = myWebView?.settings
        settings?.javaScriptEnabled = true
        myWebView?.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                // do your handling codes here, which url is the requested url
                // probably you need to open that url rather than redirect:
                view.loadUrl(url)
                if (url.startsWith("https://l.instagram.com/?u=https%3A%2F%2Fsocialmediapublisher.github.io")) {
                    a = myWebView?.getUrl()
                    a = a!!.replace("https://l.instagram.com/?u=https%3A%2F%2Fsocialmediapublisher.github.io%2F%3Fcode%3D", "")
                    a = a!!.replace("%23_&e=.*".toRegex(), "")
                    Log.d("971", a!!)
                    getAccessToken(a!!)


                    //thread.start();
                }
                return true // then it is not handled by default action
            }
        })


        //CallAPI callAPI = new CallAPI();
        val queue = Volley.newRequestQueue(this)
        val url = "https://api.instagram.com/oauth/authorize" +
                "?client_id=1079924572594402" +
                "&client_secret=a3f31b213283bf291160e3a1a0efb24e" +
                "&redirect_uri=https://socialmediapublisher.github.io/" +
                "&scope=user_profile,user_media" +
                "&response_type=code"

// Request a string response from the provided URL.
        val stringRequest = StringRequest(Request.Method.POST, url,
                { // Display the first 500 characters of the response string.
                    Log.d("", myWebView?.getUrl()!!)
                }) { }

// Add the request to the RequestQueue.
        queue.add(stringRequest)


        //set
        //WebView webview = new WebView(this);
        //setContentView(webview);


        //setContentView(myWebView);
        //addContentView(myWebView);
        //setContentView(R.layout.activity_main);
    }

    var Url = "https://api.instagram.com/oauth/authorize" +
            "?client_id=" + R.string.instagram_app_id +
            "&client_secret=" + R.string.instagram_app_secret_key +
            "&redirect_uri=" + R.string.instagram_app_redirect_uri +
            "&scope=user_profile,user_media" +
            "&response_type=code"
    var query: String? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        val request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), object : GraphRequest.GraphJSONObjectCallback {
            override fun onCompleted(`object`: JSONObject?, response: GraphResponse?) {
                Log.d("Test", `object`.toString())
            }
        })
        val bundle = Bundle()
        bundle.putString("fields", "gender, name, id, first_name, last_name")
        request.parameters = bundle
        request.executeAsync()
    }

    var accessTokenTracker: AccessTokenTracker = object : AccessTokenTracker() {
        override fun onCurrentAccessTokenChanged(oldAccessToken: AccessToken, currentAccessToken: AccessToken) {
            if (currentAccessToken == null) {
                LoginManager.getInstance().logOut()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        accessTokenTracker.stopTracking()
    }

    @Throws(IOException::class)
    private fun streamToString(`is`: InputStream?): String {
        var str = ""
        if (`is` != null) {
            val sb = StringBuilder()
            var line: String?
            try {
                val reader = BufferedReader(
                        InputStreamReader(`is`))
                while (reader.readLine().also { line = it } != null) {
                    sb.append(line)
                }
                reader.close()
            } finally {
                `is`.close()
            }
            str = sb.toString()
        }
        return str
    }

    private fun getAccessToken(code: String) {
        Log.d("Getting access token", "Getting access token ...")
        object : Thread() {
            override fun run() {
                Log.i(TAG, "Getting access token")
                val what = 2
                try {
                    val url = URL("https://api.instagram.com/oauth/access_token")
                    // URL url = new URL(mTokenUrl + "&code=" + code);
                    Log.i(TAG, "Opening Token URL $url")
                    val urlConnection = url
                            .openConnection() as HttpURLConnection
                    urlConnection.requestMethod = "POST"
                    urlConnection.doInput = true
                    //urlConnection.setDoOutput(true);
                    //urlConnection.connect();
                    val writer = OutputStreamWriter(
                            urlConnection.outputStream)
                    writer.write("client_id=" + "1079924572594402" + "&client_secret="
                            + "a3f31b213283bf291160e3a1a0efb24e" + "&grant_type=authorization_code"
                            + "&redirect_uri=" + "https://socialmediapublisher.github.io/" + "&code=" + a)
                    writer.flush()
                    val response = streamToString(urlConnection
                            .inputStream)
                    Log.i(TAG, "response $response")
                    val jsonObj = JSONTokener(response)
                            .nextValue() as JSONObject
                    val mAccessToken = jsonObj.getString("access_token")
                    // Log.i(TAG, "Got access token: " + mAccessToken);
                    var id = jsonObj.getString("user_id")
                    userToken = if (id != null) mAccessToken else "0"
                    object : Thread() {
                        override fun run() {
                            Log.i(TAG, "Getting Permanent token")
                            val what = 2
                            try {
                                val url2 = URL("https://graph.instagram.com/$id?fields=id,username,email&access_token=$mAccessToken")
                                // URL url = new URL(mTokenUrl + "&code=" + code);
                                Log.i(TAG, "Opening Permanent Token URL $url2")
                                val urlConnection2 = url2
                                        .openConnection() as HttpURLConnection
                                urlConnection2.requestMethod = "GET"
                                urlConnection2.doInput = true
                                //urlConnection2.setDoOutput(true);
                                urlConnection2.connect()
                                val response2 = streamToString(urlConnection2
                                        .inputStream)
                                Log.i(TAG, "response $response2")
                                val jsonObj2 = JSONTokener(response2)
                                        .nextValue() as JSONObject
                                val name5 = jsonObj2.getString(
                                        "username")
                                val userImage = jsonObj2.getString(
                                        "id")
                                texter!!.text = name5
                                vf!!.showNext()
                            } catch (ex: Exception) {
                                //what = WHAT_ERROR;
                                ex.printStackTrace()
                            }

                            //mHandler.sendMessage(mHandler.obtainMessage(what, 1, 0));
                        }
                    }.start()
                } catch (ex: Exception) {
                    //what = WHAT_ERROR;
                    ex.printStackTrace()
                }

                //mHandler.sendMessage(mHandler.obtainMessage(what, 1, 0));
            }
        }.start()
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        //currentUser = mAuth.getCurrentUser();
    }

    companion object {
        // TODO: handle exception
        // TODO: handle exception
        val request: String
            get() {
                val stringBuffer = StringBuffer("")
                var bufferedReader: BufferedReader? = null
                try {
                    val httpClient: HttpClient = DefaultHttpClient()
                    val httpGet = HttpGet()
                    val uri = URI("http://sample.campfirenow.com/rooms.xml")
                    httpGet.uri = uri
                    httpGet.addHeader(BasicScheme.authenticate(
                            UsernamePasswordCredentials("user", "password"),
                            CharEncoding.UTF_8, false))
                    val httpResponse = httpClient.execute(httpGet)
                    val inputStream = httpResponse.entity.content
                    bufferedReader = BufferedReader(InputStreamReader(
                            inputStream))
                    var readLine = bufferedReader.readLine()
                    while (readLine != null) {
                        stringBuffer.append(readLine)
                        stringBuffer.append("\n")
                        readLine = bufferedReader.readLine()
                    }
                } catch (e: Exception) {
                    // TODO: handle exception
                } finally {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close()
                        } catch (e: IOException) {
                            // TODO: handle exception
                        }
                    }
                }
                return ""
            }

    }
}
