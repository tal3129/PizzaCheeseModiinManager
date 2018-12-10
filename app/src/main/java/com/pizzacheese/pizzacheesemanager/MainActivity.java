package com.pizzacheese.pizzacheesemanager;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.*;
import com.google.firebase.messaging.FirebaseMessaging;
import com.pizzacheese.pizzacheesemanager.Types.AppSettings;
import com.pizzacheese.pizzacheesemanager.Types.DailyEarning;
import com.pizzacheese.pizzacheesemanager.Types.Product;
import com.pizzacheese.pizzacheesemanager.Types.SimpleNotifictaion;

import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    static ArrayList<Product> productArrayList = new ArrayList<>();
    static ArrayList<String> orderList = new ArrayList<>();
    static ArrayList<DailyEarning> earningsList = new ArrayList<>();
    static AppSettings myAppSettings;
    private ImageView btnEditProducts, btnEditDiscounts, btnEditSettings, btnEarningActivity, btnPrefProds, btnOrders, btnLocations, btnNotification;
    ProgressBar progress;
    private int productAmount, downloaded = 0;
    private DatabaseReference databaseProducts;
    int size = 0;
    private boolean first = true;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        orderList = new ArrayList<>();
        FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().subscribeToTopic("Orders");

        setupOldSettings();
        setupOrders();
        setupEarnings();
        setupProducts();

        btnEditProducts = findViewById(R.id.btnProducts);
        btnPrefProds = findViewById(R.id.btnPrefProducts);
        btnEarningActivity = findViewById(R.id.btnIncomes);
        btnEditDiscounts = findViewById(R.id.btnDiscounts);
        btnOrders = findViewById(R.id.btnOrders);
        btnEditSettings = findViewById(R.id.btnSettings);
        btnLocations = findViewById(R.id.btnLocations);
        btnNotification = findViewById(R.id.btnNotification);

        progress = findViewById(R.id.progressBar);
        progress.setProgress(0);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btnProducts:
                intent = new Intent(this, EditProducts.class);
                break;
            case R.id.btnPrefProducts:
                intent = new Intent(this, PrefProductsActivity.class);
                break;
            case R.id.btnIncomes:
                intent = new Intent(this, EarningsActivity.class);
                break;
            case R.id.btnDiscounts:
                intent = new Intent(this, EditDiscounts.class);
                break;
            case R.id.btnOrders:
                intent = new Intent(this, OrdersActivity.class);
                break;
            case R.id.btnSettings:
                intent = new Intent(this, EditSettings.class);
                break;
            case R.id.btnLocations:
                intent = new Intent(this, EditLocations.class);
                break;
            case R.id.btnNotification:
                openNotificationDialog();
                break;
        }
        if (intent != null)
            startActivity(intent);
    }

    private void openNotificationDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("התראה חדשה");
        LinearLayout lay = new LinearLayout(this);
        lay.setOrientation(LinearLayout.VERTICAL);

        TextView warning = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(50,50,100,00);
        warning.setText("לחיצה על שליחה תשלח את ההתראה לכל משתמשי האפליקציה");
        lay.addView(warning, params);

        final EditText title = new EditText(this);
        title.setHint("כותרת...");

        final EditText body = new EditText(this);
        body.setHint("תוכן...");

        lay.addView(title);
        lay.addView(body);
        alert.setView(lay);
        alert.setPositiveButton("שליחה", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                SimpleNotifictaion notification = new SimpleNotifictaion(title.getText().toString(), body.getText().toString());
                FirebaseDatabase.getInstance().getReference("messages").child(UUID.randomUUID().toString()).setValue(notification);
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
               dialog.dismiss();
            }
        });
        alert.show();
    }

    @Override
    protected void onPause() {
        try {
            databaseProducts.removeEventListener(valueEventListener);
        } catch (NullPointerException ignored) {

        }
        super.onPause();
    }

    private void finishProgress() {
        progress.setVisibility(View.INVISIBLE);

        btnPrefProds.setOnClickListener(this);
        btnEarningActivity.setOnClickListener(this);
        btnEditSettings.setOnClickListener(this);
        btnEditProducts.setOnClickListener(this);
        btnEditDiscounts.setOnClickListener(this);
        btnOrders.setOnClickListener(this);
        btnLocations.setOnClickListener(this);
        btnNotification.setOnClickListener(this);

        try {
            databaseProducts.removeEventListener(valueEventListener);
        } catch (NullPointerException ignored) {

        }
    }

    private void setupProducts() {
        databaseProducts = FirebaseDatabase.getInstance().getReference("Products");
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productAmount = (int) dataSnapshot.getChildrenCount();
                downloaded = 0;
                progress.setMax(productAmount);
                productArrayList.clear();

                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    final Product product = productSnapshot.getValue(Product.class);
                    downloaded++;
                    progress.setProgress(progress.getProgress() + 1);
                    if (downloaded == progress.getMax())
                        finishProgress();
                    productArrayList.add(product);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        databaseProducts.addValueEventListener(valueEventListener);

    }

    private void setupOrders() {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders");
        final Notification.Builder builder = new Notification.Builder(this);
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    orderList.add(snapshot.getValue(String.class));
                if (first) {
                    size = orderList.size();
                    first = false;
                }
                if (size < orderList.size()) {
                    builder.setSmallIcon(R.drawable.home_screen_logo);
                    builder.setContentTitle("הזמנה חדשה");
                    builder.setContentText("התבצעה הזמנה חדשה");
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    assert notificationManager != null;
                    notificationManager.notify(1, builder.build());
                    size = orderList.size();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    //set up the daily earning list
    private void setupEarnings() {
        earningsList.clear();
        DatabaseReference earningsRef = FirebaseDatabase.getInstance().getReference("Earnings");
        earningsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    earningsList.add(snapshot.getValue(DailyEarning.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setupOldSettings() {
        DatabaseReference settingsReference = FirebaseDatabase.getInstance().getReference("AppSettings");
        settingsReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myAppSettings = dataSnapshot.getValue(AppSettings.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                myAppSettings = new AppSettings("", true, false);
            }
        });

    }
}
