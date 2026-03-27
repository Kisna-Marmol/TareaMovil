package com.example.tareamovil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.tareamovil.clases.ApiService2;
import com.example.tareamovil.clases.Config;
import com.example.tareamovil.clases.MultipartUtility;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class FotoActivity extends AppCompatActivity {
    private int userid;
    private ImageView imgfoto;
    private Button btngaleria;
    private Button btncamara;
    private Button btnguardar;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int TAKE_PHOTO_REQUEST = 2;
    private Bitmap bitmapfoto;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto);

        Intent intent=getIntent();

        userid=Config.iduser;//intent.getIntExtra("id",1);
        btncamara=findViewById(R.id.btnCamara);
        btngaleria=findViewById(R.id.btnGaleria);
        btnguardar=findViewById(R.id.btnGuardar);

        imgfoto=findViewById(R.id.imgFoto);

        showImagen();

        btngaleria.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                abrirGaleria();
            }
        });

        btncamara.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                tomarFoto();
            }
        });

        btnguardar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                guardarFoto();
            }
        });
    }
    private void abrirGaleria()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    public void showImagen()
    {
        if (userid <= 0)return;

        //Primero obtener la URL de la imagen
        StringRequest getImageRequest = new StringRequest(
                Request.Method.POST,
                ApiService2.BASE_URL + "foto/get_image.php",
                response -> {
                    try
                    {
                        JSONObject json = new JSONObject(response);
                        boolean success = json.getBoolean("success");

                        if (success)
                        {
                            String imageUrl = json.optString("imagen_url", null);

                            if (imageUrl != null && !imageUrl.isEmpty())
                            {
                                //Descargar y mostrar imagen manualmente
                                Log.d("URL","URL"+ApiService2.BASE_URL+" IMGURL "+imageUrl);
                                downloadAndShowImage(imageUrl);
                            }
                            else
                            {
                                imgfoto.setImageResource(R.drawable.ok);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                },
                error -> Log.e("SHOW_IMAGE", "Error: " + error.getMessage())
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                params.put("userid", String.valueOf(userid));
                return params;
            }
        };
        ApiService2.getInstance(this).addToRequestQueue(getImageRequest);
    }
    //Método auxiliar para descargar imagen y mostrar
    private void downloadAndShowImage(String imageUrl)
    {
        new Thread(() -> {
            try
            {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();

                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                input.close();

                // 🔹 Mostrar en UI thread
                runOnUiThread(() -> {
                    if (bitmap != null) {
                        imgfoto.setImageBitmap(bitmap);
                    }
                    else
                    {
                        imgfoto.setImageResource(R.drawable.ok);
                    }
                });
            }
            catch (Exception e)
            {
                Log.e("SHOW_IMAGE", "Error descargando imagen: " + e.getMessage());
                runOnUiThread(() -> imgfoto.setImageResource(R.drawable.ok));
            }
        }).start();
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            Bitmap bitmap = null;
            if (requestCode == PICK_IMAGE_REQUEST && data != null)
            {
                Uri uri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                } catch (IOException e) {
                    Toast.makeText(this, "Error al cargar imagen de galería", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            else if (requestCode == TAKE_PHOTO_REQUEST && data != null)
            {
                bitmap = (Bitmap) data.getExtras().get("data"); // Foto tomada por la cámara
            }
            if (bitmap != null)
            {
                bitmapfoto=bitmap;

                byte[] imageBytes = bitmapToBytes(bitmapfoto);
                imgfoto.setImageBitmap(bitmapfoto);
                btnguardar.setVisibility(View.VISIBLE);
            }
        }
    }
    private byte[] bitmapToBytes(Bitmap bitmap)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream); // Puedes usar PNG también
        return stream.toByteArray();
    }
    public void guardarFoto()
    {
        if (bitmapfoto == null)
        {
            Toast.makeText(this, "No hay imagen para guardar", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔹 Mostrar progreso
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Subiendo imagen...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // 🔹 Guardar bitmap como archivo temporal
        File tempFile;
        try
        {
            // Crear archivo en cache directory
            tempFile = File.createTempFile("upload_", ".jpg", getCacheDir());
            FileOutputStream fos = new FileOutputStream(tempFile);
            bitmapfoto.compress(Bitmap.CompressFormat.JPEG, 85, fos); // Calidad 85%
            fos.flush();
            fos.close();
        }
        catch (IOException e)
        {
            progressDialog.dismiss();
            Toast.makeText(this, "Error al preparar imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return;
        }

        // 🔹 Preparar parámetros
        Map<String, String> params = new HashMap<>();
        params.put("userid", String.valueOf(userid));

        // 🔹 URL de subida
        String uploadURL = ApiService2.BASE_URL + "upload_image.php";
        Log.d("UPLOAD", "Subiendo a: " + uploadURL);

        // 🔹 Ejecutar subida con MultipartUtility
        MultipartUtility.uploadFile(uploadURL, tempFile, params, new MultipartUtility.UploadCallback() {
            @Override
            public void onSuccess(String response)
            {
                progressDialog.dismiss();
                Log.d("UPLOAD", "✅ Respuesta: " + response);

                try
                {
                    JSONObject json = new JSONObject(response);
                    boolean success = json.getBoolean("success");
                    String message = json.getString("message");

                    Toast.makeText(FotoActivity.this, message, Toast.LENGTH_LONG).show();

                    if (success)
                    {
                        if (json.has("imagen_url"))
                        {
                            String ruta = json.getString("imagen_url");
                            Log.d("UPLOAD", "Imagen guardada en: " + ruta);
                        }

                        // Regresar a actividad anterior
                        /*Intent intent = new Intent(FotoActivity.this, Bienvenido.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);*/
                        finish();
                    }
                }
                catch (Exception e)
                {
                    Toast.makeText(FotoActivity.this, "Error al procesar respuesta", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } finally
                {
                    if (tempFile.exists()) tempFile.delete();
                }
            }

            @Override
            public void onError(String error)
            {
                progressDialog.dismiss();
                Log.e("UPLOAD", "❌ Error: " + error);
                Toast.makeText(FotoActivity.this, "Error de subida: " + error, Toast.LENGTH_LONG).show();
                if (tempFile.exists()) tempFile.delete();
            }
        });


    }
    // Método auxiliar: Bitmap a Base64
    private String bitmapToBase64(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // Comprimir a JPEG con 80% de calidad para reducir tamaño
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }
    private void tomarFoto()
    {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
        {
            // Pedimos el permiso
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            // Ya tenemos permiso, abrimos la cámara
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, TAKE_PHOTO_REQUEST);
            } else {
                Toast.makeText(this, "No hay aplicación de cámara disponible", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                tomarFoto(); // Abrimos la cámara después de obtener el permiso
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}