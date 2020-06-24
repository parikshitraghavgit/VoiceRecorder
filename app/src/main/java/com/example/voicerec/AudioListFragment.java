package com.example.voicerec;


import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.IOException;



/**
 * A simple {@link Fragment} subclass.
 */
public class AudioListFragment extends Fragment implements AudioListAdapter.onItemListClickbabu{

private ConstraintLayout playersheet;
private BottomSheetBehavior bottomSheetBehavior;
private RecyclerView audioList;
private File[] allFiles;
private AudioListAdapter audioListAdapter;
private MediaPlayer mediaPlayer =null;
private boolean isPlaying = false;
private File fileToPlay;
private ImageButton playBtn;
private TextView playerHeader,playerFilename;
private SeekBar playerseekBar;
private Handler seekbarHandler;
private Runnable updateSeekbar;

    public AudioListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_audio_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        playersheet = view.findViewById(R.id.player_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(playersheet);
        audioList = view.findViewById(R.id.audio_list_view);
        playBtn = view.findViewById(R.id.player_play_btn);
        playerHeader = view.findViewById(R.id.player_header_title);
        playerFilename = view.findViewById(R.id.player_filename);
        playerseekBar = view.findViewById(R.id.player_seekBar);

        String path = getActivity().getExternalFilesDir("/").getAbsolutePath();
        File directory = new File(path);
        allFiles = directory.listFiles();

        audioListAdapter = new AudioListAdapter(allFiles,this);
        audioList.setHasFixedSize(true);
        audioList.setLayoutManager(new LinearLayoutManager(getContext()));
        audioList.setAdapter(audioListAdapter);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_HIDDEN)
                {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


    playBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(isPlaying){
            if(fileToPlay!=null)
            {
                playBtn.setImageResource(R.drawable.media_player_pause);

                pauseAudio();
            }
            }
            else{resumeAudio();
                playBtn.setImageResource(R.drawable.media_player_play);

            }
        }
    });


    playerseekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
if(fileToPlay!=null)
{
    pauseAudio();
}

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if(fileToPlay!=null)
            {int progress = playerseekBar.getProgress();
            mediaPlayer.seekTo(progress);
                resumeAudio();
            }

        }
    });

    }


    @Override
    public void onClickListener(File file, int position) {
        fileToPlay = file;
        if(isPlaying)
        {stopAudio();
        playAudio(fileToPlay);
        }
        else {

            playAudio(fileToPlay);

        }

    }




    private void stopAudio() {

        playBtn.setImageResource(R.drawable.media_player_play);
        playerHeader.setText("stopped");
        isPlaying = false;
        mediaPlayer.stop();
        seekbarHandler.removeCallbacks(updateSeekbar);
    }

    private void playAudio(File fileToPlay) {

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
playBtn.setImageResource(R.drawable.media_player_pause);
playerFilename.setText(fileToPlay.getName());
playerHeader.setText("playing");

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stopAudio();
                playerHeader.setText("finished");
            }
        });

        playerseekBar.setMax(mediaPlayer.getDuration());
        seekbarHandler = new Handler();
     updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar,0);

        isPlaying = true;
    }//playAudio ended

    private void updateRunnable() {
        updateSeekbar = new Runnable() {
            @Override
            public void run() {
                playerseekBar.setProgress(mediaPlayer.getCurrentPosition());
                seekbarHandler.postDelayed(this,500);

            }
        };
    }


    public void pauseAudio(){
mediaPlayer.pause();
    isPlaying = false;
        seekbarHandler.removeCallbacks(updateSeekbar);
    }

    public void resumeAudio(){
        mediaPlayer.start();
    isPlaying = true;
    updateRunnable();
    seekbarHandler.postDelayed(updateSeekbar,0);

    }

    @Override
    public void onStop() {
        super.onStop();
        if(isPlaying){
            stopAudio();
        }
    }
}
