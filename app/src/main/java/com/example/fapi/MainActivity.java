package com.example.fapi;

import static com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.CharEncoding.UTF_8;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.auth.UsernamePasswordCredentials;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpGet;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.auth.BasicScheme;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    TextView texter;
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private WebView myWebView;
    boolean b = false;
    String a;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        callbackManager = CallbackManager.Factory.create();

        texter = findViewById(R.id.textView);

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile, email"));

        loginButton.registerCallback((CallbackManager) callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Test", "Nice!");
            }

            @Override
            public void onCancel() {
                Log.d("Test", "Nah :/");
                myWebView.loadUrl("https://api.instagram.com/oauth/authorize" +
                        "?client_id=1079924572594402" +
                        "&client_secret=a3f31b213283bf291160e3a1a0efb24e" +
                        "&redirect_uri=https://localhost:8081/" +
                        "&scope=user_profile,user_media" +
                        "&response_type=code");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("Test", "Error"  + error.toString());
            }
        });

        myWebView = new WebView(this);
        //byte[] postData = new byte[10];
        String my_code;
        String postData = "code=";
        myWebView.postUrl("https://api.instagram.com/oauth/authorize" +
                "?client_id=1079924572594402" +
                "&client_secret=a3f31b213283bf291160e3a1a0efb24e" +
                "&redirect_uri=https://socialmediapublisher.github.io/" +
                "&scope=user_profile,user_media" +
                "&response_type=code",postData.getBytes());

        Log.d("sdfsdf",postData);
        WebSettings settings = myWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                // do your handling codes here, which url is the requested url
                // probably you need to open that url rather than redirect:
                view.loadUrl(url);
                if(url.startsWith("https://l.instagram.com/?u=https%3A%2F%2Fsocialmediapublisher.github.io")){
                    a = myWebView.getUrl();
                    a = a.replace("https://l.instagram.com/?u=https%3A%2F%2Fsocialmediapublisher.github.io%2F%3Fcode%3D", "");
                    a = a.replaceAll("%23_&e=.*","");
                    Log.d("971",a);
                    b=true;
                    getAccessToken(a);


                    //thread.start();

                }


                return true; // then it is not handled by default action
            }
        });



        //CallAPI callAPI = new CallAPI();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://api.instagram.com/oauth/authorize" +
                "?client_id=1079924572594402" +
                "&client_secret=a3f31b213283bf291160e3a1a0efb24e" +
                "&redirect_uri=https://socialmediapublisher.github.io/" +
                "&scope=user_profile,user_media" +
                "&response_type=code";

// Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d("", myWebView.getUrl());

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);








        WebView webview = new WebView(this);
        setContentView(webview);



        setContentView(myWebView);

    }

    String Url = "https://api.instagram.com/oauth/authorize" +
            "?client_id=1079924572594402" +
            "&client_secret=a3f31b213283bf291160e3a1a0efb24e" +
            "&redirect_uri=https://socialmediapublisher.github.io/" +
            "&scope=user_profile,user_media" +
                "&response_type=code";
    String query;



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Log.d("Test", object.toString());

            }
        });

        Bundle bundle = new Bundle();
        bundle.putString("fields", "gender, name, id, first_name, last_name");

        request.setParameters(bundle);
        request.executeAsync();
    };
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if(currentAccessToken == null)
                {
                    LoginManager.getInstance().logOut();
                }
            }
        };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }

    public static String getRequest() {
        StringBuffer stringBuffer = new StringBuffer("");
        BufferedReader bufferedReader = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet();

            URI uri = new URI("http://sample.campfirenow.com/rooms.xml");
            httpGet.setURI(uri);
            httpGet.addHeader(BasicScheme.authenticate(
                    new UsernamePasswordCredentials("user", "password"),
                    UTF_8, false));

            HttpResponse httpResponse = httpClient.execute(httpGet);
            InputStream inputStream = httpResponse.getEntity().getContent();
            bufferedReader = new BufferedReader(new InputStreamReader(
                    inputStream));

            String readLine = bufferedReader.readLine();
            while (readLine != null) {
                stringBuffer.append(readLine);
                stringBuffer.append("\n");
                readLine = bufferedReader.readLine();
            }
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    // TODO: handle exception
                }
            }
        }
        return "";
    }
    private String streamToString(InputStream is) throws IOException {
        String str = "";

        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is));

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                reader.close();
            } finally {
                is.close();
            }

            str = sb.toString();
        }

        return str;
    }
    private static final String TAG = "InstagramAPI";
    private void getAccessToken(final String code) {
        Log.d("Getting access token", "Getting access token ...");

        new Thread() {
            @Override
            public void run() {

                Log.i(TAG, "Getting access token");
                int what = 2;
                try {
                    URL url = new URL("https://api.instagram.com/oauth/access_token");
                    // URL url = new URL(mTokenUrl + "&code=" + code);
                    Log.i(TAG, "Opening Token URL " + url.toString());
                    HttpURLConnection urlConnection = (HttpURLConnection) url
                            .openConnection();
                        urlConnection.setRequestMethod("POST");
                    urlConnection.setDoInput(true);
                    //urlConnection.setDoOutput(true);
                     //urlConnection.connect();
                    OutputStreamWriter writer = new OutputStreamWriter(
                            urlConnection.getOutputStream());
                    writer.write("client_id=" + "1079924572594402" + "&client_secret="
                            + "a3f31b213283bf291160e3a1a0efb24e" + "&grant_type=authorization_code"
                            + "&redirect_uri=" + "https://socialmediapublisher.github.io/" + "&code=" + a);
                    writer.flush();
                    String response = streamToString(urlConnection
                            .getInputStream());
                    Log.i(TAG, "response " + response);
                    JSONObject jsonObj = (JSONObject) new JSONTokener(response)
                            .nextValue();

                    String mAccessToken = jsonObj.getString("access_token");
                    // Log.i(TAG, "Got access token: " + mAccessToken);

                    id = jsonObj.getString("user_id");




                    new Thread() {
                        @Override
                        public void run() {

                            Log.i(TAG, "Getting Permanent token");
                            int what = 2;
                            try {
                                URL url2 = new URL("https://graph.instagram.com/"  + id + "?" + "fields=" + "id,username,email" + "&access_token=" + mAccessToken);
                                // URL url = new URL(mTokenUrl + "&code=" + code);
                                Log.i(TAG, "Opening Permanent Token URL " + url2.toString() );
                                HttpURLConnection urlConnection2 = (HttpURLConnection) url2
                                        .openConnection();
                                urlConnection2.setRequestMethod("GET");
                                urlConnection2.setDoInput(true);
                                //urlConnection2.setDoOutput(true);
                                urlConnection2.connect();

                                String response2 = streamToString(urlConnection2
                                        .getInputStream());
                                Log.i(TAG, "response " + response2);
                                JSONObject jsonObj2
                                        = (JSONObject) new JSONTokener(response2)
                                        .nextValue();
                                String name5 = jsonObj2.getString(
                                        "username");
                                String userImage = jsonObj2.getString(
                                        "id");

                                myWebView.setAlpha(0);

                                texter.setText(name5);





                            } catch (Exception ex) {
                                //what = WHAT_ERROR;
                                ex.printStackTrace();
                            }

                            //mHandler.sendMessage(mHandler.obtainMessage(what, 1, 0));
                        }
                    }.start();

                } catch (Exception ex) {
                    //what = WHAT_ERROR;
                    ex.printStackTrace();
                }

                //mHandler.sendMessage(mHandler.obtainMessage(what, 1, 0));
            }
        }.start();
    }



}
