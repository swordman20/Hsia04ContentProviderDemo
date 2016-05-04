package com.example.contentprovider03;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：Hsia on 2016/5/4.
 * 邮箱：xiaweifeng@live.cn
 * 文件描述：读取联系人信息的工具类
 *
 * 读取联系人数据库的步骤
 *  1、读取contacts2.db数据库
 *  2、先查询raw_contacts表中的contact_id列，它保存了联系人的数目
 *  3、根据contact_id，再去查data表中的data1（保存的是联系人基本信息）列和mimetype_id（保存的是联系人详细信息）列。
 *  4、获取到mimetyp_id后，和mimetypes表中的mimetype做比较，就可以得出联系人的详细信息了（包括phone/name//nickname/address&）
 *
 */
public class ReadContactUtils {
    public List<ContactsBean> readContacts(Context context){
        //自定义一个javabean，用于存储短信格式
        List<ContactsBean> contactsBeanList = new ArrayList<>();
        //(1)去raw_contacts表查询  contact_id列,contact_id记录了联系人的数目
        Uri contactsUri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri dataUri = Uri.parse("content://com.android.contacts/data");

        Cursor contactsCursor = context.getContentResolver().query(contactsUri, new String[]{"contact_id"}, null, null, null);
        if (contactsCursor!= null&&contactsCursor.getCount()>0) {
            while(contactsCursor.moveToNext()){
                String  contactsID = contactsCursor.getString(0);
                //判断contactsID不为null说明联系人存在
                if (contactsID != null) {
                    ContactsBean contactsBean = new ContactsBean();
                    contactsBean.setId(contactsID);
                    //(2)根据 contact_id 去查 data表    查询data1列 和 mimetype_id 列
                    Cursor dataCursor = context.getContentResolver().query(dataUri, new String[]{"data1","mimetype"}, "raw_contact_id=?", new String[]{contactsID}, null);
                    if (dataCursor != null&&dataCursor.getCount()>0) {
                        while (dataCursor.moveToNext()){
                            if (dataCursor!=null){
                                String data1 = dataCursor.getString(0);//获取data1列的数据，就是联系人的详情数据
                                String mimetype = dataCursor.getString(1);//获取mimetype列数据

                                //(3)拿mimetype 数据做一下对比，然后取出联系人号码或者其他信息

                                if("vnd.android.cursor.item/name".equals(mimetype)){
                                    System.out.println("姓名data1:"+data1);
                                    contactsBean.setName(data1);
                                }else if("vnd.android.cursor.item/phone_v2".equals(mimetype)){
                                    System.out.println("电话号码data1:"+data1);
                                    contactsBean.setPhone(data1);
                                }

                            }
                        }
                    }
                contactsBeanList.add(contactsBean);
                }
            }
//                System.out.println("一共有"+contactsID+"个联系人");
        }


        return contactsBeanList;
    }
}
