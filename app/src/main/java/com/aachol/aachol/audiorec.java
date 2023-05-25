package com.aachol;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.aachol.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
public class audiorec extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    private MediaRecorder recorder;
    private String outputFile;

    private List<String> contactsList;
    private EditText contactEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audiorec);

        Button recordButton = findViewById(R.id.record_button);
        Button sendButton = findViewById(R.id.send_button);
        Button addContactButton = findViewById(R.id.add_contact_button);
        Button viewContactsButton = findViewById(R.id.view_contacts_button);

        contactEditText = findViewById(R.id.contact_edit_text);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(audiorec.this, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(audiorec.this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
                } else {
                    startRecording();
                }
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRecording();
            }
        });

        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContact();
            }
        });

        viewContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewContacts();
            }
        });

        contactsList = new ArrayList<>();
    }

    private void startRecording() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "AUDIO_" + timeStamp + ".3gp";

        File directory = new File(Environment.getExternalStorageDirectory() + "/VoiceRecorder/");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        outputFile = directory.getAbsolutePath() + "/" + fileName;

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(outputFile);

        try {
            recorder.prepare();
            recorder.start();
            Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
            Toast.makeText(getApplicationContext(), "Recording stopped", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendRecording() {
        stopRecording();

        String contact = contactEditText.getText().toString().trim();

        if (contact.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter a contact", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("audio/*");
        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(outputFile)));
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Voice recording for " + contact);
        startActivity(sendIntent);
    }

    private void addContact() {
        String contact = contactEditText.getText().toString().trim();

        if (contact.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter a contact", Toast.LENGTH_SHORT).show();
            return;
        }

        contactsList.add(contact);
        Toast.makeText(getApplicationContext(), "Contact added: " + contact, Toast.LENGTH_SHORT).show();
        contactEditText.getText().clear();
    }

    private void viewContacts() {
        StringBuilder contacts = new StringBuilder("Contacts:\n");
        for (String contact : contactsList) {
            contacts.append(contact).append("\n");
        }
        Toast.makeText(getApplicationContext(), contacts.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (permissionToRecordAccepted) {
                    startRecording();
                }
                break;
        }
        if (!permissionToRecordAccepted) finish();
    }

}
