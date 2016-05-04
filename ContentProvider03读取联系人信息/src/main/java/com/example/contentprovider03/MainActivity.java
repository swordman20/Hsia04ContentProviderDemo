package com.example.contentprovider03;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ReadContactUtils readContactUtils = new ReadContactUtils();
        List<ContactsBean> contactsBeanList = readContactUtils.readContacts(getApplicationContext());
        for (ContactsBean  contactsBean: contactsBeanList) {
            System.out.println("取出联系人信息"+contactsBean.toString());
        }
    }
}
