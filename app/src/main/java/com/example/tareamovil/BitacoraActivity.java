package com.example.tareamovil;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import com.example.tareamovil.clases.ApiService;
import com.example.tareamovil.clases.Bitacora;
import com.example.tareamovil.clases.Config;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

public class BitacoraActivity extends AppCompatActivity {

    private EditText fechaInicio, fechaFin;
    private Button btnGenerar;
    private LinearLayout tablaContenedor;
    private int userId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitacora);

        fechaInicio     = findViewById(R.id.etFechaInicio);
        fechaFin        = findViewById(R.id.etFechaFin);
        btnGenerar      = findViewById(R.id.btnGenerar);
        tablaContenedor = findViewById(R.id.tableContainer);

        // Recibir user_id desde la Activity anterior
        userId = getIntent().getIntExtra("User_ID", 0);

        // Registrar ingreso al módulo
        registrarEvento("Ingreso a Módulo de Bitácora", "5");

        // Fechas por defecto: primer día del mes — hoy
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        fechaFin.setText(sdf.format(cal.getTime()));
        cal.set(Calendar.DAY_OF_MONTH, 1);
        fechaInicio.setText(sdf.format(cal.getTime()));

        fechaInicio.setOnClickListener(v -> mostrarSelectorFecha(fechaInicio));
        fechaFin.setOnClickListener(v -> mostrarSelectorFecha(fechaFin));
        accionBoton();
    }

    // ── Registrar evento — usa Bitacora.insert() directamente ─
    public void registrarEvento(String descripcion, String modulo) {
        Bitacora bit = new Bitacora(userId, descripcion, this, modulo);
        bit.insert(new ApiService.ApiCallback() {
            @Override public void onSuccess(String r) { Log.d("BITACORA", "Registrado: " + modulo); }
            @Override public void onError(String e)   { Log.e("BITACORA", "Error: " + e); }
        });
    }

    private void accionBoton() {
        btnGenerar.setOnClickListener(v -> llenar());
    }

    private void llenar() {
        String fi = fechaInicio.getText().toString().trim();
        String ff = fechaFin.getText().toString().trim();

        if (fi.isEmpty() || ff.isEmpty()) {
            Toast.makeText(this, "Seleccione ambas fechas", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat formatoSQL     = new SimpleDateFormat("yyyy-MM-dd",  Locale.getDefault());
            String fiSQL = formatoSQL.format(formatoEntrada.parse(fi));
            String ffSQL = formatoSQL.format(formatoEntrada.parse(ff));

            JSONObject json = new JSONObject();
            json.put("fecha_inicio", fiSQL);
            json.put("fecha_fin",    ffSQL);

            btnGenerar.setEnabled(false);
            btnGenerar.setText("Consultando...");

            ApiService.post(Config.local + "bitacora_consulta.php", json, new ApiService.ApiCallback() {
                @Override
                public void onSuccess(String response) {
                    btnGenerar.setEnabled(true);
                    btnGenerar.setText("GENERAR CONSULTA");
                    procesarRespuesta(response);
                }
                @Override
                public void onError(String error) {
                    btnGenerar.setEnabled(true);
                    btnGenerar.setText("GENERAR CONSULTA");
                    Log.e("BITACORA", "Error HTTP: " + error);
                    Toast.makeText(BitacoraActivity.this,
                            "Error: " + error, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Log.e("BITACORA", "Error: " + e.getMessage());
        }
    }

    private void procesarRespuesta(String response) {
        tablaContenedor.removeAllViews();
        try {
            JSONObject res = new JSONObject(response);
            if (!res.optBoolean("success", false)) {
                Toast.makeText(this, res.optString("error", "Sin resultados"), Toast.LENGTH_SHORT).show();
                return;
            }

            JSONArray data = res.getJSONArray("data");
            if (data.length() == 0) {
                Toast.makeText(this, "No hay registros en ese rango de fechas", Toast.LENGTH_SHORT).show();
                return;
            }

            // Encabezado
            agregarFila(new String[]{
                    "FECHA/USUARIO", "DISPOSITIVO/MÓDULO", "USUARIO", "DETALLE"
            }, true);

            for (int i = 0; i < data.length(); i++) {
                JSONObject f = data.getJSONObject(i);

                // Fila 1 — fecha, dispositivo, usuario, detalle
                agregarFila(new String[]{
                        f.optString("bit_fecha",    ""),
                        f.optString("bit_marca",    "") + " <" + f.optString("bit_modelo", "") + ">",
                        "<" + f.optString("user_usuario", f.optString("user_id","")) + ">",
                        f.optString("bit_detalle",  "")
                }, false);

                // Fila 2 — usuario, módulo nombre, usuario, hostname
                agregarFila(new String[]{
                        f.optString("user_usuario",  ""),
                        f.optString("modulo_nombre", f.optString("modulo_codigo","")),
                        f.optString("user_usuario",  ""),
                        f.optString("bit_hostname",  "")
                }, false);
            }

        } catch (Exception e) {
            Log.e("BITACORA", "Error parseando: " + e.getMessage());
            Toast.makeText(this, "Error al leer respuesta.", Toast.LENGTH_LONG).show();
        }
    }
    private void agregarFila(String[] celdas, boolean esEncabezado) {
        LinearLayout fila = new LinearLayout(this);
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setBackgroundColor(esEncabezado
                ? Color.parseColor("#76FF03")
                : Color.parseColor("#00BCD4"));

        for (String celda : celdas) {
            TextView tv = new TextView(this);
            tv.setText(celda != null ? celda : "");
            tv.setPadding(10, 8, 10, 8);
            tv.setTextColor(Color.BLACK);
            tv.setMinWidth(220);
            tv.setGravity(Gravity.START);
            if (esEncabezado) tv.setTypeface(null, Typeface.BOLD);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(1, 1, 1, 1);
            tv.setLayoutParams(lp);
            fila.addView(tv);
        }

        tablaContenedor.addView(fila);

        View sep = new View(this);
        sep.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        sep.setBackgroundColor(Color.WHITE);
        tablaContenedor.addView(sep);
    }

    private void mostrarSelectorFecha(EditText campo) {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this,
                (view, anio, mes, dia) -> campo.setText(
                        String.format(Locale.getDefault(), "%02d/%02d/%04d", dia, mes + 1, anio)),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
        ).show();
    }
}