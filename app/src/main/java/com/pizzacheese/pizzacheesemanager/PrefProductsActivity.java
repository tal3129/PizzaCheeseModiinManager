package com.pizzacheese.pizzacheesemanager;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pizzacheese.pizzacheesemanager.Types.Product;

import java.util.ArrayList;

public class PrefProductsActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ListView prodListView;
    private ArrayList<Product>prefProdList;
    private ArrayList<Product>listProducts;
    private ArrayAdapter<Product>adapter;
    Button addPrefProd;
    Dialog choosePrefDialog;
    DatabaseReference databaseProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pref_products);
        prefProdList=new ArrayList<>();
        listProducts=new ArrayList<>();

        databaseProducts= FirebaseDatabase.getInstance().getReference("Products");
        addPrefProd=findViewById(R.id.btnAddPref);
        addPrefProd.setOnClickListener(this);
        for (Product product:MainActivity.productArrayList) {
            if (product.isPreferred()) {
                prefProdList.add(product);
            }
            if (!product.getType().equals("תוספות"))
                listProducts.add(product);
        }

        initializeDialog();
        prodListView=findViewById(R.id.ProdListView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, prefProdList);
        prodListView.setAdapter(adapter);

        prodListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Product prodToDel=prefProdList.get(position);
                prodToDel.setPreferred(false);
                databaseProducts.child(prodToDel.getId()).setValue(prodToDel);

                prefProdList.remove(prodToDel);
                adapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        choosePrefDialog.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        listProducts.get(position).setPreferred(true);
        prefProdList.add(listProducts.get(position));
        databaseProducts.child(listProducts.get(position).getId()).setValue(listProducts.get(position));
        adapter.notifyDataSetChanged();
        choosePrefDialog.hide();
    }


    public void initializeDialog(){
        choosePrefDialog =new Dialog(this);
        RelativeLayout dialogLay=new RelativeLayout(this);
        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        ListView listView=new ListView(this);
        listView.setOnItemClickListener(this);
        ArrayAdapter<Product>dialogAdapter =new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listProducts);
        listView.setAdapter(dialogAdapter);
        dialogLay.addView(listView);

        choosePrefDialog.setContentView(dialogLay,params);
    }


}
