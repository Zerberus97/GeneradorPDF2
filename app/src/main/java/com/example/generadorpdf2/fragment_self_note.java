package com.example.generadorpdf2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.generadorpdf2.ui.fragmentselfnote.SelfNoteFragment;

import java.io.FileNotFoundException;

public class fragment_self_note extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_self_note_activity);
        /*
        if (savedInstanceState == null) {
            try {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, SelfNoteFragment.newInstance())
                        .commitNow();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        */
    }
}
