package com.example.minijuegosapp.tic_tac_toe;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.minijuegosapp.R;

public class ResultDialog extends Dialog {

    private final String mensaje;
    private final Tic_Tac_Toe tic_tac_toe;

    public ResultDialog(@NonNull Context context, String mensaje, Tic_Tac_Toe tic_tac_toe) {
        super(context);
        this.mensaje=mensaje;
        this.tic_tac_toe=tic_tac_toe;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_result_dialog_tic_tac_toe);

        TextView mensajeTexto = findViewById(R.id.mensajeTexto);
        Button btnIniciarDeNuevo = findViewById(R.id.btnIniciarDeNuevo);

        mensajeTexto.setText(mensaje);
        btnIniciarDeNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tic_tac_toe.reiniciar();
                dismiss();
            }
        });
        //ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
          //  Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
          //  v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
          //  return insets;
       // });
    }
}