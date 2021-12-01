package com.example.apprelogio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

public class FullscreenActivity extends AppCompatActivity {

    private ViewHolder mViewHolder = new ViewHolder();
    private Handler handler = new Handler();
    private Runnable runnable;
    private boolean runnableStopped = false;
    private boolean cbBateriaChecked = true;

    private BroadcastReceiver bateriaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int nivel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0 );
            mViewHolder.textNivelBateria.setText(String.valueOf(nivel) + "%");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);


        mViewHolder.textHorasMinutos = findViewById(R.id.textHorasMinutos);
        mViewHolder.textSegundos = findViewById(R.id.textSegundos);
        mViewHolder.cbNivelBateria = findViewById(R.id.cbNivelBateria);
        mViewHolder.textNivelBateria = findViewById(R.id.textNivelBateria);
        mViewHolder.iv_preferencias = findViewById(R.id.iv_preferencias);
        mViewHolder.iv_sair = findViewById(R.id.iv_sair);
        mViewHolder.ll_menu = findViewById(R.id.ll_menu);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //tira a barra de bateria, wifi e relogio do celular

        registerReceiver(bateriaReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        mViewHolder.ll_menu.animate().translationY(500);

        mViewHolder.cbNivelBateria.setChecked(true);
        mViewHolder.cbNivelBateria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbBateriaChecked){
                    cbBateriaChecked = false;
                    mViewHolder.textNivelBateria.setVisibility(View.GONE);
                }else{
                    cbBateriaChecked = true;
                    mViewHolder.textNivelBateria.setVisibility(View.VISIBLE);
                }
            }
        });

        mViewHolder.iv_sair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mViewHolder.ll_menu.animate()
                        .translationY(mViewHolder.ll_menu.getMeasuredHeight())
                        .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
            }
        });

        mViewHolder.iv_preferencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mViewHolder.ll_menu.setVisibility(View.VISIBLE);
                mViewHolder.ll_menu.animate().translationY(0).setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        runnableStopped = false;
        atualizarHoras();
    }

    @Override
    protected void onStop() {
        super.onStop();
        runnableStopped = true;
    }

    private void atualizarHoras() {

        runnable = new Runnable() {
            @Override
            public void run() {

                if (runnableStopped)
                    return;

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());

                String horasMinutosAtualizados = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE));
                String segundosAtualizados = String.format("%02d", calendar.get(Calendar.SECOND));

                mViewHolder.textHorasMinutos.setText(horasMinutosAtualizados);
                mViewHolder.textSegundos.setText(segundosAtualizados);

                //colocar os segundos funcionando
                long agora = SystemClock.uptimeMillis();
                long proximo = agora + (1000 - (agora % 1000));

                handler.postAtTime(runnable, proximo);
            }
        };
        runnable.run();


    }

    private static class ViewHolder{
        TextView textHorasMinutos, textSegundos, textNivelBateria;
        CheckBox cbNivelBateria;
        ImageView iv_preferencias, iv_sair;
        LinearLayout ll_menu;

    }
}