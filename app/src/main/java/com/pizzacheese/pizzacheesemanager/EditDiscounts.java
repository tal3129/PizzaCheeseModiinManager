package com.pizzacheese.pizzacheesemanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pizzacheese.pizzacheesemanager.Types.Discount;

import java.util.ArrayList;

public class EditDiscounts extends Activity {
    ArrayList<Discount> discountList;
    Button btnAddDiscount;
    DatabaseReference databaseDiscounts;
    ListView discountListView;
    ArrayAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        discountList = new ArrayList<>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_discounts);
        databaseDiscounts = FirebaseDatabase.getInstance().getReference("Discounts");


        discountListView = findViewById(R.id.listView);

        btnAddDiscount = findViewById(R.id.addDiscount);
        btnAddDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditDiscounts.this, addDiscount.class));
            }
        });


        discountListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                discountList.get(position).setActive(!discountList.get(position).isActive());
                view.setBackgroundColor(ContextCompat.getColor(EditDiscounts.this, discountList.get(position).isActive() ? R.color.lightGreen : R.color.lightRed));

            }
        });
        discountListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //on long item click- btnDelete the discount (toppingsDiscount is un-deletable
                if(!discountList.get(i).getId().equals("toppingsDiscount")) {
                    databaseDiscounts.child(discountList.get(i).getId()).removeValue();
                    discountList.remove(i);
                    if(adapter!=null)
                        adapter.notifyDataSetChanged();
                }
                return true;
            }
        });


        databaseDiscounts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                discountList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    discountList.add(snapshot.getValue(Discount.class));
                adapter = new ArrayAdapter<Discount>(EditDiscounts.this, android.R.layout.simple_list_item_1, discountList) {
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        view.setBackgroundColor(ContextCompat.getColor(EditDiscounts.this, discountList.get(position).isActive() ? R.color.lightGreen : R.color.lightRed));
                        return view;
                    }
                };
                discountListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @Override
    protected void onStop() {
        for (Discount discount : discountList)
            databaseDiscounts.child(discount.getId()).setValue(discount);
        super.onStop();
    }

}
