package com.example.tareamovil.modelos;

import org.json.JSONObject;

public class Cliente {

    private String cliente_dni;
    private String cliente_nombre;
    private String cliente_apellido;
    private String cliente_dir;
    private String cliente_municipio;
    private String cliente_depto;
    private String cliente_pais;
    private String cliente_fnac;
    private double cliente_limitecredicto;
    private String cliente_rtn;
    private String cliente_estado;

    public Cliente() {}

    // Constructor desde JSONObject — lo usa cargarClientes()
    public Cliente(JSONObject obj) {
        try {
            this.cliente_dni            = obj.optString("cliente_dni", "");
            this.cliente_nombre         = obj.optString("cliente_nombre", "");
            this.cliente_apellido       = obj.optString("cliente_apellido", "");
            this.cliente_dir            = obj.optString("cliente_dir", "");
            this.cliente_municipio      = obj.optString("cliente_municipio", "");
            this.cliente_depto          = obj.optString("cliente_depto", "");
            this.cliente_pais           = obj.optString("cliente_pais", "");
            String fnac = obj.optString("cliente_fnac", "");
            // Si viene vacío o es fecha nula de MySQL, guarda vacío
            this.cliente_fnac = (fnac.startsWith("0000") || fnac.isEmpty()) ? "" : fnac.substring(0, 10);
            this.cliente_limitecredicto = obj.optDouble("cliente_limitecredicto", 0);
            this.cliente_rtn            = obj.optString("cliente_rtn", "");
            this.cliente_estado         = obj.optString("cliente_estado", "ACTIVO");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Getters y Setters
    public String getCliente_dni()                       { return cliente_dni; }
    public void   setCliente_dni(String v)               { this.cliente_dni = v; }

    public String getCliente_nombre()                    { return cliente_nombre; }
    public void   setCliente_nombre(String v)            { this.cliente_nombre = v; }

    public String getCliente_apellido()                  { return cliente_apellido; }
    public void   setCliente_apellido(String v)          { this.cliente_apellido = v; }

    public String getCliente_dir()                       { return cliente_dir; }
    public void   setCliente_dir(String v)               { this.cliente_dir = v; }

    public String getCliente_municipio()                 { return cliente_municipio; }
    public void   setCliente_municipio(String v)         { this.cliente_municipio = v; }

    public String getCliente_depto()                     { return cliente_depto; }
    public void   setCliente_depto(String v)             { this.cliente_depto = v; }

    public String getCliente_pais()                      { return cliente_pais; }
    public void   setCliente_pais(String v)              { this.cliente_pais = v; }

    public String getCliente_fnac()                      { return cliente_fnac; }
    public void   setCliente_fnac(String v)              { this.cliente_fnac = v; }

    public double getCliente_limitecredicto()            { return cliente_limitecredicto; }
    public void   setCliente_limitecredicto(double v)    { this.cliente_limitecredicto = v; }

    public String getCliente_rtn()                       { return cliente_rtn; }
    public void   setCliente_rtn(String v)               { this.cliente_rtn = v; }

    public String getCliente_estado()                    { return cliente_estado; }
    public void   setCliente_estado(String v)            { this.cliente_estado = v; }

    // Lo que muestra cada fila del ListView
    @Override
    public String toString() {
        return "DNI: " + cliente_dni + "  |  " + cliente_nombre + " " + cliente_apellido
                + "\n" + cliente_municipio + ", " + cliente_depto
                + "  |  Estado: " + cliente_estado;
    }
}
