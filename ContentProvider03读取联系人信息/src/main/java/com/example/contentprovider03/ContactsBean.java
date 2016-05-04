package com.example.contentprovider03;

/**
 * 作者：Hsia on 2016/5/4.
 * 邮箱：xiaweifeng@live.cn
 * 文件描述：存储联系人信息的bean
 */
public class ContactsBean {
    public String id;
    public String name;
    public String phone;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "ContactsBean{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
