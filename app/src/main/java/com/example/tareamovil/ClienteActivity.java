package com.example.tareamovil;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.tareamovil.clases.ApiService;
import com.example.tareamovil.clases.Config;
import com.example.tareamovil.clases.Dialog.ConfirmationDialogCallback;
import com.example.tareamovil.clases.Dialog;
import com.example.tareamovil.modelos.Cliente;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ClienteActivity extends AppCompatActivity {

    // ── Tu código original ────────────────────────────────────────────────────
    ListView lvclientes;
    ArrayList<Cliente> listclientes;
    ArrayAdapter<Cliente> listAdapter;

    // ── Lo que se agrega: campos del formulario ───────────────────────────────
    TextView  tvTitulo;
    EditText  etDni, etNombre, etApellido, etDireccion,
            etMunicipio, etDepartamento, etPais,
            etFechaNacimiento, etLimiteCredito, etRTN;
    Spinner   spinnerEstado;
    Button    btnNuevo, btnGuardar;

    // DNI del cliente que se está editando; null = modo INSERT
    String dniEditando = null;

    // ─────────────────────────────────────────────────────────────────────────
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*getWindow().setSoftInputMode(
                android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        );*/

        setContentView(R.layout.activity_cliente);


        // ── Tu línea original ─────────────────────────────────────────────────
        lvclientes = findViewById(R.id.lvClientes);

        // ── Se agregan los demás views ────────────────────────────────────────
        tvTitulo          = findViewById(R.id.tvTitulo);
        etDni             = findViewById(R.id.etIdentificacion);
        etNombre          = findViewById(R.id.etNombre);
        etApellido        = findViewById(R.id.etApellido);
        etDireccion       = findViewById(R.id.etDireccion);
        etMunicipio       = findViewById(R.id.etMunicipio);
        etDepartamento    = findViewById(R.id.etDepartamento);
        etPais            = findViewById(R.id.etPais);
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento);
        etLimiteCredito   = findViewById(R.id.etLimiteCredito);
        etRTN             = findViewById(R.id.etRTN);
        spinnerEstado     = findViewById(R.id.spinnerEstado);
        btnNuevo          = findViewById(R.id.btnCrear);
        btnGuardar        = findViewById(R.id.btnGuardar);

        setupSpinner();
        setupListeners();

        // ── Tu llamada original ───────────────────────────────────────────────
        cargarClientes();
    }

    // ─── Spinner estados ──────────────────────────────────────────────────────
    private void setupSpinner() {
        ArrayAdapter<String> sa = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"ACTIVO", "INACTIVO"});
        sa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(sa);
    }

    // ─── Listeners ────────────────────────────────────────────────────────────
    private void setupListeners() {

        btnNuevo.setOnClickListener(v -> modoNuevo());

        btnGuardar.setOnClickListener(v -> {
            if (!validar()) return;
            if (dniEditando == null) insertarCliente();
            else                    actualizarCliente();
        });

        // Toca un item → carga en formulario para editar
        lvclientes.setOnItemClickListener((parent, view, position, id) ->
                cargarEnFormulario(listclientes.get(position)));

        // Mantén presionado → confirmar eliminación
        lvclientes.setOnItemLongClickListener((parent, view, position, id) -> {
            Cliente c = listclientes.get(position);
            Dialog.confirm(this,
                    "Eliminar cliente",
                    "¿Desea eliminar a " + c.getCliente_nombre() + " " + c.getCliente_apellido() + "?",
                    android.R.drawable.ic_dialog_alert,
                    new ConfirmationDialogCallback() {
                        @Override public void onConfirm() { eliminarCliente(c.getCliente_dni()); }
                        @Override public void onCancel()  { }
                    });
            return true;
        });

        etFechaNacimiento.setOnClickListener(v -> {
            // Toma la fecha actual o la que ya tiene el campo
            int year = 2000, month = 0, day = 1;
            String actual = etFechaNacimiento.getText().toString();
            if (actual.matches("\\d{4}-\\d{2}-\\d{2}")) {
                String[] parts = actual.split("-");
                year  = Integer.parseInt(parts[0]);
                month = Integer.parseInt(parts[1]) - 1; // mes es 0-based
                day   = Integer.parseInt(parts[2]);
            }

            new android.app.DatePickerDialog(this,
                    (picker, y, m, d) -> {
                        // Formatea como yyyy-MM-dd para enviar a MySQL
                        etFechaNacimiento.setText(String.format("%04d-%02d-%02d", y, m + 1, d));
                    },
                    year, month, day).show();
        });
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Tu método original cargarClientes() — solo se reemplaza JDBC por ApiService
    // ═══════════════════════════════════════════════════════════════════════════
    private void cargarClientes() {
        listclientes = new ArrayList<Cliente>();

        ApiService.get(Config.local + "cliente_list.php", new ApiService.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONArray arr = new JSONArray(response);
                    for (int i = 0; i < arr.length(); i++) {
                        listclientes.add(new Cliente(arr.getJSONObject(i)));
                    }
                } catch (Exception exp) {
                    // Mismo Log que tenías antes
                    Log.e("Error", exp.getMessage());
                }
                listAdapter = new ArrayAdapter<>(ClienteActivity.this,
                        android.R.layout.simple_list_item_1, listclientes);
                lvclientes.setAdapter(listAdapter);
            }
            @Override
            public void onError(String error) {
                Log.e("Error", error);
                Dialog.toast(ClienteActivity.this, "Error al cargar: " + error);
            }
        });
    }

    // ─── INSERT ───────────────────────────────────────────────────────────────
    private void insertarCliente() {
        ApiService.post(Config.local + "cliente_insert.php", buildJson(),
                new ApiService.ApiCallback() {
                    @Override
                    public void onSuccess(String response) {
                        handleRespuesta(response, "✅ Cliente creado", true);
                    }
                    @Override
                    public void onError(String error) {
                        Log.e("Error", error);
                        Dialog.msgbox(ClienteActivity.this, "Error de red", error,
                                android.R.drawable.ic_dialog_alert);
                    }
                });
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────
    private void actualizarCliente() {
        ApiService.post(Config.local + "cliente_update.php", buildJson(),
                new ApiService.ApiCallback() {
                    @Override
                    public void onSuccess(String response) {
                        handleRespuesta(response, "✏️ Cliente actualizado", true);
                    }
                    @Override
                    public void onError(String error) {
                        Log.e("Error", error);
                        Dialog.msgbox(ClienteActivity.this, "Error de red", error,
                                android.R.drawable.ic_dialog_alert);
                    }
                });
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────
    private void eliminarCliente(String dni) {
        JSONObject json = new JSONObject();
        try { json.put("cliente_dni", dni); }
        catch (Exception exp) { Log.e("Error", exp.getMessage()); }

        ApiService.post(Config.local + "cliente_delete.php", json,
                new ApiService.ApiCallback() {
                    @Override
                    public void onSuccess(String response) {
                        handleRespuesta(response, "🗑️ Cliente eliminado", false);
                    }
                    @Override
                    public void onError(String error) {
                        Log.e("Error", error);
                        Dialog.msgbox(ClienteActivity.this, "Error de red", error,
                                android.R.drawable.ic_dialog_alert);
                    }
                });
    }

    // ─── Maneja {"success":true} o {"error":"..."} de cualquier operación ─────
    private void handleRespuesta(String response, String msgOk, boolean limpiar) {
        try {
            JSONObject res = new JSONObject(response);
            if (res.optBoolean("success", false)) {
                Dialog.toast(this, msgOk);
                if (limpiar) modoNuevo();
                // Recarga igual que al inicio
                cargarClientes();
            } else {
                Dialog.msgbox(this, "Error",
                        res.optString("error", "Operación fallida"),
                        android.R.drawable.ic_dialog_alert);
            }
        } catch (Exception exp) {
            Log.e("Error", exp.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Helpers UI
    // ═══════════════════════════════════════════════════════════════════════════

    private void modoNuevo() {
        dniEditando = null;
        etDni.setEnabled(true);
        limpiarFormulario();
        tvTitulo.setText("Nuevo Cliente");
        btnGuardar.setText("💾 Guardar Cliente");
    }

    private void cargarEnFormulario(Cliente c) {
        dniEditando = c.getCliente_dni();
        etDni.setText(c.getCliente_dni());
        etDni.setEnabled(false);          // PK, no se modifica
        etNombre.setText(c.getCliente_nombre());
        etApellido.setText(c.getCliente_apellido());
        etDireccion.setText(c.getCliente_dir());
        etMunicipio.setText(c.getCliente_municipio());
        etDepartamento.setText(c.getCliente_depto());
        etPais.setText(c.getCliente_pais());
        etFechaNacimiento.setText(c.getCliente_fnac());
        etLimiteCredito.setText(String.valueOf(c.getCliente_limitecredicto()));
        etRTN.setText(c.getCliente_rtn());
        ArrayAdapter<String> sa = (ArrayAdapter<String>) spinnerEstado.getAdapter();
        spinnerEstado.setSelection(sa.getPosition(
                c.getCliente_estado() != null ? c.getCliente_estado() : "ACTIVO"));
        tvTitulo.setText("Editando: " + c.getCliente_nombre());
        btnGuardar.setText("✏️ Actualizar Cliente");
    }

    private void limpiarFormulario() {
        etDni.setText(""); etNombre.setText(""); etApellido.setText("");
        etDireccion.setText(""); etMunicipio.setText(""); etDepartamento.setText("");
        etPais.setText(""); etFechaNacimiento.setText("");
        etLimiteCredito.setText(""); etRTN.setText("");
        spinnerEstado.setSelection(0);
        etDni.requestFocus();
    }

    private boolean validar() {
        if (etDni.getText().toString().trim().isEmpty()) {
            etDni.setError("Requerido"); etDni.requestFocus(); return false;
        }
        if (etNombre.getText().toString().trim().isEmpty()) {
            etNombre.setError("Requerido"); etNombre.requestFocus(); return false;
        }
        if (etApellido.getText().toString().trim().isEmpty()) {
            etApellido.setError("Requerido"); etApellido.requestFocus(); return false;
        }
        return true;
    }

    private JSONObject buildJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("cliente_dni",           etDni.getText().toString().trim());
            json.put("cliente_nombre",         etNombre.getText().toString().trim());
            json.put("cliente_apellido",       etApellido.getText().toString().trim());
            json.put("cliente_dir",            etDireccion.getText().toString().trim());
            json.put("cliente_municipio",      etMunicipio.getText().toString().trim());
            json.put("cliente_depto",          etDepartamento.getText().toString().trim());
            json.put("cliente_pais",           etPais.getText().toString().trim());
            json.put("cliente_fnac",           etFechaNacimiento.getText().toString().trim());
            String lim = etLimiteCredito.getText().toString().trim();
            json.put("cliente_limitecredicto", lim.isEmpty() ? 0 : Double.parseDouble(lim));
            json.put("cliente_rtn",            etRTN.getText().toString().trim());
            json.put("cliente_estado",         spinnerEstado.getSelectedItem().toString());
        } catch (Exception exp) {
            Log.e("Error", exp.getMessage());
        }
        return json;
    }
}