package com.example.minijuegosapp.tic_tac_toe;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.minijuegosapp.R;

import java.util.ArrayList;
import java.util.List;

public class Tic_Tac_Toe extends AppCompatActivity {

    private final List<int[]> listaConbinada = new ArrayList<>();
    private int[] posicionesCaja ={0,0,0,0,0,0,0,0,0};
    private int turnoJugador = 1;
    private int totalDeCajasSeleccionadas = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tic_tac_toe);

        listaConbinada.add(new int[]{0,1,2});
        listaConbinada.add(new int[]{3,4,5});
        listaConbinada.add(new int[]{6,7,8});
        listaConbinada.add(new int[]{0,3,6});
        listaConbinada.add(new int[]{1,4,7});
        listaConbinada.add(new int[]{2,5,8});
        listaConbinada.add(new int[]{2,4,6});
        listaConbinada.add(new int[]{0,4,8});

        String getjugadorUnoNombre = getIntent().getStringExtra("jugador1");
        String getjugadorDosNombre = getIntent().getStringExtra("jugador2");

        ((TextView)findViewById(R.id.jugadorUnoNombre)).setText(getjugadorUnoNombre);
        ((TextView)findViewById(R.id.jugadorDosNombre)).setText(getjugadorUnoNombre);

        findViewById(R.id.image1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cajaUsable(0)){
                    performAction((ImageView) view,0);
                }
            }
        });

        findViewById(R.id.image2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cajaUsable(1)){
                    performAction((ImageView) view,1);
                }
            }
        });

        findViewById(R.id.image3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cajaUsable(2)){
                    performAction((ImageView) view,2);
                }
            }
        });
        findViewById(R.id.image4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cajaUsable(3)){
                    performAction((ImageView) view,3);
                }
            }
        });
        findViewById(R.id.image5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cajaUsable(4)){
                    performAction((ImageView) view,4);
                }
            }
        });
        findViewById(R.id.image6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cajaUsable(5)){
                    performAction((ImageView) view,5);
                }
            }
        });
        findViewById(R.id.image7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cajaUsable(6)){
                    performAction((ImageView) view,6);
                }
            }
        });
        findViewById(R.id.image8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cajaUsable(7)){
                    performAction((ImageView) view,7);
                }
            }
        });
        findViewById(R.id.image9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cajaUsable(8)){
                    performAction((ImageView) view,8);
                }
            }
        });

        //ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
          //  Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
           // v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            //return insets;
    //    });
    }

    private void performAction(ImageView imageView, int seleccionDePosicionCaja){
        posicionesCaja[seleccionDePosicionCaja]=turnoJugador;

        if (turnoJugador==1){
            imageView.setImageResource(R.drawable.x);
        if (resultados()){
            ResultDialog resultadoDialogo=new ResultDialog(Tic_Tac_Toe.this,((TextView)findViewById(R.id.jugadorUnoNombre)).getText().toString()+" es el ganador",Tic_Tac_Toe.this);
            resultadoDialogo.setCancelable(false);
            resultadoDialogo.show();
        }else if (totalDeCajasSeleccionadas==9){
            ResultDialog resultadoDialogo=new ResultDialog(Tic_Tac_Toe.this, " Empate", Tic_Tac_Toe.this);
            resultadoDialogo.setCancelable(false);
            resultadoDialogo.show();
        }else {
            cambiarTurno(2);
            totalDeCajasSeleccionadas++;
        }
        }else {
            imageView.setImageResource(R.drawable.o);
            if (resultados()){
                ResultDialog resultadoDialogo=new ResultDialog(Tic_Tac_Toe.this,((TextView)findViewById(R.id.jugadorDosNombre)).getText().toString()+" es el ganador",Tic_Tac_Toe.this);
                resultadoDialogo.setCancelable(false);
                resultadoDialogo.show();
            }else if (totalDeCajasSeleccionadas==9){
                ResultDialog resultadoDialogo=new ResultDialog(Tic_Tac_Toe.this, " Empate", Tic_Tac_Toe.this);
                resultadoDialogo.setCancelable(false);
                resultadoDialogo.show();
            }else {
                cambiarTurno(1);
                totalDeCajasSeleccionadas++;
            }
        }
    }

    private void cambiarTurno(int turnoActual){
        turnoJugador=turnoActual;
        if (turnoJugador==1){
            findViewById(R.id.jugadorUnoLayout).setBackgroundResource(R.drawable.borde_negro);
            findViewById(R.id.jugadorDosLayout).setBackgroundResource(R.drawable.caja_blanca);
        }else{
            findViewById(R.id.jugadorUnoLayout).setBackgroundResource(R.drawable.caja_blanca);
            findViewById(R.id.jugadorDosLayout).setBackgroundResource(R.drawable.borde_negro);
        }
    }
    private boolean resultados(){
        boolean respuesta = false;
        for(int i=0; i<listaConbinada.size(); i++){
            final int [] combinacion = listaConbinada.get(i);

            if (posicionesCaja[combinacion[0]]== turnoJugador&&posicionesCaja[combinacion[1]]==turnoJugador&&posicionesCaja[combinacion[2]]==turnoJugador){
                respuesta=true;
            }
        }
        return respuesta;
    }
    private boolean cajaUsable(int posicionCaja){
        boolean respuesta = false;
        if(posicionesCaja[posicionCaja]==0){
            respuesta=true;
        }
        return respuesta;
    }
    public void reiniciar (){
        posicionesCaja=new int[]{0,0,0,0,0,0,0,0,0};
        turnoJugador =1;
        totalDeCajasSeleccionadas=1;

        ((ImageView)findViewById(R.id.image1)).setImageResource(R.drawable.caja_blanca);
        ((ImageView)findViewById(R.id.image2)).setImageResource(R.drawable.caja_blanca);
        ((ImageView)findViewById(R.id.image3)).setImageResource(R.drawable.caja_blanca);
        ((ImageView)findViewById(R.id.image4)).setImageResource(R.drawable.caja_blanca);
        ((ImageView)findViewById(R.id.image5)).setImageResource(R.drawable.caja_blanca);
        ((ImageView)findViewById(R.id.image6)).setImageResource(R.drawable.caja_blanca);
        ((ImageView)findViewById(R.id.image7)).setImageResource(R.drawable.caja_blanca);
        ((ImageView)findViewById(R.id.image8)).setImageResource(R.drawable.caja_blanca);
        ((ImageView)findViewById(R.id.image9)).setImageResource(R.drawable.caja_blanca);


    }
}