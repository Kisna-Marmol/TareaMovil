package com.example.tareamovil;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.tareamovil.clases.Bitacora;

public class DatoDispoActivity extends AppCompatActivity {

    TextView txtmarca, txtmodelo, txthost,txtip,txtmac;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dato_dispo);
        txtmarca=findViewById(R.id.tvMarca);
        txtmodelo=findViewById(R.id.tvModelo);
        txthost=findViewById(R.id.tvHost);
        txtip=findViewById(R.id.tvIP);
        txtmac=findViewById(R.id.tvMAC);
        llenar();
    }
    private void llenar()
    {
        Bitacora bit=new Bitacora();
        txtmarca.setText(bit.getMarca());
        txtmodelo.setText(bit.getModelo());
        txthost.setText(bit.getHostName(this));
        txtip.setText(bit.getIP() != null ? bit.getIP() : "No disponible");
        txtmac.setText(bit.getDirMac(this));
    }
}