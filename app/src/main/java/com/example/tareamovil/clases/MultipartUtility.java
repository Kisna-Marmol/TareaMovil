package com.example.tareamovil.clases;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MultipartUtility {
    private static final String TAG = "MultipartUtility";

    public interface UploadCallback
    {
        void onSuccess(String response);
        void onError(String error);
    }

    public static void uploadFile(String requestURL, File file, Map<String, String> params, UploadCallback callback)
    {
        new Thread(() -> {
            HttpURLConnection connection = null;
            DataOutputStream outputStream = null;
            BufferedReader inputStream = null;

            String boundary = "*****" + System.currentTimeMillis() + "*****";
            String lineEnd = "\r\n";
            String twoHyphens = "--";

            try
            {
                URL url = new URL(requestURL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setConnectTimeout(60000);  // 60 segundos
                connection.setReadTimeout(60000);

                outputStream = new DataOutputStream(connection.getOutputStream());

                // 🔹 Agregar parámetros de texto
                if (params != null)
                {
                    for (Map.Entry<String, String> entry : params.entrySet())
                    {
                        outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                        outputStream.writeBytes("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + lineEnd);
                        outputStream.writeBytes(lineEnd);
                        outputStream.writeBytes(entry.getValue() + lineEnd);
                    }
                }

                // 🔹 Agregar archivo
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"imagen\"; filename=\"" + file.getName() + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: image/jpeg" + lineEnd);  // Ajustar según tipo real
                outputStream.writeBytes(lineEnd);

                // 🔹 Leer y enviar archivo
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1)
                {
                    outputStream.write(buffer, 0, bytesRead);
                }
                fileInputStream.close();

                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                outputStream.flush();

                // 🔹 Leer respuesta
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK)
                {
                    inputStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = inputStream.readLine()) != null)
                    {
                        response.append(line);
                    }
                    inputStream.close();
                    connection.disconnect();

                    // 🔹 Callback en thread principal
                    android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                    mainHandler.post(() -> callback.onSuccess(response.toString()));
                }
                else
                {
                    throw new Exception("HTTP error: " + responseCode);
                }

            } catch (Exception e)
            {
                Log.e(TAG, "Upload error: " + e.getMessage());
                android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
            finally
            {
                if (outputStream != null) try { outputStream.close(); } catch (Exception ignored) {}
                if (inputStream != null) try { inputStream.close(); } catch (Exception ignored) {}
                if (connection != null) connection.disconnect();
            }
        }).start();
    }
}
