package com.example.administrator.hsia04contentproviderdemo;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Xml;
import android.view.View;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 通过内容提供者，读取短信，并备份短信
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    public void backsms(View view){
        //写入到xml文件中
        XmlSerializer serializer = Xml.newSerializer();
        File file = new File(Environment.getExternalStorageDirectory().getPath()+"/Download", "smsback.xml");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            serializer.setOutput(fos,"utf-8");
            //文档开始
            serializer.startDocument("utf-8",true);
            //开始节点
            serializer.startTag(null,"smss");
            //循环写 xml sms  body address date
            //1、利用内容解析者 获取我们关心列
            //content://sms 是短信的匹配规则
            Uri uri = Uri.parse("content://sms");
            Cursor cursor = getContentResolver().query(uri, new String[]{"address", "date", "body"}, null, null, null);
            if (cursor != null&&cursor.getCount()>0) {
                while(cursor.moveToNext()){
                    String address = cursor.getString(0);
                    String date = cursor.getString(1);
                    String body = cursor.getString(2);
                    String datef = formatDate(date);
                    //开始写 xml 的  sms节点
                    serializer.startTag(null,"sms");

                    serializer.startTag(null,"address");
                    //如果不做这个判断，读取到是草稿短信，就会报空指针异常。
                    if (TextUtils.isEmpty(address)){
                        serializer.text("草稿箱");
                    }else{
                        serializer.text(address);
                    }
                    serializer.endTag(null,"address");

                    serializer.startTag(null,"date");
                    serializer.text(datef);
                    serializer.endTag(null,"date");

                    serializer.startTag(null,"body");
                    serializer.text(body);
                    serializer.endTag(null,"body");

                    serializer.endTag(null,"sms");
                }
            }

            serializer.endTag(null,"smss");
            serializer.endDocument();
            //关闭数据库查询
            cursor.close();
            fos.close();//释放资源

            Toast.makeText(getApplicationContext(),"短信备份完成。",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"短信备份失败。",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }



    }

    /**
     * 时间格式转换
     * @param date
     * @return
     */
    private String formatDate(String date){
        long l = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        String format1 = format.format(new Date(new Long(date)));
        return format1;
    }
}
