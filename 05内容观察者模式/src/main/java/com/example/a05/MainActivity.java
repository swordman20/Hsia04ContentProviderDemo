package com.example.a05;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //内容观察模式
        Uri uri = Uri.parse("content://sms");
        getContentResolver().registerContentObserver(uri,true,new MyContentObserver(new Handler()));
    }
    class MyContentObserver extends ContentObserver{

        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            System.out.println("当短信发生变化时调用。");
            super.onChange(selfChange);
        }
    }
}
