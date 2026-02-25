package com.example.tareamovil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Bienvenido extends AppCompatActivity {
    Button btnsalir, btnAccesos, btnUsuario;
    TextView tblTitulo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bienvenido);
        btnsalir = findViewById(R.id.btnCerrarSesion);
        btnAccesos = findViewById(R.id.btnModuloAcceso);
        btnUsuario = findViewById(R.id.btnModuloUsuario);
        tblTitulo = findViewById(R.id.lbl_titulo);

        // ✅ RECIBIR EL NOMBRE DEL USUARIO
        String nombreUsuario = getIntent().getStringExtra("Nombre_Usuario");
        if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
            tblTitulo.setText("Bienvenido, " + nombreUsuario);
        }

        btnsalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Bienvenido.this, Login.class);
                startActivity(intent);
            }
        });

        btnAccesos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Bienvenido.this, AdministradorAccesos.class);
                startActivity(intent);
            }
        });

        btnUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Bienvenido.this, CrearcionUsuarios.class);
                startActivity(intent);
            }
        });
    }
}