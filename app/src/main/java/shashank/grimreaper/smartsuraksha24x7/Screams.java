package shashank.grimreaper.smartsuraksha24x7;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Shashank on 24-10-2016.
 */

public class Screams extends AppCompatActivity {

    boolean flag = false;
    MediaPlayer mp = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screams);
        Toolbar toolbar = (Toolbar)findViewById(R.id.mytoolbar2);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        Button male, female, police;
        male = (Button) findViewById(R.id.maleScream);
        female = (Button) findViewById(R.id.femaleScream);
        police = (Button) findViewById(R.id.policeSiren);

        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag)
                    stopPlaying();
                else{
                    mp = MediaPlayer.create(Screams.this, R.raw.male_scream);
                    mp.start();
                    flag = true;
                    Toast.makeText(Screams.this, "Press again to stop.", Toast.LENGTH_LONG).show();
                }
            }
        });


        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(flag)
                    stopPlaying();
                else{
                    mp = MediaPlayer.create(Screams.this, R.raw.girl_screaming);
                    mp.start();
                    flag = true;
                    Toast.makeText(Screams.this, "Press again to stop.", Toast.LENGTH_LONG).show();
                }
            }
        });


        police.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(flag)
                    stopPlaying();
                else{
                    mp = MediaPlayer.create(Screams.this,R.raw.police_siren);
                    mp.start();
                    flag = true;
                    Toast.makeText(Screams.this, "Press again to stop.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void stopPlaying() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
            flag = false;
        }
    }
}
