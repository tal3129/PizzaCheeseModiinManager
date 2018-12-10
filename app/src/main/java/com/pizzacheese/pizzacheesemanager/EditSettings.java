package com.pizzacheese.pizzacheesemanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pizzacheese.pizzacheesemanager.Types.AppSettings;

public class EditSettings extends AppCompatActivity {

    Button commit;
    EditText receiverMail;
    Switch appStatus, ccStat;
    DatabaseReference settingsReference;
    EditText edMinWheelSum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_settings);
        settingsReference = FirebaseDatabase.getInstance().getReference("AppSettings");
        edMinWheelSum = findViewById(R.id.edMinWS);
        receiverMail = (EditText) findViewById(R.id.edMail);
        appStatus = (Switch) findViewById(R.id.appStatus);
        ccStat = (Switch) findViewById(R.id.ccStatus);

        setupOldSettings();

        commit = (Button) findViewById(R.id.btnFinish);
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsReference.child("appStatus").setValue(appStatus.isChecked());
                settingsReference.child("ccStatus").setValue(ccStat.isChecked());
                settingsReference.child("receiverMail").setValue(receiverMail.getText().toString());
                String s = edMinWheelSum.getText().toString();
                settingsReference.child("minWheelSum").setValue(s.isEmpty() ? 0 : Double.valueOf(s));
                Toast.makeText(EditSettings.this, "ההגדרות הוחלו", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setupOldSettings() {
        AppSettings oldSettings = MainActivity.myAppSettings;
        if (oldSettings != null) {
            receiverMail.setText(oldSettings.getReceiverMail());
            edMinWheelSum.setText(String.valueOf(oldSettings.getMinWheelSum()));
            appStatus.setChecked(oldSettings.getAppStatus());
            ccStat.setChecked(oldSettings.isCcStatus());
        }
    }


    public void testMail(View view) {

/*
        final String senderMail = "pizzacheeseserver@gmail.com";
        final String senderPassword = "cheesetal";
        final String receiver = receiverMail.getText().toString();
        final String subject = "בדיקת מייל";
        final String message = "בדיקה";
        Thread thread = new Thread() {
            @Override
            public void run() {
                GMailSender sender = new GMailSender(senderMail, senderPassword);
                try {
                    sender.sendMail(subject, message, receiver, receiver);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        Toast.makeText(EditSettings.this, "המייל נשלח", Toast.LENGTH_SHORT).show();
*/
    }


}
