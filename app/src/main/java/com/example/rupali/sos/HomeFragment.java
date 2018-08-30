package com.example.rupali.sos;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class HomeFragment extends Fragment {

    TextView searchdir,inputdir,searchappeal,inputappeal;
    ImageButton searchdirbtn,inputdirbtn,searchappealbtn,inputappealbtn;

    private android.support.v7.widget.Toolbar page_name;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    public void onViewCreated(final View view,@Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        page_name = (Toolbar) getActivity().findViewById(R.id.page_name);
        ((AppCompatActivity) getActivity()).setSupportActionBar(page_name);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        searchdir = view.findViewById(R.id.searchDirectorylabel);
        inputdir = view.findViewById(R.id.inputDirectorylabel);
        searchappeal = view.findViewById(R.id.searchAppeallabel);
        inputappeal = view.findViewById(R.id.raiseOnAppeallabel);

        searchdirbtn = view.findViewById(R.id.searchDirectory);
        inputdirbtn = view.findViewById(R.id.inputDirectory);
        searchappealbtn = view.findViewById(R.id.searchAppeal);
        inputappealbtn = view.findViewById(R.id.raiseOnAppeal);

        searchdirbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(),SearchDirectory.class);
                startActivity(intent);
            }
        });

        inputdirbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(),InputDirectory.class);
                startActivity(intent);
            }
        });

        searchappealbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(),SearchAppeal.class);
                startActivity(intent);
            }
        });

        inputappealbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(),RaiseOnAppeal.class);
                startActivity(intent);
            }
        });

    }
}
