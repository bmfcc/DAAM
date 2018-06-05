package com.iscte.zoozone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class NewsView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_view);

        Intent intent = getIntent();

        String title = intent.getStringExtra("title");
        String desc = intent.getStringExtra("desc");

        ((TextView)findViewById(R.id.title_view)).setText(title);
        TextView descview = (TextView)findViewById(R.id.desc_view);

        descview.setText(desc);
        descview.setMovementMethod(new ScrollingMovementMethod());


    }

}
