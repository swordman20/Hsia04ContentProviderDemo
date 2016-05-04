package com.example.contentprovider04;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText mTvName;
    private EditText mTvNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvName = (EditText) findViewById(R.id.tv_name);
        mTvNumber = (EditText) findViewById(R.id.tv_number);
    }
    public void addContact(View view){
        String name = mTvName.getText().toString().trim();
        String number = mTvNumber.getText().toString().trim();

        //添加联系人之前先查询联系的contact_id
        Uri contactsUri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri dataUri = Uri.parse("content://com.android.contacts/data");
        Cursor cursor = getContentResolver().query(contactsUri, null, null, null, null);
        int contact_id = cursor.getCount();
//        System.out.println("contact_id"+contact_id);
        //2现在开始插入联系人id
        ContentValues Values = new ContentValues();
        //3为什么加1，是在原来的基础上+1
        int newContact_id = contact_id+1;
        Values.put("contact_id",newContact_id);
        getContentResolver().insert(contactsUri,Values);
        //４开始插入联系人姓名
        ContentValues nameValues = new ContentValues();
        nameValues.put("raw_contact_id",newContact_id);
        nameValues.put("mimetype","vnd.android.cursor.item/name");
        nameValues.put("data1",name);
        getContentResolver().insert(dataUri,nameValues);
        //5插入联系人手机号码
        ContentValues numberValues = new ContentValues();
        numberValues.put("raw_contact_id", newContact_id);
        numberValues.put("mimetype", "vnd.android.cursor.item/phone_v2");
        numberValues.put("data1",number);
        getContentResolver().insert(dataUri,numberValues);

        Toast.makeText(getApplicationContext(),"联系人插入成功",Toast.LENGTH_SHORT).show();
    }
}
