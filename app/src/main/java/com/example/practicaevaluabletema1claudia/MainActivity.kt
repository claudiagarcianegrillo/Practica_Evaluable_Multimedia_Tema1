package com.example.practicaevaluabletema1claudia

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.AlarmClock
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

//Esta es la pantalla principal, que nos permite llamar, abrir una URL, crear una alarma y enviar un correo
class MainActivity : AppCompatActivity() {

    //Estas son las variables que las inicializamos luego
    private lateinit var textoNombre: TextView
    private lateinit var campoUrl: EditText
    private lateinit var botonLlamada: ImageButton
    private lateinit var botonUrl: ImageButton
    private lateinit var botonAlarma: ImageButton
    private lateinit var botonCorreo: ImageButton
    private lateinit var botonConfig: Button

    //Estas variables son el nombre y el telefono que nos llegan del ConfigActivity
    private var nombre: String? = ""
    private var telefono: String? = ""

    //Esto se ejecuta al abrir la pantalla
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initEvent()
        showData()
    }

    //Esto enlaza vistas y pone los eventos de los botones
    private fun initEvent() {
        textoNombre = findViewById(R.id.textoNombre)
        campoUrl = findViewById(R.id.campoUrl)
        botonLlamada = findViewById(R.id.botonLlamada)
        botonUrl = findViewById(R.id.botonUrl)
        botonAlarma = findViewById(R.id.botonAlarma)
        botonCorreo = findViewById(R.id.botonCorreo)
        botonConfig = findViewById(R.id.botonConfig)

        //Esto es un intent explicito que va de esta pantalla a la pantalla de la llamada
        botonLlamada.setOnClickListener {
            val intent = Intent(this, LlamadaActivity::class.java).apply {
                putExtra("nombre", nombre)
                putExtra("telefono", telefono)
            }
            startActivity(intent)
        }

        //Esto es un intent implicito ya que le estamos pediendo que abra el navegador
        botonUrl.setOnClickListener {
            // Leemos lo que se ha escrito en el campo y quitamos espacios sobrantes con el trim()
            var direccion = campoUrl.text.toString().trim()
            if (direccion.isEmpty()) {
                direccion = "https://www.google.es" //Esta es la URL por defecto si no se pone nada
            }
            // Si no empieza por http:// ni https://, se lo añadimos para que sea una URL válida
            if (!direccion.startsWith("http://") && !direccion.startsWith("https://")) {
                direccion = "https://$direccion"
            }

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(direccion))

            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "No hay navegador instalado en este emulador", Toast.LENGTH_LONG).show()
            }
        }

        //Esto es el botón de la alarma que es un intent implicito a la app de Reloj
        botonAlarma.setOnClickListener {
            //Esto es una variable que guarda la fecha y la hora actual
            val calendario = Calendar.getInstance()
            //Esto le suma 2 minutos a la hora que sea
            calendario.add(Calendar.MINUTE, 2)
            val hora = calendario.get(Calendar.HOUR_OF_DAY)
            val minutos = calendario.get(Calendar.MINUTE)

            //Esto crea una alarma
            val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                putExtra(AlarmClock.EXTRA_MESSAGE, "Práctica Evaluable Multimedia - $nombre")
                putExtra(AlarmClock.EXTRA_HOUR, hora)
                putExtra(AlarmClock.EXTRA_MINUTES, minutos)
                putExtra(AlarmClock.EXTRA_SKIP_UI, true)
            }

            try {
                startActivity(intent)
                // "%02d" muestra los minutos con 2 cifras en vez de 05 que sea 5)
                Toast.makeText(this, "¡Alarma creada a las $hora:%02d!".format(minutos), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Este emulador no soporta la aplicación de Reloj", Toast.LENGTH_LONG).show()
            }
        }

        //Este es el botón del correo que es un intent implicito para enviar el email
        botonCorreo.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf("profesor@clase.es"))
                putExtra(Intent.EXTRA_SUBJECT, "Práctica Tema 1 Claudia - $nombre")
                putExtra(Intent.EXTRA_TEXT, "Envío la práctica evaluable correctamente.")
            }
            try {
                startActivity(Intent.createChooser(intent, "Enviar correo usando..."))
            } catch (e: Exception) {
                Toast.makeText(this, "No hay aplicación de correo configurada", Toast.LENGTH_LONG).show()
            }
        }

        //Este es el boton para volver a la pantalla de configuración
        botonConfig.setOnClickListener {
            val intent = Intent(this, ConfigActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            startActivity(intent)
        }
    }

    //Esto recoge los datos recibidos/guardados y los muestra
    private fun showData() {
        //Esto recoge el nombre que nos envia la pantalla de configuración
        nombre = intent.getStringExtra("nombre")

        val preferencias = getSharedPreferences("configuracion", Context.MODE_PRIVATE)
        telefono = preferencias.getString("telefono", "112")
        val urlGuardada = preferencias.getString("url", "")

        if (!urlGuardada.isNullOrEmpty()) {
            campoUrl.setText(urlGuardada)
        }

        //Esto muestra el nombre que se ha registrado en la pantalla de configuración y si no se ha metido ninguno sale Usuario
        if (!nombre.isNullOrEmpty()) {
            textoNombre.text = "Hola, $nombre"
        } else {
            textoNombre.text = "Hola, Usuario"
        }
    }
}