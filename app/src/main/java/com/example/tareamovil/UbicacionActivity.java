package com.example.tareamovil;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/*import com.example.a2025_2c_pm1.Clases.BDAdmin;
import com.example.a2025_2c_pm1.Clases.Dialog;
import com.example.a2025_2c_pm1.Clases.Utils;*/

import com.example.tareamovil.clases.ApiService2;
import com.example.tareamovil.clases.BDAdmin;
import com.example.tareamovil.clases.Config;
import  com.example.tareamovil.clases.Dialog;
import com.example.tareamovil.clases.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.HashMap;
import java.util.Map;

public class UbicacionActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private MapView map;
    private MyLocationNewOverlay myLocationOverlay;
    private Button btnGuardar;
    private double latitudActual = 0.0;
    private double longitudActual = 0.0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE));
        setContentView(R.layout.activity_ubicacion);

        inicializarComponentes();
        configurarMapa();
        configurarBoton();

        if (checkLocationPermission()) {
            enableMyLocation();
        } else {
            requestLocationPermission();
        }
    }
    private void inicializarComponentes() {
        map = findViewById(R.id.mapview);
        btnGuardar = findViewById(R.id.btnGuardar);
    }

    private void configurarMapa() {
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
    }
    // Opcional: mostrar el punto fijo en el mapa

    private void configurarBoton() {
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (latitudActual != 0.0 && longitudActual != 0.0) {
                    guardarUbicacionEnBD(latitudActual, longitudActual);
                } else {
                    Toast.makeText(UbicacionActivity.this, "Obteniendo ubicación...", Toast.LENGTH_SHORT).show();
                    // Forzar obtener ubicación
                    obtenerUbicacionActual();
                }
            }
        });
    }
    private void enableMyLocation() {
        try {
            myLocationOverlay = new MyLocationNewOverlay(map);
            myLocationOverlay.enableMyLocation();
            map.getOverlays().add(myLocationOverlay);

            myLocationOverlay.runOnFirstFix(() -> runOnUiThread(() -> {
                GeoPoint ubicacion = myLocationOverlay.getMyLocation();
                if (ubicacion != null) {
                    latitudActual = ubicacion.getLatitude();
                    longitudActual = ubicacion.getLongitude();
                    map.getController().animateTo(ubicacion);
                    map.getController().setZoom(16.0);
                }
            }));

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
    private void obtenerUbicacionActual() {
        if (myLocationOverlay != null) {
            GeoPoint ubicacion = myLocationOverlay.getMyLocation();
            if (ubicacion != null) {
                latitudActual = ubicacion.getLatitude();
                longitudActual = ubicacion.getLongitude();

                /*double distanciaMetros = calcularDistancia(puntoFijo, new GeoPoint(latitudActual,longitudActual));
                Dialog.msgbox(this,"Distancia","Distancia "+distanciaMetros,R.drawable.ok);

                if (validarDistanciaAlPuntoFijo(latitudActual, longitudActual)) {
                    guardarUbicacionEnBD(latitudActual, longitudActual);
                } else {
                    mostrarMensajeFueraDeRango();
                }*/


                guardarUbicacionEnBD(latitudActual, longitudActual);
            } else {
                Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show();
            }
        }
    }
    // Método para validar si la ubicación está dentro del radio permitido

    private void guardarUbicacionEnBD(double latitud, double longitud) {
        // Mostrar diálogo de confirmación
        String msg = "¿Estás seguro de guardar esta ubicación?\nCoordenadas:\n" +
                "Latitud: " + String.format("%.6f", latitud) + "\n" +
                "Longitud: " + String.format("%.6f", longitud);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Confirmación")
                .setMessage(msg)
                .setPositiveButton("Sí", (dialog, which) ->
                {
                    // Preparar datos para enviar
                    int userId = Config.iduser; // Asegúrate que Utils.iduser esté disponible
                    String fechaReg = Config.hoy(); // Formato: "YYYY-MM-DD HH:MM:SS"

                    // Mostrar progreso
                    Dialog.msgbox(this, "Procesando", "Guardando ubicación...", R.drawable.ok);

                    // Llamar a la API
                    ApiService2.getInstance(this).guardarUbicacion(
                            latitud, longitud, userId, fechaReg,
                            new ApiService2.ApiCallback() {
                                @Override
                                public void onSuccess(JSONObject response) {
                                    runOnUiThread(() -> {
                                        Dialog.msgbox(UbicacionActivity.this, "Éxito",
                                                "Ubicación guardada satisfactoriamente", R.drawable.ok);
                                        // Opcional: cerrar activity o limpiar
                                    });
                                }

                                @Override
                                public void onError(String error) {
                                    runOnUiThread(() -> {
                                        Dialog.msgbox(UbicacionActivity.this, "Error",
                                                "No se pudo guardar: " + error, R.drawable.error);
                                    });
                                }
                            }
                    );
                })
                .setNegativeButton("No", null)
                .setIcon(R.drawable.ok)
                .show();
    }

    // AsyncTask para enviar datos al servidor (método POST)
    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_LOCATION_PERMISSION);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
        if (myLocationOverlay != null) {
            myLocationOverlay.enableMyLocation();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
        if (myLocationOverlay != null) {
            myLocationOverlay.disableMyLocation();
        }
    }
}