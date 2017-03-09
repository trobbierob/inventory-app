package com.example.android.inventoryapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

public class DetailActivity extends AppCompatActivity {

    private static int qty = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        TextView plusIcon = (TextView) findViewById(R.id.plus_icon);
        plusIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (qty > 100) {
                    Toasty.error(DetailActivity.this, "Quantity Cannot Be More Than 100", Toast.LENGTH_SHORT).show();
                } else {
                    qty = qty + 1;
                    displayQuantity();
                }
            }
        });

        Button addButton = (Button) findViewById(R.id.plus);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (qty > 100) {
                    Toasty.error(DetailActivity.this, "Quantity Cannot Be More Than 100", Toast.LENGTH_SHORT).show();
                } else {
                    qty = qty + 1;
                    displayQuantity();
                }
            }
        });

        Button minusButton = (Button) findViewById(R.id.minus);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (qty <= 0) {
                    Toasty.error(DetailActivity.this, "Quantity Cannot Be Less Than 0", Toast.LENGTH_SHORT).show();
                } else {
                    qty = qty - 1;
                    displayQuantity();
                }
            }
        });

        Button saleButton = (Button) findViewById(R.id.sale_btn);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (qty <= 0) {
                    Toasty.error(DetailActivity.this, "Quantity Cannot Be Less Than 0", Toast.LENGTH_SHORT).show();
                } else {
                    qty = qty - 1;
                    displayQuantity();
                }
            }
        });

        displayQuantity();
    }

    public void displayQuantity() {
        TextView quantityView = (TextView) findViewById(R.id.quantity);
        quantityView.setText(String.valueOf(qty));
    }
}
