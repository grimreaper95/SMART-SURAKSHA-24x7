package shashank.grimreaper.smartsuraksha24x7;

        import android.content.DialogInterface;
        import android.support.annotation.Nullable;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.support.v7.widget.Toolbar;
        import android.view.SurfaceHolder;
        import android.view.Window;
        import android.view.WindowManager;

        import net.majorkernelpanic.streaming.Session;
        import net.majorkernelpanic.streaming.SessionBuilder;
        import net.majorkernelpanic.streaming.audio.AudioQuality;
        import net.majorkernelpanic.streaming.gl.SurfaceView;
        import net.majorkernelpanic.streaming.rtsp.RtspClient;

        import java.util.regex.Matcher;
        import java.util.regex.Pattern;

/**
 * Created by Shashank on 08-02-2017.
 */

public class LiveStreaming extends AppCompatActivity implements RtspClient.Callback,
        Session.Callback, SurfaceHolder.Callback {
    public final static String TAG = LiveStreaming.class.getSimpleName();
    // surfaceview
    private static SurfaceView mSurfaceView;

    // Rtsp session
    private Session mSession;
    private static RtspClient mClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.live_streaming);


        Toolbar toolbar = (Toolbar)findViewById(R.id.mytoolbar3);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }



        mSurfaceView = (SurfaceView) findViewById(R.id.surface);

        mSurfaceView.getHolder().addCallback(this);
        initRtspClient();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void onBitrateUpdate(long bitrate) {

    }

    @Override
    public void onSessionError(int reason, int streamType, Exception e) {
        switch (reason) {
            case Session.ERROR_CAMERA_ALREADY_IN_USE:
                break;
            case Session.ERROR_CAMERA_HAS_NO_FLASH:
                break;
            case Session.ERROR_INVALID_SURFACE:
                break;
            case Session.ERROR_STORAGE_NOT_READY:
                break;
            case Session.ERROR_CONFIGURATION_NOT_SUPPORTED:
                break;
            case Session.ERROR_OTHER:
                break;
        }

        if (e != null) {
            alertError(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onPreviewStarted() {

    }

    @Override
    public void onSessionConfigured() {

    }

    @Override
    public void onSessionStarted() {

    }

    @Override
    public void onSessionStopped() {

    }

    @Override
    public void onRtspUpdate(int message, Exception exception) {
        switch (message) {
            case RtspClient.ERROR_CONNECTION_FAILED:
            case RtspClient.ERROR_WRONG_CREDENTIALS:
                alertError(exception.getMessage());
                exception.printStackTrace();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        toggleStreaming();
    }

    @Override
    protected void onPause() {
        super.onPause();
        toggleStreaming();
    }

    private void initRtspClient() {
        // Configures the SessionBuilder
        mSession = SessionBuilder.getInstance()
                .setContext(getApplicationContext())
                .setAudioEncoder(SessionBuilder.AUDIO_NONE)
                .setAudioQuality(new AudioQuality(8000, 16000))
                .setVideoEncoder(SessionBuilder.VIDEO_H264)
                .setSurfaceView(mSurfaceView).setPreviewOrientation(0)
                .setCallback(this).build();

        // Configures the RTSP client
        mClient = new RtspClient();
        mClient.setSession(mSession);
        mClient.setCallback(this);
        mSurfaceView.setAspectRatioMode(SurfaceView.ASPECT_RATIO_PREVIEW);
        String ip, port, path;

        // We parse the URI written in the Editext
        Pattern uri = Pattern.compile("rtsp://(.+):(\\d+)/(.+)");
        Matcher m = uri.matcher(AppConfig.STREAM_URL);
        m.find();
        ip = m.group(1);
        port = m.group(2);
        path = m.group(3);

        mClient.setCredentials(AppConfig.PUBLISHER_USERNAME,
                AppConfig.PUBLISHER_PASSWORD);
        mClient.setServerAddress(ip, Integer.parseInt(port));
        mClient.setStreamPath("/" + path);
    }

    private void toggleStreaming() {
        if (!mClient.isStreaming()) {
            // Start camera preview
            mSession.startPreview();
            // Start video stream
            mClient.startStream();
        } else {
            // already streaming, stop streaming
            // stop camera preview
            mSession.stopPreview();
            // stop streaming
            mClient.stopStream();
        }
    }
    private void alertError(final String msg) {
        final String error = (msg == null) ? "Unknown error: " : msg;
        AlertDialog.Builder builder = new AlertDialog.Builder(LiveStreaming.this);
        builder.setMessage(error).setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        mClient.release();
        mSession.release();
        mSurfaceView.getHolder().removeCallback(this);
    }
}