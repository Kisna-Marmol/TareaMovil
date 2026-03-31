package com.example.tareamovil.clases;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Locale;

public class ApiService2
{
    private static ApiService2 instance;
    private RequestQueue requestQueue;
    private static Context ctx;

    // URL de tu API (cámbiala por tu dominio real)
    public static final String BASE_URL = "http://kisna.bonaquian.com/movil1/";

    private ApiService2(Context context)
    {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized ApiService2 getInstance(Context context) {
        if (instance == null) {
            instance = new ApiService2(context.getApplicationContext());
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    // Interfaz para callbacks
    public interface ApiCallback {
        void onSuccess(JSONObject response);
        void onError(String error);
    }

    public <T> void addToRequestQueue(Request<T> req)
    {
        getRequestQueue().add(req);
    }

    public void guardarUbicacion(double latitud, double longitud, int userId, String fechaReg, ApiService2.ApiCallback callback) {
        String url = Config.UBI_SAVE;

        Log.d("COORDENADAS",""+latitud);
        Log.d("COORDENADAS",""+longitud);
        Log.d("COORDENADAS","" + userId);

        try {
            JSONObject params = new JSONObject();
            params.put("latitud", String.format(Locale.US,"%.8f", latitud));
            params.put("longitud", String.format(Locale.US,"%.8f", longitud));
            params.put("user_id", userId);
            //params.put("fechareg", fechaReg);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST, url, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getBoolean("success")) {
                                    callback.onSuccess(response);
                                } else {
                                    callback.onError(response.getString("message"));
                                }
                            } catch (Exception e) {
                                callback.onError("Error procesando respuesta: " + e.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            callback.onError("Error de red: " + (error.getMessage() != null ? error.getMessage() : "Desconocido"));
                        }
                    }
            );

            requestQueue.add(request);

        } catch (Exception e) {
            callback.onError("Error preparando request: " + e.getMessage());
        }
    }
}