package com.example.camer.swipetunes.views;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.camer.swipetunes.R;

public class Tab2 extends Fragment {
    private OnFragmentInteractionListener mListener;

    private DrawingView drawView;
    private String gestureName = "Next Song";
    private boolean isRecording = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_tab2, container, false);

        drawView = rootView.findViewById(R.id.drawing);

        ImageView recordButtonImageView = rootView.findViewById(R.id.recordButtonImageView);
        Button resetButton = rootView.findViewById(R.id.resetButton);
        ImageView confirmButtonImageView = rootView.findViewById(R.id.confirmButtonImageView);

        recordButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GesturesActivity.customPager.setPagingEnabled(false);
                isRecording = true;

            }
        });
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GesturesActivity.customPager.setPagingEnabled(true);
                isRecording = false;
                drawView.clear();
            }
        });
        confirmButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GesturesActivity.customPager.setPagingEnabled(true);
                isRecording = false;
                drawView.addGesture(gestureName);
                drawView.clear();
            }
        });

        return rootView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
        else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
