package shashank.grimreaper.smartsuraksha24x7;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.provider.MediaStore;
import io.vov.vitamio.widget.MediaController;

/**
 * Created by Shashank on 14-04-2017.
 */

public class LiveStreamingUsingVitamio extends AppCompatActivity {
    private String path="rtmp://192.168.109.241:1935/live/android_test";
    //private String path="rtmp://192.168.85.119/live/";
    private io.vov.vitamio.widget.VideoView mVideoView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LibsChecker.checkVitamioLibs(this))  //Important!
            return;
        //Toast.makeText(this, "Sorry, video is not available.", Toast.LENGTH_LONG).show();
        //      return;
        //}
        // else{
        setContentView(R.layout.livestreamingusingvitamio);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        //progressDialog.setIcon(R.drawable.ic_launcher);
        progressDialog.setMessage("Waiting...");
        progressDialog.show();

        MediaController mediaController = new MediaController(this);
        mVideoView = (io.vov.vitamio.widget.VideoView) findViewById(R.id.video_view);
        mVideoView.setVideoPath(path);
        //mVideoView.setVideoQuality(io.vov.vitamio.MediaPlayer.VIDEOQUALITY_LOW);
        //mVideoView.setBufferSize(2048);

        mVideoView.requestFocus();
        mVideoView.start();

        mVideoView.setMediaController(mediaController);
        mVideoView.setOnPreparedListener(new io.vov.vitamio.MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(io.vov.vitamio.MediaPlayer mediaPlayer) {
                // optional need Vitamio 4.0
                progressDialog.dismiss();
                mediaPlayer.setPlaybackSpeed(1.0f);
            }
        });
        //}
    }

    public void startPlay(View view) {
        if (!TextUtils.isEmpty(path)) {
            mVideoView.setVideoPath(path);
        }
    }

    public void openVideo(View View) {
        mVideoView.setVideoPath(path);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
}
