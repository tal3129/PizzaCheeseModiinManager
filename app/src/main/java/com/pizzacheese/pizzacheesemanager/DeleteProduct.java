package com.pizzacheese.pizzacheesemanager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pizzacheese.pizzacheesemanager.Types.Product;
import com.squareup.picasso.Picasso;

public class DeleteProduct extends AppCompatActivity implements View.OnClickListener {

    DatabaseReference databaseProducts, idCounter;
    StorageReference mStorage, picturePath, nextPicPath;

    //seeImage-the image preview
    ImageView seeImage, choosePicture;

    private final int GALLERY_INTENT = 2;
    private String nextPicId;
    Intent i;
    Button btnAddProduct, btnCancel, btnDelete;
    Spinner spinnerType;
    EditText prodName, prodPrice;
    //the current product the activity is working with
    private Product currentProduct;
    //the current product's image uri
    private Uri picUri;
    private ProgressBar progressCircle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_product);

        idCounter = FirebaseDatabase.getInstance().getReference("idCounter");
        idCounter.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int id = dataSnapshot.getValue(Integer.class);
                nextPicId = String.valueOf(id);
                nextPicPath = mStorage.child("pictures").child(nextPicId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        //setting the references
        i = this.getIntent();
        databaseProducts = FirebaseDatabase.getInstance().getReference("Products");
        mStorage = FirebaseStorage.getInstance().getReference();

        //setting the views
        prodName = (EditText) findViewById(R.id.productName);
        prodPrice = (EditText) findViewById(R.id.productPrice);
        spinnerType = (Spinner) findViewById(R.id.spinnerType);
        choosePicture = (ImageView) findViewById(R.id.choosePicture);
        seeImage = (ImageView) findViewById(R.id.seebutton);
        btnAddProduct = (Button) findViewById(R.id.finishButton);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnDelete = (Button) findViewById(R.id.deleteButton);
        progressCircle=findViewById(R.id.progress);

        //setting the buttons' onClickListeners
        btnDelete.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        choosePicture.setOnClickListener(this);
        btnAddProduct.setOnClickListener(this);

        //set up the activity's values to the intended ones

        currentProduct = (Product) getIntent().getSerializableExtra("product");
        prodName.setText(currentProduct.getName());
        prodPrice.setText(String.format("%.2f", currentProduct.getPrice()));
        if(!currentProduct.getPictureId().equals("default")) {
            Picasso.get().load(Uri.parse(currentProduct.getUri())).into(seeImage);
            picturePath = mStorage.child("pictures").child(currentProduct.getPictureId());
        }
        String compareValue = currentProduct.getType();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.productTypes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);
        if (!compareValue.equals("")) {
            int spinnerPosition = adapter.getPosition(compareValue);
            spinnerType.setSelection(spinnerPosition);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //end the activity
            case R.id.btnCancel:
                finish();
                break;
            //let the user select an Image from his internal storage
            case R.id.choosePicture:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
                break;

            //add the product to the database
            case R.id.finishButton:
                uploadCurrentProduct();
                break;
            case R.id.deleteButton:
                for (Product p : MainActivity.productArrayList)
                    if (p.getId().equals(currentProduct.getId()))
                        MainActivity.productArrayList.remove(p);
                databaseProducts.child(currentProduct.getId()).removeValue();
                if(picturePath!=null)
                    picturePath.delete();
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                break;
        }

    }


    public void uploadCurrentProduct() {
        //declaring the product attributes
        String id, name, sprice, type;
        double price;

        name = prodName.getText().toString();
        sprice = prodPrice.getText().toString();

        if (sprice.equals(""))
            sprice = "0";
        price = Double.parseDouble(sprice);
        type = spinnerType.getSelectedItem().toString();

        if (name.equals(""))
            Toast.makeText(this, "יש להכניס שם למוצר", Toast.LENGTH_SHORT).show();
        else {
            currentProduct.setType(type);
            currentProduct.setName(name);
            currentProduct.setPrice(price);
            currentProduct.setAvailable(true);
            for (Product p : MainActivity.productArrayList)
                if (p.getId().equals(currentProduct.getId()))
                    MainActivity.productArrayList.set(MainActivity.productArrayList.indexOf(p), currentProduct);

            databaseProducts.child(currentProduct.getId()).setValue(currentProduct);

            if (picUri != null) {
                setLoading();
                uploadPic();
            }
            else {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                Toast.makeText(DeleteProduct.this, "המוצר התעדכן בהצלחה", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void uploadPic() {
        if(currentProduct.getPictureId().equals("default")) {
            picturePath = nextPicPath;
            currentProduct.setPictureId(nextPicId);
        }
            //upload the chosen pic to the Firebase storage
        picturePath.putFile(picUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(DeleteProduct.this, "המוצר התעדכן בהצלחה", Toast.LENGTH_SHORT).show();
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(DeleteProduct.this, "שגיאה", Toast.LENGTH_SHORT).show();
                }
            });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //set the local variables(uri and path) to the chosen picture's
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            picUri = uri;
            //change the see image
            Picasso.get().load(uri).into(seeImage);
        }
    }


    private void setLoading(){
            progressCircle.setVisibility(View.VISIBLE);
            btnAddProduct.setEnabled(false);
            btnCancel.setEnabled(false);
            btnDelete.setEnabled(false);
    }

}