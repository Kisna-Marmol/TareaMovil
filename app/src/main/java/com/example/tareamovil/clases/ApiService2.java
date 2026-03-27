package com.example.tareamovil.clases;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

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

    public <T> void addToRequestQueue(Request<T> req)
    {
        getRequestQueue().add(req);
    }
}