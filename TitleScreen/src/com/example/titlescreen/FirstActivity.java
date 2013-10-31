package com.example.titlescreen;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.TextView;

public class FirstActivity extends Fragment {

    private static View mView;

    public static final FirstActivity newInstance(String sampleText) {
        FirstActivity f = new FirstActivity();

        Bundle b = new Bundle();
        b.putString("bString", sampleText);
        f.setArguments(b);

        
    return f;
    }
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.first_layout, container, false);
        //String sampleText = getArguments().getString("bString");
        
        FragmentTabHost mTabHost;
        
        mTabHost = (FragmentTabHost)mView.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);
        
        return mView;
        

    }
}