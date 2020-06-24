package com.example.voicerec;


import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment implements View.OnClickListener {

    private NavController navController;
    private ImageButton listBtn;
    private ImageButton recordBtn;
    private boolean isRecording = false;
    private MediaRecorder mediaRecorder;
    private String recordFile;
    private Chronometer timer;
    private TextView filenameText;

    public RecordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        listBtn = view.findViewById(R.id.record_list_btn);
        recordBtn = view.findViewById(R.id.record_btn);
        timer = view.findViewById(R.id.record_timer);
        filenameText = view.findViewById(R.id.record_filename);

        listBtn.setOnClickListener(this);
        recordBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.record_list_btn:
                navController.navigate(R.id.action_recordFragment_to_audioListFragment);
                break;

            case R.id.record_btn:
if(isRecording){
    //stop recording
stopRecording();
    isRecording = false;
    recordBtn.setImageResource(R.drawable.record_btn_stopped);
    Toast.makeText(getContext(),"recording Stopped",Toast.LENGTH_SHORT).show();
}

     else{//start recording

if(checkPermissions())
{
    startRecording();

    isRecording = true;
    recordBtn.setImageResource(R.drawable.record_btn_recording);
    Toast.makeText(getContext(),"Recording Started",Toast.LENGTH_SHORT).show();
}
}
                break;

        }
    }








    private void startRecording() {

        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();
        String recordPath = getActivity().getExternalFilesDir("/").getAbsolutePath();
SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.CANADA);
Date now = new Date();
        recordFile = "Filename_"+formatter.format(now)+".3gp";
        mediaRecorder =new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath+"/"+recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
filenameText.setText("Recording "+recordFile);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
mediaRecorder.start();


    }




    private void stopRecording() {
        timer.stop();
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder =null;
        filenameText.setText("Recorded "+recordFile);
    }




    private boolean checkPermissions() {
    if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED)
    {return true;}
    else {
        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.RECORD_AUDIO},21);
        return false;
    }
    }

    @Override
    public void onStop() {
        super.onStop();
      if(isRecording){
          stopRecording();
      }
    }
}