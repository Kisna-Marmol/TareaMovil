package com.example.tareamovil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int TIEMPO_CARGA = 2000; // 2 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // En SplashActivity.java, antes de navegar al Login
        SharedPreferences prefs = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        boolean recordar = prefs.getBoolean("recordarme", false);
        String usuarioGuardado = prefs.getString("usuario", "");

        if (recordar && !usuarioGuardado.isEmpty()) {
            // Ya tiene usuario guardado, ir directo al Login
            // (el Login lo cargará automáticamente)
        }
        // Siempre va al Login, él se encarga de cargar el usuario

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, Login.class);
            startActivity(intent);
            finish(); // Elimina el splash del stack para que no se regrese a él
        }, TIEMPO_CARGA);
    }
}