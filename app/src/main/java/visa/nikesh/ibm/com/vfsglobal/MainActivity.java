package visa.nikesh.ibm.com.vfsglobal;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener {


    protected static final int REQUEST_OK = 1;
    TextToSpeech t1;
    private double pitch=1.0;
    private double speed=1.0;
    private SeekBar seekPitch;
    private SeekBar seekSpeed;
    private String event;
    boolean eventfailnamefail;
    EditText name;
    EditText age;
    EditText contact;
    String build = "5";
    String passname;
    String passcontact;
    String passage;
    // From Bitbucket in Nikesh Working
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        name = (EditText) findViewById(R.id.namef);
        age = (EditText) findViewById(R.id.age);
        contact = (EditText) findViewById(R.id.contact);

        t1 = new TextToSpeech(this, this);

    }

    public void getName(View v) {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en");
        event = "name";
        try {
            startActivityForResult(i, REQUEST_OK);
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing speech to text engine.", Toast.LENGTH_LONG).show();
        }

    }

    public void getAge(View v) {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en");
        event = "age";
        try {
            startActivityForResult(i, REQUEST_OK);
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing speech to text engine.", Toast.LENGTH_LONG).show();
        }

    }

    public void getContact(View v) {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en");
        event = "contact";
        try {
            startActivityForResult(i, REQUEST_OK);
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing speech to text engine.", Toast.LENGTH_LONG).show();
        }

    }

    public void processVisaForm(View v) {

        passage = age.getText().toString();
        passcontact = contact.getText().toString();
        passname = name.getText().toString();

        ProcessVisa proc = new ProcessVisa();
        proc.execute();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_OK  && resultCode==RESULT_OK) {
            ArrayList<String> thingsYouSaid = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String tts = thingsYouSaid.get(0);
            t1.setLanguage(Locale.UK);

            if(event.equals("contact")) {
                contact.setText(tts);
                contact.setFocusable(true);
                if(tts.length()!=10) {
                   speech("Contact should be of 10 digit");
                }

            }

            if(event.equals("age")) {
                age.setText(tts);
                age.setFocusable(true);
            }

            if(event.equals("name")) {
                name.setFocusable(true);
                name.setText(tts);
            }


        }
    }


    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS) {
            t1.setLanguage(Locale.UK);
        }
    }

    private void speech(String stext) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttsGreater21(stext);
        } else {
            ttsUnder20(stext);
        }
    }

    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        t1.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId=this.hashCode() + "";
        t1.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }


    private class ProcessVisa extends AsyncTask<Void, Void, Void>
    {

        final String url="http://192.168.43.206:7800/  ?name="+passname+"&age="+passage+"&contact="+passcontact+"";
        //final String url="http://192.168.43.206:7800/esb/restService?name=vikastatic&age=23&contact=3333";
        ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        protected void onPreExecute() {

            dialog.setMessage("Processing ...");
            dialog.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {
                System.out.println("Bloody in");
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(new HttpGet(url));
            }
            catch(Exception e) {

            }

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            Toast.makeText(MainActivity.this, new StringBuilder().append("Visa Processed Successfully"),Toast.LENGTH_SHORT).show();
            Toast.makeText(MainActivity.this, new StringBuilder().append(url),   Toast.LENGTH_SHORT).show();
        }
    }

}// End oF Class
