package com.example.contentprovider02;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Xml;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private TextView mTvContext;
    private TextView mTvPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvContext = ((TextView) findViewById(R.id.et_context));
        mTvPhone = ((TextView) findViewById(R.id.et_phone));

    }

    public void insertSms(View view){
        //通过内容解析者，插入数据到短信数据库中
        ContentResolver resolver = getContentResolver();
        Uri uri = Uri.parse("content://sms");
        String phoneNumber = mTvPhone.getText().toString().trim();
        String context = mTvPhone.getText().toString().trim();
            long l = System.currentTimeMillis();
            ContentValues values = new ContentValues();
            values.put("address",context);
            values.put("date",l);
            values.put("body",phoneNumber);
        //从这里插入你添加的短信数据
            resolver.insert(uri,values);


    }

    public void restoreSms(View view){
        //解析xml
    }

}
