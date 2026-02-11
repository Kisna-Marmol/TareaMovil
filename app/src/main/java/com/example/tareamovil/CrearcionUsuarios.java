package com.example.tareamovil;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class CrearcionUsuarios extends AppCompatActivity {

    private EditText txtDNI, txtNombre, txtApellido, txtCorreo, txtUser, txtClave;
    private Button btnCrear;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crearcion_usuarios);

        txtDNI = findViewById(R.id.txtDni);
        txtNombre = findViewById(R.id.txtNombre);
        txtApellido = findViewById(R.id.txtApellido);
        txtCorreo = findViewById(R.id.txtCorreo);
        txtUser = findViewById(R.id.txtNewUser);
        txtClave = findViewById(R.id.txtNewClave);

        btnCrear = findViewById(R.id.btnCrearCuenta);
    }
}