package com.example.tareamovil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Inicio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(Inicio.this, Login.class);
            startActivity(intent);
            finish();
        },3000); //3000 ms = 3 segundos
    }
}