package com.example.practicaevaluabletema1claudia

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/*Esta es la pantalla de configuración y sirve para que el usuario
* meta sus datos y la app recuerde esos datos*/
class ConfigActivity : AppCompatActivity() {

    //Preparamos las variables para las casillas del texto y el botón, las inicializamos después
    private lateinit var campoNombre : EditText
    private lateinit var campoTelefono : EditText
    private lateinit var campoUrl : EditText
    private lateinit var botonGuardar : Button

    /*Esto es lo primero que se ejecuta al abrir la pantalla*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Esto dibuja la pantalla visualmente
        setContentView(R.layout.activity_config)
        //Conecta el código con los botones que va haber en la pantalla
        initEvent()
        //Si ya hemos guardado los datos antes, los recupera y los pone directamente
        cargarPreferencias()

    }

    /*Esto va a buscar los elementos visuales y les da la orden de reacciones
    cuando hagamos clic*/
    private fun initEvent() {
        //variables con su ID real del archivo xml
        campoNombre = findViewById(R.id.campoNombre)
        campoTelefono = findViewById(R.id.campoTelefono)
        campoUrl = findViewById(R.id.campoUrl)
        botonGuardar = findViewById(R.id.botonGuardar)

        //si el usuario le da al botón de guardar
        botonGuardar.setOnClickListener {
            //revisamos que las casillas que son 'obligatorias'
            if (campoNombre.text.toString().isEmpty() || campoTelefono.text.toString().isEmpty()) {
                //si falta algún dato por meter, salta un aviso flotante
                Toast.makeText(this, "Rellena el nombre y el teléfono", Toast.LENGTH_LONG).show()
            } else {
                //si todos los datos están rellenos, se guardan y podemos ir a la siguiente pantalla
                guardarPreferencias()
                abrirPrincipal()
            }
        }
    }

    /*Este metodo lo que hace es guardar los datos que hemos puesto
    * para que no se borren al cerrar la aplicación*/
    private fun guardarPreferencias() {
        val preferencias = getSharedPreferences("configuracion", Context.MODE_PRIVATE)

        preferencias.edit()
            .putString("nombre", campoNombre.text.toString())
            .putString("telefono", campoTelefono.text.toString())
            .putString("url", campoUrl.text.toString())
            .apply() // Guarda los cambios de forma definitiva
    }

    /*Lee los datos que se guardaron la última vez y los vuelve a escribir
    * en los huecos correspondientes*/
    private fun cargarPreferencias() {
        val preferencias = getSharedPreferences("configuracion", Context.MODE_PRIVATE)

        campoNombre.setText(preferencias.getString("nombre", ""))
        campoTelefono.setText(preferencias.getString("telefono", ""))
        campoUrl.setText(preferencias.getString("url", ""))
    }

    /*Prepara un intent para que cerrar la pantalla de la configuracion
    * y abrir la pantalla principal que es la del MainActivity y pasarle el
    * campo del nombre*/
    private fun abrirPrincipal() {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("nombre", campoNombre.text.toString())
        }
        startActivity(intent)
    }
}