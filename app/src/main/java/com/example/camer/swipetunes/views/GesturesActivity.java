package com.example.camer.swipetunes.views;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.camer.swipetunes.R;

public class GesturesActivity extends AppCompatActivity implements
        Tab1.OnFragmentInteractionListener,
        Tab2.OnFragmentInteractionListener,
        Tab3.OnFragmentInteractionListener,
        Tab4.OnFragmentInteractionListener,
        Tab5.OnFragmentInteractionListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestures);

        TabLayout tabLayout = findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("Play/Pause"));
        tabLayout.addTab(tabLayout.newTab().setText("Next Song"));
        tabLayout.addTab(tabLayout.newTab().setText("Previous Song"));
        tabLayout.addTab(tabLayout.newTab().setText("Save Song"));
        tabLayout.addTab(tabLayout.newTab().setText("Remove Song"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.myViewPager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}