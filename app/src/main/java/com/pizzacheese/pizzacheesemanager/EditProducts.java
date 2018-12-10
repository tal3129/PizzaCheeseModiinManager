package com.pizzacheese.pizzacheesemanager;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pizzacheese.pizzacheesemanager.Types.Product;

import java.util.ArrayList;

public class EditProducts extends ListActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {
    private ArrayList<Product> currentProducts = new ArrayList<>();

    private Button btnAddProduct;
    private DatabaseReference databaseProducts, versions;
    private Spinner spinnerProductType;
    private ListView listView;
    private int databaseVersion;
    public static ProductList adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_products);

        spinnerProductType = (Spinner) findViewById(R.id.spinnerProductType);
        btnAddProduct = (Button) findViewById(R.id.newProduct);
        listView = (ListView) findViewById(android.R.id.list);


        listView.setOnItemLongClickListener(this);
        listView.setOnItemClickListener(this);
        spinnerProductType.setOnItemSelectedListener(this);
        btnAddProduct.setOnClickListener(this);

        databaseProducts = FirebaseDatabase.getInstance().getReference("Products");
        versions = FirebaseDatabase.getInstance().getReference("versions");

        versions.child("databaseVersion").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                databaseVersion = dataSnapshot.getValue(Integer.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        databaseProducts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                updateListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.newProduct:
                Intent intent = new Intent(this, AddProduct.class);
                startActivityForResult(intent, 1);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        updateListView();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, DeleteProduct.class);
        Product p = currentProducts.get(position);
        intent.putExtra("product",p);
        startActivityForResult(intent, 2);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Product clickedPro = currentProducts.get(position);
        for (Product p : MainActivity.productArrayList)
            if (p.getId().equals(clickedPro.getId()))
                p.setAvailable(!p.isAvailable());

        //set the background of the clicked view
        view.setBackgroundColor(ContextCompat.getColor(this, (clickedPro.isAvailable()) ? R.color.lightGreen : R.color.lightRed));
        //set the availability of the clicked view
        return true;
    }

    @Override
    protected void onPause() {
        for (Product p : MainActivity.productArrayList) {
            databaseProducts.child(p.getId()).setValue(p);
        }
        versions.child("databaseVersion").setValue(databaseVersion + 1);

        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 || requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                updateListView();
            }
        }
    }

    private void updateListView() {
        currentProducts.clear();
        for (Product product : MainActivity.productArrayList) {
            if (product.getType().equals(spinnerProductType.getSelectedItem().toString()))
                currentProducts.add(product);
        }

        adapter = new ProductList(EditProducts.this, currentProducts);
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}


