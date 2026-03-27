package com.example.tareamovil;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tareamovil.clases.ApiService;
import com.example.tareamovil.clases.Bitacora;
import com.example.tareamovil.clases.Config;
import com.example.tareamovil.clases.Dialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLData;

public class Login extends AppCompatActivity {
    Button btningresar, btncrear;
    TextView txtUser, txtClave, lblConexion;

    CheckBox chRecordarme;

    // Claves para SharedPreferences
    private static final String PREFS_NOMBRE   = "LoginPrefs";
    private static final String CLAVE_RECORDAR = "recordarme";
    private static final String CLAVE_USUARIO  = "usuario";

    private static final String CLAVE_CLAVE    = "clave";
    //@SuppressLint("MissingInflatedId")
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        lblConexion = findViewById(R.id.lblConexion);

        txtUser = findViewById(R.id.txtUsuario);
        txtClave = findViewById(R.id.txtClave);
        chRecordarme = findViewById(R.id.chRecordarme);
        btningresar = findViewById(R.id.btnIngresar);
        btncrear = findViewById(R.id.btnCrearCuenta);
        btningresar.setOnClickListener(View -> validarCampos());
        btncrear.setOnClickListener(View -> llamarCrearUser());

        cargarUsuarioGuardado();
        verificarConexionBD();

    }

    public static void registrarBitacora(int userId, String desc, String modulo, Context ctx) {
        new Bitacora(userId, desc, ctx, modulo).insert(new ApiService.ApiCallback() {
            @Override
            public void onSuccess(String r) { Log.d("BITACORA", "OK: " + modulo); }
            @Override
            public void onError(String e)   { Log.e("BITACORA", "ERR: " + e); }
        });
    }

    //Empiezar codigo recordarme
    private void cargarUsuarioGuardado(){
        SharedPreferences prefs = getSharedPreferences(PREFS_NOMBRE, Context.MODE_PRIVATE);
        boolean recordar = prefs.getBoolean(CLAVE_RECORDAR, false);

        Log.d("RECORDARME", "Recordar: " + recordar +
                " | Usuario: " + prefs.getString(CLAVE_USUARIO, "vacío"));

        if (recordar) {
            String usuarioGuardado = prefs.getString(CLAVE_USUARIO, "");
            String claveGuardada   = prefs.getString(CLAVE_CLAVE, "");
            txtUser.setText(usuarioGuardado);
            txtClave.setText(claveGuardada);
            chRecordarme.setChecked(true);
        }
    }

    private void guardarUsuario(String usuario, String clave) {
        SharedPreferences.Editor editor =
                getSharedPreferences(PREFS_NOMBRE, Context.MODE_PRIVATE).edit();
        editor.putBoolean(CLAVE_RECORDAR, true);
        editor.putString(CLAVE_USUARIO, usuario);
        editor.putString(CLAVE_CLAVE, clave);
        editor.apply();

        // ← Agrega esto para verificar en Logcat
        Log.d("RECORDARME", "Usuario guardado: " + usuario);
    }

    private void borrarUsuarioGuardado() {
        SharedPreferences.Editor editor =
                getSharedPreferences(PREFS_NOMBRE, Context.MODE_PRIVATE).edit();
        editor.putBoolean(CLAVE_RECORDAR, false);
        editor.remove(CLAVE_USUARIO);
        editor.remove(CLAVE_CLAVE);
        editor.apply();
    }
    //Fin codigo recordarme
    private void verificarConexionBD() {
        String url = Config.local + "estado.php";

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        String estado = json.getString("status");
                        String mensaje = json.getString("message");

                        lblConexion.setText(mensaje);

                        if (estado.equals("online")) {
                            lblConexion.setTextColor(Color.BLACK);
                        } else {
                            lblConexion.setTextColor(Color.RED);
                        }

                    } catch (JSONException e) {
                        lblConexion.setText("Error al leer respuesta");
                        lblConexion.setTextColor(Color.RED);
                    }
                },
                error -> {
                    lblConexion.setText("Sin conexion al servidor");
                    lblConexion.setTextColor(Color.RED);
                }
        );

        queue.add(request);
    }

    public void validarCampos()
    {
        if(txtUser.getText().toString().trim().equals(""))
        {
            //Dialog.msgbox(MainActivity.this,"Inválido","Ingrese el Usuario",R.drawable.error);
            Dialog.toast(Login.this,"Favor Ingrese el Usuario");
            txtUser.requestFocus();
        }
        else if(txtClave.getText().toString().trim().equals(""))
        {
            //Dialog.msgbox(MainActivity.this,"Inválido","Ingrese la Clave",R.drawable.error);
            Dialog.toast(Login.this,"Favor Ingrese la Clave");
            txtClave.requestFocus();
        }
        else
        {
            String usuario=txtUser.getText().toString();
            String clave=txtClave.getText().toString();

            // ← Guardar el estado ANTES de llamar la API
            boolean recordar = chRecordarme.isChecked(); // ← Capturar aquí
            confirmarCredenciales(usuario, clave, recordar); // ← Pasar el valor
            //Dialog.msgbox(MainActivity.this,"Informacion","Entramos Bien",R.drawable.ok);
        }
    }
    private void confirmarCredenciales(String usuario, String clave, boolean recordar)
    {
        ApiService.login(usuario, clave, new ApiService.LoginCallback() {
            @Override
            public void onSuccess(JSONObject userdata) {
                try
                {
                    boolean success=userdata.getBoolean("success");
                    if(success)
                    {
                        String username = userdata.getJSONObject("data").getString("user_nombre");
                        int userId = Integer.parseInt(userdata.getJSONObject("data").getString("user_id"));
                        //txtestado.setText("✅ Bienvenido, " + username + "!");
                        //Dialog.msgbox(MainActivity.this,"Exito","Bienvenido "+username,R.drawable.ok);
                        Config.usuario=usuario;
                        Config.iduser = userId;

                        Log.d("RECORDARME", "Checkbox marcado: " + recordar);

                        if (recordar) {
                            guardarUsuario(usuario, clave);
                        } else {
                            borrarUsuarioGuardado();
                        }
                        //navegarPral();
                        Log.e("LOGIN EXITOSO",usuario);
                        Dialog.toast(Login.this,"Exito");
                        //TEMPORAL
                        Log.e("LOGIN_USERID", "Enviando userId: " + userId);
                        //PASAR EL NOMBRE DE USUARIO A LA PANTALLA DE BIENVENIDA

                        Log.d("RECORDARME", "Checkbox marcado: " + chRecordarme.isChecked());

                        // ← AGREGADO — registrar ingreso al sistema
                        registrarBitacora(userId, "Ingreso al Sistema", "0", Login.this);
                        // ── FIN AGREGADO

                        llamarPral(username, userId);
                    }
                    else
                    {
                        String errorMsg = userdata.optString("error", "Credenciales inválidas");
                        //txtestado.setText("❌ " + errorMsg);
                        Dialog.msgbox(Login.this, "Error", errorMsg, R.drawable.error);
                        //btnentrar.setEnabled(true);
                        Dialog.toast(Login.this,"Error "+errorMsg);
                    }
                }catch (Exception e)
                {
                    Log.e("LOGIN_PARSE", e.getMessage());
                    //txtestado.setText("⚠️ Error procesando respuesta");
                    //Dialog.msgbox(MainActivity.this, "Error", "Error en la respuesta", R.drawable.error);
                    //btnentrar.setEnabled(true);
                    Dialog.toast(Login.this,"Error "+e.getMessage());
                }
            }
            public void onError(String errorMessage) {
                //btnentrar.setEnabled(true);
                //txtestado.setText("❌ " + errorMessage);
                //Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                //Dialog.msgbox(MainActivity.this, "Error", errorMessage, R.drawable.error);
                Dialog.toast(Login.this,"Error "+errorMessage);
            }
        });
    }
    private void llamarPral(String nombreUsuario, int userId)
    {
        Intent intent = new Intent(Login.this, Bienvenido.class);
        intent.putExtra("Nombre_Usuario", nombreUsuario);
        intent.putExtra("User_ID", userId);
        startActivity(intent);
        finish(); // Elimina el splash del stack para que no se regrese a él
    }
    private void llamarCrearUser()
    {
        Intent intent = new Intent(Login.this, CrearcionUsuarios.class);
        startActivity(intent);
        finish(); // Elimina el splash del stack para que no se regrese a él
    }
}