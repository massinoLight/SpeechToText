package com.massino.speechtotext;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private static final String COLLECTION_NAME = "speech";
    FirebaseFirestore db;

    private TextView txvResult;
    JSONObject speech = new JSONObject();
    JSONObject speechObject = new JSONObject();
    String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
    int id=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txvResult = (TextView) findViewById(R.id.txvResult);
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();

    }

    public void getSpeechInput(View view) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txvResult.setText(result.get(0));

                    //genetation du fichier JSON avec les informatons requises
                    try {
                        speech.put("id", id);
                        speech.put("speech",result.get(0));
                        speech.put("DateTime", currentDateTimeString);
                        //speech.put("location", location);
                        Log.e(TAG, speech.getString("id"));
                        Log.e(TAG, speech.getString("speech"));
                        Log.e(TAG, speech.getString("DateTime"));
                        addSpeech(id,speech.getString("speech"),speech.getString("DateTime"));

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                }
                break;
        }
        id++;
    }

    public void addSpeech(int id,String leSpeech,String date) {
        // [START add_ada_lovelace]
        // Create a new user with a first and last name
        Map<String, Object> speech = new HashMap<>();
        speech.put("id", id);
        speech.put("speech", leSpeech);
        speech.put("DateTime", date);

        // Add a new document with a generated ID
        db.collection("speech")
                .add(speech)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.e(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }




}