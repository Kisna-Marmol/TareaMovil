package com.example.tareamovil;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Switch;

public class AdministradorAccesos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.administrador_accesos);

        // Enlazar switches
        swReiniciarSesion    = findViewById(R.id.swReiniciarSesion);
        swRestringirIntentos = findViewById(R.id.swRestringirIntentos);
        swModuloUsuario      = findViewById(R.id.swModuloUsuario);
        swCrearUsuario       = findViewById(R.id.swCrearUsuario);
        swModificarUsuario   = findViewById(R.id.swModificarUsuario);
        swActivarUsuario     = findViewById(R.id.swActivarUsuario);
        swDesactivarUsuario  = findViewById(R.id.swDesactivarUsuario);
        swModuloProducto     = findViewById(R.id.swModuloProducto);
        swCrearProducto      = findViewById(R.id.swCrearProducto);
        swModificarProducto  = findViewById(R.id.swModificarProducto);

        // Enlazar filas
        filaReiniciar        = findViewById(R.id.filaReiniciarSesion);
        filaRestringir       = findViewById(R.id.filaRestringirIntentos);
        filaModuloUsuario    = findViewById(R.id.filaModuloUsuario);
        filaCrearUsuario     = findViewById(R.id.filaCrearUsuario);
        filaModificarUsuario = findViewById(R.id.filaModificarUsuario);
        filaActivarUsuario   = findViewById(R.id.filaActivarUsuario);
        filaDesactivarUsuario= findViewById(R.id.filaDesactivarUsuario);
        filaModuloProducto   = findViewById(R.id.filaModuloProducto);
        filaCrearProducto    = findViewById(R.id.filaCrearProducto);
        filaModificarProducto= findViewById(R.id.filaModificarProducto);

        // Aplicar lógica verde/rojo a cada fila
        configurarSwitch(swReiniciarSesion,    filaReiniciar);
        configurarSwitch(swRestringirIntentos, filaRestringir);
        configurarSwitch(swModuloUsuario,      filaModuloUsuario);
        configurarSwitch(swCrearUsuario,       filaCrearUsuario);
        configurarSwitch(swModificarUsuario,   filaModificarUsuario);
        configurarSwitch(swActivarUsuario,     filaActivarUsuario);
        configurarSwitch(swDesactivarUsuario,  filaDesactivarUsuario);
        configurarSwitch(swModuloProducto,     filaModuloProducto);
        configurarSwitch(swCrearProducto,      filaCrearProducto);
        configurarSwitch(swModificarProducto,  filaModificarProducto);
    }

    private void configurarSwitch(Switch sw, LinearLayout fila) {
        // Color inicial según estado
        if (sw.isChecked()) {
            fila.setBackgroundColor(Color.parseColor("#00C853")); // verde
        } else {
            fila.setBackgroundColor(Color.parseColor("#F44336")); // rojo
        }

        // Cambiar color al toglear
        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                fila.setBackgroundColor(Color.parseColor("#00C853")); // verde
            } else {
                fila.setBackgroundColor(Color.parseColor("#F44336")); // rojo
            }
        });
    }

    // Declarar switches y filas
    Switch swReiniciarSesion, swRestringirIntentos, swModuloUsuario,
            swCrearUsuario, swModificarUsuario, swActivarUsuario,
            swDesactivarUsuario, swModuloProducto, swCrearProducto, swModificarProducto;

    LinearLayout filaReiniciar, filaRestringir, filaModuloUsuario,
            filaCrearUsuario, filaModificarUsuario, filaActivarUsuario,
            filaDesactivarUsuario, filaModuloProducto, filaCrearProducto, filaModificarProducto;

}