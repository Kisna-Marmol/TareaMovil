package com.example.tareamovil;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tareamovil.clases.Dialog;

public class Login extends AppCompatActivity {
    Button btningresar, btncrear;
    TextView txtusuario, txtpassword;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        txtusuario = findViewById(R.id.txtUser);
        txtpassword = findViewById(R.id.txtClave);
        btningresar = findViewById(R.id.btnIngresar);
        btningresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intento = new Intent(Login.this, Bienvenido.class);
                //startActivity(intento);
                if (txtusuario.getText().toString().trim().equals("")){
                    Dialog.msgbox(Login.this, "Invalido", "Debe ingresar un usuario", R.drawable.error);
                }
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