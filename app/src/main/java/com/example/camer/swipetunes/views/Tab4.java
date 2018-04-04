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

public class Tab4 extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private DrawingView drawView;
    private String gestureName = "Save Song";
    private boolean isRecording = false;

    public Tab4() {

    }

    public static Tab4 newInstance(String param1, String param2) {
        Tab4 fragment = new Tab4();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_tab4, container, false);

        drawView = rootView.findViewById(R.id.drawing);

        ImageView recordButtonImageView = rootView.findViewById(R.id.recordButtonImageView);
        Button resetButton = rootView.findViewById(R.id.resetButton);
        ImageView confirmButtonImageView = rootView.findViewById(R.id.confirmButtonImageView);

        recordButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRecording = true;

            }
        });
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRecording = false;
                drawView.clear();
            }
        });
        confirmButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRecording = false;
                drawView.addGesture(gestureName);
                drawView.clear();
            }
        });

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
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
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
