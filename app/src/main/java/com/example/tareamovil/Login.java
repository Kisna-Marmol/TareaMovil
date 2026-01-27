package com.example.tareamovil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Login extends AppCompatActivity {
    Button btningresar, btncrear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        btningresar = findViewById(R.id.btnIngresar);
        btningresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intento = new Intent(Login.this, Bienvenido.class);
                startActivity(intento);
            }
        });
        btncrear = findViewById(R.id.btnCrearCuenta);
        btncrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intento = new Intent(Login.this, CrearcionUsuarios.class);
                startActivity(intento);
            }
        });
    }
}