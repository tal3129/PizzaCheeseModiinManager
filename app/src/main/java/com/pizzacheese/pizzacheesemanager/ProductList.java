package com.pizzacheese.pizzacheesemanager;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pizzacheese.pizzacheesemanager.Types.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ProductList extends ArrayAdapter<Product> {
    private Activity context;
    private ArrayList<Product> productList;


    public ProductList(Activity context, ArrayList<Product> productList) {
        super(context, R.layout.product_list, productList);
        this.context = context;
        this.productList = productList;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.product_list, null, true);
        final TextView productName = (TextView) listViewItem.findViewById(R.id.listProductName);
        final TextView productPrice = (TextView) listViewItem.findViewById(R.id.listProductPrice);
        final ImageView productImage = (ImageView) listViewItem.findViewById(R.id.productImage);

        Product product = productList.get(position);

        listViewItem.setBackgroundColor(ContextCompat.getColor(context, (product.isAvailable()) ? R.color.lightGreen : R.color.lightRed));
        productName.setText(product.getName());
        productPrice.setText(String.valueOf(product.getPrice()));
        if(product.getUri() != null)
            Picasso.get().load(Uri.parse(product.getUri())).into(productImage);

        return listViewItem;
    }


}
