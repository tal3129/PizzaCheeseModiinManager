package com.pizzacheese.pizzacheesemanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pizzacheese.pizzacheesemanager.mailSender.GMailSender;

public class OrdersActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvOrder;
    Button btnPrev, btnNxt, btnPrint;
    int currentIndex=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        btnNxt = findViewById(R.id.next);
        btnNxt.setOnClickListener(this);
        btnPrev = findViewById(R.id.prev);
        btnPrev.setOnClickListener(this);
        btnPrint = findViewById(R.id.btnPrint);
        btnPrint.setOnClickListener(this);
        tvOrder = findViewById(R.id.tvOrder);


        currentIndex=MainActivity.orderList.size()-1;
        tvOrder.setText(MainActivity.orderList.get(currentIndex));
        updateButtons();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.next:
                if (currentIndex != MainActivity.orderList.size() - 1) {
                    currentIndex += 1;
                    tvOrder.setText(MainActivity.orderList.get(currentIndex));
                    updateButtons();
                }
                break;
            case R.id.prev:
                if (currentIndex != 0) {
                    currentIndex -= 1;
                    tvOrder.setText(MainActivity.orderList.get(currentIndex));
                    updateButtons();
                }
                break;
            case R.id.btnPrint:
                startPrint();
                break;
        }
    }

    public void updateButtons(){
        if(currentIndex==0)
            btnPrev.setEnabled(false);
        else
            btnPrev.setEnabled(true);

        if(currentIndex==MainActivity.orderList.size()-1)
            btnNxt.setEnabled(false);
        else
            btnNxt.setEnabled(true);
    }



    public void startPrint() {
        final String senderMail = "pizzacheeseserver@gmail.com";
        final String senderPassword = "cheesetal";
        final String receiver = MainActivity.myAppSettings.getReceiverMail();
        final String subject = "בדיקת מייל";
        final String message = MainActivity.orderList.get(currentIndex);
        Thread thread = new Thread() {
            @Override
            public void run() {
                GMailSender sender = new GMailSender(senderMail, senderPassword);
                try {
                    sender.sendMail(subject, message, receiver, receiver);
                    Toast.makeText(OrdersActivity.this, "המייל נשלח", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

}
