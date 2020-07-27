package com.example.importcontacts;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
    }

    public void createContact(View view)
    {
        Intent intent = new Intent(this, ContactEdit.class);
        startActivity(intent);
    }

    public void selectContact(View view)
    {
        Intent intent = new Intent(this, ContactSelect.class);
        startActivity(intent);
    }

    public void importContact(View view)
    {
        Intent intent = new Intent(this, ContactImport.class);
        startActivity(intent);
    }
}
