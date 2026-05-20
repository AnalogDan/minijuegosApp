package com.example.minijuegosapp.tic_tac_toe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.minijuegosapp.MainActivity;
import com.example.minijuegosapp.R;

public class AddPlayers extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_players_tic_tac_toe);

        EditText jugador1 = findViewById(R.id.jugadorUno);
        EditText jugador2 = findViewById(R.id.jugadorDos);
        Button btnIniciar = findViewById(R.id.btnIniciar);

        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String getjugadorUnoNombre = jugador1.getText().toString();
                String getjugadorDosNombre = jugador2.getText().toString();

                if (getjugadorUnoNombre.isEmpty()||getjugadorDosNombre.isEmpty()){
                    Toast.makeText(AddPlayers.this, "Por favor introduce el nombre del jugador", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(AddPlayers.this, MainActivity.class);
                    intent.putExtra("jugador1", getjugadorUnoNombre);
                    intent.putExtra("jugador2", getjugadorDosNombre);
                    startActivity(intent);
                }
            }
        });




        //ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
          //  Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            //v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            //return insets;
        //});
    }
}