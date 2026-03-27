package com.example.tareamovil;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tareamovil.clases.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdministradorAccesos extends AppCompatActivity {

    private Spinner spUsuarios;
    private Switch swReiniciarSesion, swRestringirIntentos, swModuloUsuario,
            swCrearUsuario, swModificarUsuario, swActivarUsuario,
            swDesactivarUsuario, swModuloProducto, swCrearProducto, swModificarProducto,
            swCliente;
    private LinearLayout filaReiniciar, filaRestringir, filaModuloUsuario,
            filaCrearUsuario, filaModificarUsuario, filaActivarUsuario,
            filaDesactivarUsuario, filaModuloProducto, filaCrearProducto, filaModificarProducto,
            filaCliente;

    // Listas para el spinner
    private List<String> nombresUsuarios = new ArrayList<>();
    private List<Integer> idsUsuarios = new ArrayList<>();
    private int userIdSeleccionado = -1;
    private boolean cargandoAccesos = false; // evita guardar mientras se cargan
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.administrador_accesos);

        spUsuarios = findViewById(R.id.sp_usuarios);

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
        swCliente = findViewById(R.id.swCliente);

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
        filaCliente = findViewById(R.id.filaCliente);

        // Configurar switches con color
        configurarSwitch(swReiniciarSesion,    filaReiniciar,    "0");
        configurarSwitch(swRestringirIntentos, filaRestringir,   "0.1");
        configurarSwitch(swModuloUsuario,      filaModuloUsuario,"1");
        configurarSwitch(swCrearUsuario,       filaCrearUsuario, "1.1");
        configurarSwitch(swModificarUsuario,   filaModificarUsuario,"1.2");
        configurarSwitch(swActivarUsuario,     filaActivarUsuario,  "1.3");
        configurarSwitch(swDesactivarUsuario,  filaDesactivarUsuario,"1.4");
        configurarSwitch(swModuloProducto,     filaModuloProducto,  "2");
        configurarSwitch(swCrearProducto,      filaCrearProducto,   "2.1");
        configurarSwitch(swModificarProducto,  filaModificarProducto,"2.2");
        configurarSwitch(swCliente,  filaCliente,"2.3");

        // Cargar usuarios en spinner
        cargarUsuarios();

        int userId = getIntent().getIntExtra("User_ID", 0);
        Login.registrarBitacora(userId, "Ingreso a Módulo de Accesos", "3", this);

    }

    private void configurarSwitch(Switch sw, LinearLayout fila, String moduloCodigo) {
        actualizarColorFila(sw, fila);
        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            actualizarColorFila(sw, fila);
            if (!cargandoAccesos && userIdSeleccionado != -1) {
                guardarAccesos();
            }
        });
    }

    private void actualizarColorFila(Switch sw, LinearLayout fila) {
        if (sw.isChecked()) {
            fila.setBackgroundColor(Color.parseColor("#00C853"));
        } else {
            fila.setBackgroundColor(Color.parseColor("#F44336"));
        }
    }

    private void cargarUsuarios() {
        String URL = Config.local + "listar_usuario.php";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, URL, null,
                response -> {
                    nombresUsuarios.clear();
                    idsUsuarios.clear();
                    nombresUsuarios.add("-- Seleccione un usuario --");
                    idsUsuarios.add(-1);
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            String nombre = obj.getString("user_nombre") + " " + obj.getString("user_apellido");
                            int id = Integer.parseInt(obj.getString("user_id"));
                            nombresUsuarios.add(nombre);
                            idsUsuarios.add(id);
                        } catch (JSONException e) { e.printStackTrace(); }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_item, nombresUsuarios);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spUsuarios.setAdapter(adapter);

                    spUsuarios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                            userIdSeleccionado = idsUsuarios.get(position);
                            if (userIdSeleccionado != -1) {
                                cargarAccesosUsuario(userIdSeleccionado);
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                },
                error -> {
                    Log.e("ERROR_USUARIOS", error.toString());
                    Toast.makeText(this, "Error cargando usuarios", Toast.LENGTH_SHORT).show();
                }
        );
        Volley.newRequestQueue(this).add(request);
    }

    private void cargarAccesosUsuario(int userId) {
        cargandoAccesos = true; // bloquear guardado mientras cargamos
        String URL = Config.local + "listar_accesos_usuario.php";

        JSONObject params = new JSONObject();
        try { params.put("user_id", userId); } catch (JSONException e) { e.printStackTrace(); }

        // Primero apagar todos los switches
        desactivarTodos();

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, URL, params,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray accesos = response.getJSONArray("accesos");
                            for (int i = 0; i < accesos.length(); i++) {
                                JSONObject acceso = accesos.getJSONObject(i);
                                String codigo = acceso.getString("modulo_codigo");
                                activarSwitch(codigo);
                            }
                        }
                    } catch (JSONException e) { e.printStackTrace(); }
                    cargandoAccesos = false; // desbloquear guardado
                },
                error -> {
                    cargandoAccesos = false;
                    Toast.makeText(this, "Error cargando accesos", Toast.LENGTH_SHORT).show();
                }
        );
        Volley.newRequestQueue(this).add(request);
    }

    private void desactivarTodos() {
        swReiniciarSesion.setChecked(false);
        swRestringirIntentos.setChecked(false);
        swModuloUsuario.setChecked(false);
        swCrearUsuario.setChecked(false);
        swModificarUsuario.setChecked(false);
        swActivarUsuario.setChecked(false);
        swDesactivarUsuario.setChecked(false);
        swModuloProducto.setChecked(false);
        swCrearProducto.setChecked(false);
        swModificarProducto.setChecked(false);
        swCliente.setChecked(false);
    }

    private void activarSwitch(String codigo) {
        switch (codigo) {
            case "0":   swReiniciarSesion.setChecked(true);    break;
            case "0.1": swRestringirIntentos.setChecked(true); break;
            case "1":   swModuloUsuario.setChecked(true);      break;
            case "1.1": swCrearUsuario.setChecked(true);       break;
            case "1.2": swModificarUsuario.setChecked(true);   break;
            case "1.3": swActivarUsuario.setChecked(true);     break;
            case "1.4": swDesactivarUsuario.setChecked(true);  break;
            case "2":   swModuloProducto.setChecked(true);     break;
            case "2.1": swCrearProducto.setChecked(true);      break;
            case "2.2": swModificarProducto.setChecked(true);  break;
            case "2.3": swCliente.setChecked(true); break;
        }
    }

    private void guardarAccesos() {
        String URL = Config.local + "guardar_accesos.php";

        JSONArray accesos = new JSONArray();
        try {
            accesos.put(crearAcceso("0",   swReiniciarSesion.isChecked()));
            accesos.put(crearAcceso("0.1", swRestringirIntentos.isChecked()));
            accesos.put(crearAcceso("1",   swModuloUsuario.isChecked()));
            accesos.put(crearAcceso("1.1", swCrearUsuario.isChecked()));
            accesos.put(crearAcceso("1.2", swModificarUsuario.isChecked()));
            accesos.put(crearAcceso("1.3", swActivarUsuario.isChecked()));
            accesos.put(crearAcceso("1.4", swDesactivarUsuario.isChecked()));
            accesos.put(crearAcceso("2",   swModuloProducto.isChecked()));
            accesos.put(crearAcceso("2.1", swCrearProducto.isChecked()));
            accesos.put(crearAcceso("2.2", swModificarProducto.isChecked()));
            accesos.put(crearAcceso("2.3", swCliente.isChecked()));

            JSONObject params = new JSONObject();
            params.put("user_id", userIdSeleccionado);
            params.put("accesos", accesos);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST, URL, params,
                    response -> {
                        try {
                            if (response.getBoolean("success")) {
                                Toast.makeText(this, "Accesos guardados", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) { e.printStackTrace(); }
                    },
                    error -> {
                        Log.e("GUARDAR_ERROR", error.toString());
                        Toast.makeText(this, "Error guardando accesos", Toast.LENGTH_SHORT).show();
                    }
            );
            Volley.newRequestQueue(this).add(request);

        } catch (JSONException e) { e.printStackTrace(); }
    }

    private JSONObject crearAcceso(String codigo, boolean activo) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("modulo_codigo", codigo);
        obj.put("estado", activo ? "ACTIVO" : "INACTIVO");
        return obj;
    }
}