package com.example.ahmed.p2_popularmoviesstage2.app.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.ahmed.p2_popularmoviesstage2.app.R;


public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.Movie_detail,
                    getIntent().getParcelableExtra(DetailFragment.Movie_detail));

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail, fragment)
                    .commit();
        }
    }
}
