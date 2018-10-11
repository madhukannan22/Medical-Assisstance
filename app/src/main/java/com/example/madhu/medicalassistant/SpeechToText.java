package com.example.madhu.medicalassistant;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class SpeechToText extends AppCompatActivity {

    private TextView txvResult , answer;
    private static final String TAG = "ViewDatabase";
    private FirebaseDatabase mdatabase;
    private  DatabaseReference mref ;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();



    private  String userID;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //<< this
        setContentView(R.layout.speechtotext);
        txvResult = (TextView) findViewById(R.id.txvResult);
        answer = (TextView) findViewById(R.id.answer);
        // Write a message to the database



    }
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);

    }


    public void getSpeechInput() {

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
                    String textinput = result.get(0);
                    String temp ="";
                    String doctorname ="";
                    int indexno = 0;
                    for(int i = 0;i<result.get(0).length();i++)
                    {
                        if(textinput.charAt(i) != ' ')
                        {
                            indexno = i ;
                            temp = temp + textinput.charAt(i);
                        }else break;

                    }
                    indexno++;
                    if(temp.equals("டாக்டர்"))
                    {
                        for(int j =indexno+1;j<textinput.length();j++)
                        {
                            doctorname = doctorname + textinput.charAt(j);
                        }
                        doctoravailability(doctorname);

                    }else if (!temp.equals("டாக்டர்")){firstaidprovider(result.get(0));}
                }
                break;
        }
    }
    void doctoravailability(String doctorname)
    {
        DatabaseReference mostafa = ref.child("Doctor").child(doctorname).child("time");
        mostafa.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String availtime = dataSnapshot.getValue(String.class);
                TextView answer = (TextView) findViewById(R.id.answer);
                if(availtime == null)
                {
                    Toast toast = Toast.makeText(getApplicationContext(),"Entry not available ! !",Toast.LENGTH_SHORT);
                }
                else answer.setText( "வருகை மணி நேரம் : " +availtime);
                //do what you want with the email
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    void firstaidprovider(String s)
    {
        // Database queries to retrieve the firstaid according to the health problem
        //To check the health problem and its remedies are present in the database

        //Assuming that தலைவலி is a valid health problem
        DatabaseReference mostafa = ref.child("Firstaid").child(s).child("Medicine");
        mostafa.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String Medicine = dataSnapshot.getValue(String.class);
                TextView answer = (TextView) findViewById(R.id.answer);
                if(Medicine == null)
                {
                    answer.setText( " தவறான உள்ளீடு !");
                }
                else answer.setText( "Medicine : " +Medicine);
                //do what you want with the email
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    void clear(View view)
    {
        answer.setText("");
        txvResult.setText("");
        getSpeechInput();
    }

}