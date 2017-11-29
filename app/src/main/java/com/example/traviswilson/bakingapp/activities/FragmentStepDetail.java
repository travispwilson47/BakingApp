package com.example.traviswilson.bakingapp.activities;


import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import com.example.traviswilson.bakingapp.R;
import com.example.traviswilson.bakingapp.data.BakingContract;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import static android.R.attr.x;

/**
 * Created by traviswilson on 11/1/17.
 * Note that we use MediaPlayer here instead of ExoPlayer because of the simplicity of the video
 * requirements.
 */
//
public class FragmentStepDetail extends Fragment implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnVideoSizeChangedListener, SurfaceHolder.Callback, MediaController.MediaPlayerControl, MediaPlayer.OnSeekCompleteListener {

    private static final String CURRENT_POSITION_KEY = "mediaPlayerPosition";
    private static final String LOG_TAG = FragmentStepDetail.class.toString();
    private String recipeStepProjection[] = {BakingContract.RecipeMain.TABLE_NAME+ "." + BakingContract.RecipeMain._ID
            , BakingContract.RecipeStep.VIDEO_URL, BakingContract.RecipeStep.DESCRIPTION,
    BakingContract.RecipeStep.STEP_ID};

    private String sharedPreferencesFileName = "stepPrefsFile";
    private int mPosition;

    private String recipeName;
    private String videoURL;
    private String description;

    private MediaPlayer mediaPlayer;
    private MediaController mediaControler;
    private SurfaceView surfaceView;
    private SurfaceHolder holder;

    private Handler handler = new Handler();

    private boolean modeTablet;

    private View rootView;
    private boolean videoUrlPresent;
    private boolean aspectsSet;


    //TODO: Save instance state of the video playing (for device rotation)
    //TODO: Check device rotation so as not to initalize views that don't exit
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Log.v(LOG_TAG, "Made it to onCreate");
        if (savedInstanceState != null){
            mPosition = savedInstanceState.getInt(sharedPreferencesFileName);
        } else{
            mPosition = -1;
            if ((recipeName = getArguments().getString(ActivityDetail.RECIPE_NAME_KEY)) != null){
                description = getArguments().getString(ActivityDetail.RECIPE_DESCRIPTION_KEY);
                videoURL = getArguments().getString(ActivityDetail.RECIPE_VIDEO_URL_KEY);
            }
            //TODO: detect mode tablet
            modeTablet = false;
        }
    }
    @Override
    public void onDestroy(){
        Log.v(LOG_TAG+"leak possiblity", "onDestroy called"+(mediaPlayer!=null));
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
            mediaControler.setEnabled(false);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        aspectsSet = false;
        View view;
        if (videoURL == null || videoURL.equals("") || videoURL.equals(" ")){
            view = inflater.inflate(R.layout.fragment_step_detail_page_no_video, container, false);
            videoUrlPresent = false;
        } else {
            videoUrlPresent = true;
            view = inflater.inflate(R.layout.fragment_step_detail_page, container, false);
        }
        rootView = view;

        rootView.findViewById(R.id.step_description_textview);

        if (!videoUrlPresent) return view;
        Log.v(LOG_TAG, "video Url: "+videoURL);

        Log.v(LOG_TAG, "video url is as desired" +videoURL.equals("https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffdc33_-intro-brownies/-intro-brownies.mp4"));
        surfaceView = view.findViewById(R.id.surfaceView);
        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mediaControler.show();
                return false;
            }
        });

        holder = surfaceView.getHolder();
        holder.addCallback(this);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); //TODO: fix this

        try {
            Log.v(LOG_TAG, videoURL);
            mediaPlayer.setDataSource(videoURL);

            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnPreparedListener(this);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error on video loading", Toast.LENGTH_SHORT);
            return null;
        }

        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mediaControler.show();
                return false;
            }
        });

        mediaControler = new MediaController(getActivity());
        mediaControler.setAnchorView(surfaceView);
        setSurfaceViewAspects(view);

        return view;
    }
    public Uri getURL(){
        return Uri.parse(videoURL);
    }
    private void setSurfaceViewAspects(View rootView){
        if (videoUrlPresent) {
            try {
                Log.v(LOG_TAG, videoURL);
                final Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        retriever.setDataSource(videoURL, new HashMap<String, String>());
                        Bitmap bmp = retriever.getFrameAtTime();
                        final int videoHeight = bmp.getHeight();
                        final int videoWidth = bmp.getWidth();
                        if (getActivity() != null)
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    finishAspects(videoHeight, videoWidth);
                                }
                            });
                    }
                });
                thread.start();
            } catch (Exception e) {
                e.printStackTrace();
                //TODO: Figure out what UI to display in this case
                return;
            }
        }
    }
    private void finishAspects(int videoHeight, int videoWidth){
        Log.v(LOG_TAG, "videoHeight "+ videoHeight);
        Log.v(LOG_TAG, "videoWidth" + videoWidth);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        int screenWidth = displaymetrics.widthPixels;

        ConstraintSet set = new ConstraintSet();



        int modifiedHeight = (int) (((float)videoHeight / (float)videoWidth) * (float)screenWidth);

        ConstraintLayout rootLayout = rootView.findViewById(R.id.constraintLayout);

        set.clone(rootLayout);

        set.constrainHeight(R.id.surfaceView, (int) (modifiedHeight ));
        set.constrainWidth(R.id.surfaceView, (int) (screenWidth));
        set.centerHorizontally(R.id.surfaceView, R.id.constraintLayout);
        set.applyTo(rootLayout);
        aspectsSet = true;
    }
    @Override
    public void onSaveInstanceState(Bundle b){
        super.onSaveInstanceState(b);
        if (videoURL != null) {
            //b.putInt(CURRENT_POSITION_KEY, getCurrentPosition());
        }
    }
    @Override
    public void onPause(){
        super.onPause();
        if (mediaControler != null) {
            mediaControler.hide();
        }
    }
    public void onPageScrolled(){
//        if (mediaControler != null && mediaPlayer != null){
//            mediaControler.setEnabled(false);
//            mediaControler.hide();
//
//        }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                mediaControler.hide();
//            }
//        });
    }
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        mediaControler.setMediaPlayer(this);
        mediaControler.setAnchorView(surfaceView);
        handler.post(new Runnable() { //below code can't be executed on UI thread.

            public void run() {
                while (!aspectsSet) {
                    try {
                        Thread.sleep(10); //wait 10 mills and try again, waiting for aspects to be setup
                        //before enabling the mediaControler
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                mediaControler.setEnabled(true);
                mediaControler.show();
            }
        });
        mediaPlayer.setOnSeekCompleteListener(this);
        if (mPosition != -1) { // device rotation or some other reason to save state.
            mediaPlayer.seekTo(mPosition);
            //wait until seekTo is finished to call start (done with listener)
        } else{
            mediaPlayer.start();
        }
    }
    //TODO: See if any of this stuff is necessary to implement (probably not
    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mediaPlayer.setDisplay(surfaceHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        } else{
            return 0;
        }
    }

    @Override
    public void seekTo(int i) {
        mediaPlayer.seekTo(i);
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }
}
