package com.uisrel.scaneolo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Escanear extends AppCompatActivity {

    Button botonScanear;
    TextView tvCodigo;
    EditText tvCodigoBarras;
    TextView tvNombre;
    EditText tvPrecio;
    ImageView imgPrd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escanear);

        botonScanear = findViewById(R.id.btnScanner);
        tvCodigoBarras = findViewById(R.id.etBarras);
        tvNombre = findViewById(R.id.tv1Nombre);
        tvCodigo = findViewById(R.id.tv1Codigo);
        tvPrecio = findViewById(R.id.etPrecio);
        imgPrd=findViewById(R.id.imgProducto);

        botonScanear.setOnClickListener(monClickListener);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents() != null)
                //tvCodigoBarras.setText("El codigo de barras es:\n"+ result.getContents());
                tvCodigoBarras.setText(result.getContents());

            }else {
                tvCodigoBarras.setText("ERROR AL SCANEAR");
            }
    }


    private View.OnClickListener monClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnScanner:
                    new IntentIntegrator(Escanear.this).initiateScan();
                    break;
            }
        }
    };


///////////////////////////////////////////////////
    public void getData(View V){
        //Dirección del servidor donde esta la base de datos, en este caso de nuestra máquina local
        String ws = "http://192.168.1.13:8080/scanealo/post_producto.php";

        //Habilitar permisos de la app para el acceso a nuestro Web service
        StrictMode.ThreadPolicy politica = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(politica);
        URL url = null;
        HttpURLConnection conn;

        //Captura de Excepciones
        try {
            //Llamada a nuestro WS,
            url = new URL(ws);
            //Realizamos un cast de tipo HttpUrlConnection y realizamos la coneccion a la base de datos
            conn = (HttpURLConnection) url.openConnection();
            //Definimos el método que vamos a utilizar del WS
            conn.setRequestMethod("GET");
            conn.connect();

            //Utilizamos un Buffer para la lectura de datos, lo que llegue de GET, se almacena en la variable: in
            BufferedReader in = new BufferedReader((new InputStreamReader(conn.getInputStream())));

            //variables para llenar los datos del GET, el Buffer que llega se lo convierte a String y el String a Json
            String inputLine;
            StringBuffer response = new StringBuffer();
            String json="";

            //Este ciclo hace la conversión antes mencionada: Buffer---> String ----> Json
            while ((inputLine = in.readLine())!=null){
                response.append(inputLine);
            }

            json = response.toString(); //Aqui obtenemos los datos de tipo String
            JSONArray jsonArr=null;

            //Aquí ya obtenemos los datos de tipo Json
            jsonArr = new JSONArray(json);

            //Declaramos las variables donde vamos almacenar los datos obtenidos
            String nombre = "";
            String codigo = "";
            String barras = "";
            String precioVenta = "";

            for (int i = 0; i<jsonArr.length();i++){
                JSONObject objeto = jsonArr.getJSONObject(i);
                nombre =objeto.optString("prdNombre");
                codigo =objeto.optString("prdCodigo");
                barras =objeto.optString("prdCodBarrasQr");
                precioVenta =objeto.optString("prdPrecioVenta");
            }

            tvNombre.setText(nombre);
            tvCodigo.setText(codigo);
            tvPrecio.setText(precioVenta);


        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


/*

    public void consumirServicio(View v){
        String cedula= tvCodigogetText().toString();
        String nombre= et2.getText().toString();
        String apellido= et3.getText().toString();
        int edad= Integer.parseInt( et4.getText().toString());
        post servicioTask= new post(this,"http://192.168.1.3/rest/post.php",cedula,nombre,apellido, edad);
        servicioTask.execute();

    }

*/
}