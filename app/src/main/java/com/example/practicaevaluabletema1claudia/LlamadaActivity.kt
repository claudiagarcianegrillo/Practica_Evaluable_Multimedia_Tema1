package com.example.practicaevaluabletema1claudia

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

// Esto es una etiqueta para poder identificar nuestros mensajes en Logcat
private const val TAG = "LlamadaActivity"

//Esta es la pantalla de la llamada
class LlamadaActivity : AppCompatActivity() {

    /*Esto son las variables de la clase que las inicializamos más tarde*/
    private lateinit var textoGrande: TextView
    private lateinit var textoTelefono: TextView
    private lateinit var botonLlamar: ImageButton
    private lateinit var botonVolver: Button

    /*Esta variable guarda el telefono que ponemos en la pantalla de configuración*/
    private var telefono: String? = ""

    /*Aquí preparamos cómo pedir el permiso y qué hacer con la respuesta del usuario*/
    private val lanzadorPermiso = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            //El usuario ha pulsado permitir
            Log.d(TAG, "El usuario ha concedido el permiso")
            llamar()
        } else {
            // Si el usuario pulsa en denegar
            Toast.makeText(this, "Habilita los permisos", Toast.LENGTH_LONG).show()
        }
    }

    //Se ejecuta al abrir la pantalla
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_llamada)
        initEvent()
        showData()
    }

    //Esto enlaza las vistas y pone los eventos de los botones
    //El findViewById busca cada elemento del xml y lo guarda
    private fun initEvent() {
        textoGrande = findViewById(R.id.textoGrande)
        textoTelefono = findViewById(R.id.textoTelefono)
        botonLlamar = findViewById(R.id.botonLlamar)
        botonVolver = findViewById(R.id.botonVolver)

        //Cuando pulsamos el primer botón (el de llamar), pide el permiso
        botonLlamar.setOnClickListener {
            pedirPermisos()
        }

        //Cuando pulsamos el botón de volver, se cierra la pantalla y vuelve a la anterior
        botonVolver.setOnClickListener {
            finish()
        }
    }

    //Esto lee los datos que envió la pantalla anterior y los muestra
    private fun showData() {
        val nombre = getIntent().getStringExtra("nombre")
        telefono = getIntent().getStringExtra("telefono")

        textoGrande.text = "Llamar a $nombre"
        textoTelefono.text = telefono
    }

    //Esto decide si hay que pedir permiso o se puede llamar directamente
    private fun pedirPermisos() {
        //Esto muestra en el Logcat la versión de android del dispositivo
        Log.d(TAG, "API del dispositivo: ${Build.VERSION.SDK_INT}")

        // A partir de Android 6 (API 23, "M") los permisos se piden en tiempo de ejecución
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (tienePermiso()) {
                //Como ya se había aceptado los permisos se puede llamar directamente
                Log.d(TAG, "API >= 23 y el permiso si estaba concedido")
                llamar()
            } else {
                //No tenemos permisos asi que se lo pedimos al usuario
                Log.d(TAG, "API >= 23 y el permiso NO está concedido: se lo pedimos al usuario")
                lanzadorPermiso.launch(Manifest.permission.CALL_PHONE)
            }
        } else {
            //En Android antiguo (< 23) el permiso se concede al instalar la app por lo que no hay que pedir permisos
            Log.d(TAG, "API < 23: no hace falta pedir permisos")
            llamar()
        }
    }

    //Esto devuelve true si el permiso CALL_PHONE ya está concedido
    private fun tienePermiso(): Boolean =
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED

    /*Esto lanza la llamada telefónica, se indica el número con formato y se lanza el intent
    y entonces se abre la llamada*/
    private fun llamar() {
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:$telefono")
        }
        startActivity(intent)
    }
}