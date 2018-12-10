package com.pizzacheese.pizzacheesemanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pizzacheese.pizzacheesemanager.Types.Discount;
import com.pizzacheese.pizzacheesemanager.Types.Product;

import java.util.ArrayList;

public class addDiscount extends AppCompatActivity {
    Spinner spinnerDiscounted;
    Spinner spinnerDiscounter;
    EditText newPrice, edAmount;
    Button btnCancel,btnFinish;
    DatabaseReference databaseDiscounts;
    final ArrayList<Product>newProductList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_discount);

        btnFinish=(Button)findViewById(R.id.btnFinish);
        btnCancel=(Button)findViewById(R.id.btnCancel);
        databaseDiscounts= FirebaseDatabase.getInstance().getReference("Discounts");
        edAmount=(EditText)findViewById(R.id.edAmount);
        newPrice=(EditText) findViewById(R.id.newPrice);
        spinnerDiscounted=(Spinner)findViewById(R.id.spinnerDiscounted);
        spinnerDiscounter=(Spinner)findViewById(R.id.spinnerDiscounter);


        ArrayList<String>arraySpinner=new ArrayList<>();

        for(Product p:MainActivity.productArrayList)
            if(!p.getType().equals("תוספות")) {
                newProductList.add(p);
                arraySpinner.add(p.getName());
            }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arraySpinner);
        spinnerDiscounted.setAdapter(adapter);
        spinnerDiscounter.setAdapter(adapter);



        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Product discounted=newProductList.get(spinnerDiscounted.getSelectedItemPosition());
                Product discounter=newProductList.get(spinnerDiscounter.getSelectedItemPosition());
                int amount=Integer.valueOf(edAmount.getText().toString());
                double price=Double.valueOf(newPrice.getText().toString());
                Discount discount=new Discount(discounter,amount,discounted,price);
                databaseDiscounts.child(discount.getId()).setValue(discount);
                finish();
            }
        });
    }
}
