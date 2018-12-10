package com.pizzacheese.pizzacheesemanager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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


public class AddProduct extends AppCompatActivity implements View.OnClickListener {
    //database and storage references
    DatabaseReference databaseProducts, idCounter;
    StorageReference mStorage, pictureStoragePath;

    //seeImage-the image preview
    ImageView seeImage, choosePicture;

    private final int GALLERY_INTENT = 2;
    private String picId;
    Intent i;
    Button btnAddProduct, btnCancel;
    Spinner spinnerType;
    //the current product the activity is working with
    private Product currentProduct;
    //the current product's image uri
    private Uri picUri;
    private CheckBox toppingProd;
    private ProgressBar progressCircle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        //setting the references
        i = this.getIntent();
        databaseProducts = FirebaseDatabase.getInstance().getReference("Products");
        mStorage = FirebaseStorage.getInstance().getReference();

        idCounter = FirebaseDatabase.getInstance().getReference("idCounter");
        idCounter.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int id = dataSnapshot.getValue(Integer.class);
                picId = String.valueOf(id);
                pictureStoragePath = mStorage.child("pictures").child(picId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        currentProduct = new Product();

        //setting the views
        spinnerType = (Spinner) findViewById(R.id.spinnerType);
        choosePicture = (ImageView) findViewById(R.id.choosePicture);
        seeImage = (ImageView) findViewById(R.id.seebutton);
        btnAddProduct = (Button) findViewById(R.id.finishButton);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        toppingProd = findViewById(R.id.checkBox);
        progressCircle = findViewById(R.id.progress);

        //setting the button's onClickListeners
        btnCancel.setOnClickListener(this);
        choosePicture.setOnClickListener(this);
        btnAddProduct.setOnClickListener(this);

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
                //declaring the product attributes
                String id, name, sprice, type;
                double price;

                id = databaseProducts.push().getKey();
                name = ((EditText) findViewById(R.id.productName)).getText().toString();
                sprice = ((EditText) findViewById(R.id.productPrice)).getText().toString();
                type = spinnerType.getSelectedItem().toString();

                if (sprice.equals(""))
                    sprice = "0";
                price = Double.parseDouble(sprice);


                if (name.equals(""))
                    Toast.makeText(this, "יש להכניס שם למוצר", Toast.LENGTH_SHORT).show();
                else {
                    currentProduct.setId(id);
                    currentProduct.setType(type);
                    currentProduct.setName(name);
                    currentProduct.setPrice(price);
                    currentProduct.setAvailable(true);
                    currentProduct.setPictureId(picId);
                    if (picUri != null) {
                        setLoading();
                        uploadPic();
                    } else {
                        //set the product's pic to default
                        currentProduct.setPictureId("default");

                        databaseProducts.child(id).setValue(currentProduct);
                        MainActivity.productArrayList.add(currentProduct);

                        Toast.makeText(this, "המוצר נוסף בהצלחה", Toast.LENGTH_SHORT).show();

                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                }

        }

    }

    public void uploadPic() {
        //upload the chosen pic to the Firebase storage
        pictureStoragePath.putFile(picUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddProduct.this, "העלאה הושלמה", Toast.LENGTH_SHORT).show();
                currentProduct.setUri(picUri.toString());
                MainActivity.productArrayList.add(currentProduct);
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddProduct.this, "שגיאה", Toast.LENGTH_SHORT).show();
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
            idCounter.setValue(Integer.valueOf(picId) + 1);

            //change the see image
            Picasso.get().load(uri).into(seeImage);

        }
    }

    private void setLoading() {
        progressCircle.setVisibility(View.VISIBLE);
        btnAddProduct.setEnabled(false);
        btnCancel.setEnabled(false);
    }

}


