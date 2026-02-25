package com.example.tareamovil;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.tareamovil.clases.ApiService;
import com.example.tareamovil.clases.Config;
import com.example.tareamovil.clases.Dialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class CrearcionUsuarios extends AppCompatActivity {

    private EditText txtDNI, txtNombre, txtApellido, txtCorreo, txtUser, txtClave, txtTelefono;
    private Button btnCrear;
    private Spinner spEstado;

    private ListView listViewUsuarios;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crearcion_usuarios);

        txtDNI = findViewById(R.id.txtDni);
        txtNombre = findViewById(R.id.txtNombre);
        txtApellido = findViewById(R.id.txtApellido);
        txtCorreo = findViewById(R.id.txtCorreo);
        txtTelefono = findViewById(R.id.txtTelefono);
        txtUser = findViewById(R.id.txtNewUser);
        txtClave = findViewById(R.id.txtNewClave);
        spEstado = findViewById(R.id.spEstado);

        String[] estados = {"ACTIVO", "INACTIVO"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                estados
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstado.setAdapter(adapter);

        btnCrear = findViewById(R.id.btnCrearUsuario);

        btnCrear.setOnClickListener(View -> validarCampos());

        listViewUsuarios = findViewById(R.id.listViewUsuarios);
        cargarUsuarios();

    }

    public void cargarUsuarios(){
        String URL = Config.local + "listar_usuario.php";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                URL,
                null,
                response -> {
                    List<String[]> listaUsuarios = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            String nombre   = obj.getString("user_nombre");
                            String apellido = obj.getString("user_apellido");
                            String usuario  = obj.getString("user_usuario");
                            listaUsuarios.add(new String[]{nombre + " " + apellido, usuario});
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    actualizarListView(listaUsuarios);
                },
                error -> Toast.makeText(this, "Error al cargar usuarios", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void actualizarListView(List<String[]> listaUsuarios) {
        ArrayAdapter<String[]> adapterLista = new ArrayAdapter<String[]>(
                this, R.layout.item_usuario, listaUsuarios) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext())
                            .inflate(R.layout.item_usuario, parent, false);
                }
                String[] item = getItem(position);
                TextView nombre  = convertView.findViewById(R.id.txtItemNombreCompleto);
                TextView usuario = convertView.findViewById(R.id.txtItemUsuario);
                nombre.setText(item[0]);
                usuario.setText(item[1]);
                return convertView;
            }
        };
        listViewUsuarios.setAdapter(adapterLista);
    }

    private void validarCampos()
    {
        if(txtUser.getText().toString().trim().equals("")||
                txtClave.getText().toString().trim().equals("")||
                txtApellido.getText().toString().trim().equals("")||
                txtNombre.getText().toString().trim().equals("")||
                txtCorreo.getText().toString().trim().equals("") ||
                txtTelefono.getText().toString().trim().equals("") ||
                txtDNI.getText().toString().trim().equals(""))
        {
            Dialog.toast(CrearcionUsuarios.this,"Debe de Ingresar todos los Campos");
        }
        else
        {
            crearUser();
        }
    }
    private void crearUser()
    {
        String usuario = txtUser.getText().toString();
        String pass = txtClave.getText().toString();
        String apellido = txtApellido.getText().toString();
        String nombre = txtNombre.getText().toString();
        String correo = txtCorreo.getText().toString();
        String telefono = txtTelefono.getText().toString();
        String dni = txtDNI.getText().toString().trim();//etDni.getText().toString().trim();
        boolean estado=true;

        ApiService.guardarUsuario(dni, nombre, apellido, correo, usuario, pass, telefono, estado, new ApiService.ApiCallback()
                {
                    @Override
                    public void onSuccess(String response)
                    {
                        // AGREGA ESTA LÍNEA PARA VER LA RESPUESTA
                        Log.e("CREAR_USER_RESPONSE", response);

                        //progressDialog.dismiss();
                        try {
                            JSONObject res = new JSONObject(response);
                            if (res.getBoolean("success"))
                            {
                                int idNuevo = res.getInt("id");
                                //Toast.makeText(FormUsuarioActivity.this,
                                //    "✅ Usuario creado exitosamente\nID: " + idNuevo,
                                //      Toast.LENGTH_LONG).show();
                                //Dialog.msgbox(UserActivity.this,"Exito","Usuario Guardado Satisfactoriamente",R.drawable.ok);
                                Dialog.toast(CrearcionUsuarios.this,"Exitoso");
                                setResult(RESULT_OK);
                                navegarALogin();
                                //finish();
                            }
                            else
                            {
                                String error = res.optString("error", "Error desconocido");
                                //Toast.makeText(FormUsuarioActivity.this,
                                //        "❌ " + error,
                                //        Toast.LENGTH_LONG).show();
                                //Dialog.msgbox(UserActivity.this,"Error","Error al Ingresar Usuario "+error,R.drawable.error);
                                Dialog.toast(CrearcionUsuarios.this,"Error "+error);
                            }
                        }
                        catch (Exception e)
                        {
                            //Toast.makeText(FormUsuarioActivity.this, "Error procesando respuesta", Toast.LENGTH_SHORT).show();
                            //Dialog.msgbox(UserActivity.this,"Error","Error Procesando respuesta ",R.drawable.error);
                            Dialog.toast(CrearcionUsuarios.this,"Error "+e.getMessage());
                        }
                    }
                    @Override
                    public void onError(String error)
                    {
                        // AGREGA ESTA LÍNEA
                        Log.e("CREAR_USER_ERROR", error);
                        //progressDialog.dismiss();
                        Toast.makeText(CrearcionUsuarios.this,
                                "❌ Error: " + error,
                                Toast.LENGTH_LONG).show();
                        //Dialog.msgbox(UserActivity.this,"Error","Error "+error,R.drawable.error);
                    }
                }
        );
        //Dialog.msgbox(UserActivity.this,"Información","Usuario "+nombreu+" creado Satisfactoriamente",R.drawable.ok);
    }
    private void navegarALogin() {
        Intent intent = new Intent(CrearcionUsuarios.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Cierra SplashActivity
    }
}