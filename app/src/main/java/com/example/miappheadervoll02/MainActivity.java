package com.example.miappheadervoll02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import WebService.Asynchtask;
import WebService.WebService;

public class MainActivity extends AppCompatActivity implements Asynchtask
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Map<String, String> datos = new HashMap<String, String>();
        datos.put("correo", "carlos@gmail.com");
        datos.put("clave", "12345678");
        WebService wsGetToken = new WebService("https://api.uealecpeterson.net/public/login"
                ,datos, MainActivity.this, MainActivity.this);
        wsGetToken.execute("POST");
    }
    @Override
    public void processFinish(String result) throws JSONException
    {
        TextView txtSaludo = (TextView) findViewById(R.id.txtmostrarproductos);
        JSONObject res = new JSONObject(result);
        String token = res.getString("access_token");

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://api.uealecpeterson.net/public/productos/search";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        String lsproduc = "";
                        String id, nombre, subnombre;
                        TextView txtSaludo = (TextView)findViewById(R.id.txtmostrarproductos);
                        try {
                            JSONObject JSONres = new JSONObject(response);
                            JSONArray  JSONlista = JSONres.getJSONArray("productos");
                            for(int i=0; i< JSONlista.length();i++) {
                                JSONObject respo = JSONlista.getJSONObject(i);
                                id = respo.getString("id");
                                nombre = respo.getString("p_categoria");
                                subnombre = respo.getString("p_subcategoria");

                                lsproduc = lsproduc + "<<<<<<<<>>>>>>>>" + "\n" +"ID "+ id + "\n" +"Nombre de producto:  " + nombre + "\n" + "Producto: "+ subnombre +  "\n\n";
                            }
                        }catch (Exception e)
                        {

                        }
                        txtSaludo.setText(lsproduc);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        txtSaludo.setText("ERROR"+ error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("fuente", "1");
                //params.put("clave", "12345678");
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer "+ token);
                return headers;
            }
        };
        requestQueue.add(stringRequest);
    }
}