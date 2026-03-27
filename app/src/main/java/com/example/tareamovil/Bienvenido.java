package com.example.tareamovil;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tareamovil.clases.Config;
import com.example.tareamovil.clases.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Bienvenido extends AppCompatActivity {
    Button btnsalir, btnAccesos, btnUsuario, btnBitacora, btnCliene;
    TextView tblTitulo;

    ImageView imagen;

    int userId;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bienvenido);
        btnsalir = findViewById(R.id.btnCerrarSesion);
        btnAccesos = findViewById(R.id.btnModuloAcceso);
        btnUsuario = findViewById(R.id.btnModuloUsuario);
        btnBitacora = findViewById(R.id.btnModuloBitacora);
        btnCliene = findViewById(R.id.btnModuloCliente);
        tblTitulo = findViewById(R.id.lbl_titulo);
        imagen = findViewById(R.id.imgFotoPerfil);

        //ocultar();

        btnAccesos.setVisibility(View.GONE);
        btnUsuario.setVisibility(View.GONE);
        findViewById(R.id.btnModuloProducto).setVisibility(View.GONE);
        findViewById(R.id.btnModuloPedido).setVisibility(View.GONE);
        findViewById(R.id.btnModuloBitacora).setVisibility(View.GONE);
        findViewById(R.id.btnReiniciarSesion).setVisibility(View.GONE);

        // ✅ RECIBIR EL NOMBRE DEL USUARIO
        String nombreUsuario = getIntent().getStringExtra("Nombre_Usuario");
        int userId = getIntent().getIntExtra("User_ID", -1);
        Log.e("USER_ID_RECIBIDO", "ID: " + userId);
        Login.registrarBitacora(userId, "Ingreso al Sistema", "0", this);
        if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
            tblTitulo.setText("Bienvenido, " + nombreUsuario);
        }

        // Cargar accesos del usuario
        if (userId != -1) {
            cargarAccesos(userId);
        }

        btnsalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Bienvenido.this, Login.class);
                intent.putExtra("User_ID", userId);
                startActivity(intent);
            }
        });

        btnAccesos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Bienvenido.this, AdministradorAccesos.class);
                intent.putExtra("User_ID", userId);
                startActivity(intent);
            }
        });

        btnUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Bienvenido.this, CrearcionUsuarios.class);
                intent.putExtra("User_ID", userId);
                startActivity(intent);
            }
        });

        btnBitacora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Bienvenido.this, BitacoraActivity.class);
                intent.putExtra("User_ID", userId);
                startActivity(intent);
            }
        });

        btnCliene.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Bienvenido.this, ClienteActivity.class);
                intent.putExtra("User_ID", userId);
                startActivity(intent);
            }
        });

        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pral=new Intent(Bienvenido.this,FotoActivity.class);
                //pral.putExtra("id",Utils.iduser);
                pral.putExtra("tabla","tbl_user_img");
                pral.putExtra("campof","user_img");
                pral.putExtra("campoi","user_id");
                Bienvenido.this.startActivity(pral);
            }
        });
    }

    /*public void ocultar(){
        btnAccesos.setVisibility(View.GONE);
        btnUsuario.setVisibility(View.GONE);
        findViewById(R.id.btnModuloProducto).setVisibility(View.GONE);
        findViewById(R.id.btnModuloPedido).setVisibility(View.GONE);
        findViewById(R.id.btnModuloBitacora).setVisibility(View.GONE);
        findViewById(R.id.btnReiniciarSesion).setVisibility(View.GONE);
    }*/

    private void cargarAccesos(int userId) {
        String URL = Config.local + "listar_accesos_usuario.php";

        JSONObject params = new JSONObject();
        try { params.put("user_id", userId); } catch (JSONException e) { e.printStackTrace(); }

        //temporal
        Log.e("ACCESOS", "Llamando con user_id: " + userId);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, URL, params,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray accesos = response.getJSONArray("accesos");
                            for (int i = 0; i < accesos.length(); i++) {
                                JSONObject acceso = accesos.getJSONObject(i);
                                String codigo = acceso.getString("modulo_codigo");
                                switch (codigo) {
                                    case "0":  findViewById(R.id.btnReiniciarSesion).setVisibility(View.VISIBLE); break;
                                    case "1":  btnUsuario.setVisibility(View.VISIBLE); break;
                                    case "2":  findViewById(R.id.btnModuloProducto).setVisibility(View.VISIBLE); break;
                                    case "3":  btnAccesos.setVisibility(View.VISIBLE); break;
                                    case "4":  findViewById(R.id.btnModuloPedido).setVisibility(View.VISIBLE); break;
                                    case "5":  findViewById(R.id.btnModuloBitacora).setVisibility(View.VISIBLE); break;
                                }
                            }
                        }
                    } catch (JSONException e) { e.printStackTrace(); }
                },
                error -> {
                    //temporal
                    Log.e("ACCESOS_ERROR", error.toString());
                    Toast.makeText(this, "Error cargando accesos", Toast.LENGTH_SHORT).show();
                }
        );

        Volley.newRequestQueue(this).add(request);
    }
}