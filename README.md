#04Android学习从零单排之ContentProvider
**读了那么多年的书让我明白一个道理。人要稳重，不要想到啥就做啥。做一行越久即使你不会，几年之后慢慢的你也会了，加上一点努力你或许你能成为别人眼中的专家。**

##ContentProvider简介
	ContentProvider 在android中的作用是对外共享数据，也就是说你可以通过ContentProvider把应用中的数据共享给其他应用访问，其他应用可以通过ContentProvider 对你应用中的数据进行添删改查。

##ContentProvider实际应用场景
	ContentProvider实际应用场景，主要是通过getContentResolver来获取安卓系统中已经通过ContentProvider共享好的数据。例如，读取短信数据、获取联系人信息，它们都是通过ContentProvider把数据库对外提供好的，一般我们很少会ContentProvider把自己的应用程序数据暴露出去。

##ContentProvider共享数据的使用步骤
1、定义一个类继承ContentProvider，并在清单文件中配置provider和android:authorities节点。
2、定义路径匹配规则
3、添加路径匹配规则，以供其他应用程序访问。
4、判断匹配规则，和定义的匹配规则是否相同。相同数据即可不其他应用操作。

```
public class AccountProvider extends ContentProvider {

	private static final int QUERYSUCESS = 1;  //ctrl+shift+x 变成大些  ctrl+shift小写加Y
	private static final int ADDSUCESS = 2;
	private static final int DELSUCESS = 3;
	private static final int UPDATESUCESS = 4;

	
	//(1) 定义路径匹配规则  
	static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
	private MyOpenHelper helper;
	//(2) 添加路径匹配规则  
	static{
		/**
		 * authority  主机名 自己定义 
		 * path 路径  
		 * 
		 */
		matcher.addURI("com.itheima.account.provider", "query", QUERYSUCESS);  //添加 query方法的匹配规则 
		matcher.addURI("com.itheima.account.provider", "add", ADDSUCESS);  //添加 add方法的匹配规则 
		matcher.addURI("com.itheima.account.provider", "delete", DELSUCESS);  //添加 add方法的匹配规则 
		matcher.addURI("com.itheima.account.provider", "update", UPDATESUCESS);  //添加 add方法的匹配规则 
		
	}
	
	
	@Override
	public boolean onCreate() {
		
		helper = new MyOpenHelper(getContext());
		
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		//(3)获取匹配code 
		int code = matcher.match(uri);
		if (code == QUERYSUCESS) {
			//说明匹配成功  

			SQLiteDatabase db = helper.getReadableDatabase();
			Cursor cursor = db.query("info", projection, selection, selectionArgs, null, null, sortOrder);
			return cursor;
		}else {
			
			throw new IllegalArgumentException("路径不匹配 请检查");
		}
		
		
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
	   int code = matcher.match(uri);
	   if (code == ADDSUCESS ) {
		   //匹配成功 
			SQLiteDatabase db = helper.getReadableDatabase();
			//代表插入到了多少行 
			long insert = db.insert("info", null, values);
		    return Uri.parse("com.itheima.account.provider"+insert); 
			 
	}else {
		
		throw new IllegalArgumentException("路径不匹配 请检查");
	}
	        
	        
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int code = matcher.match(uri);
		if (code == DELSUCESS) {
			SQLiteDatabase db = helper.getReadableDatabase();
			int delete = db.delete("info", selection, selectionArgs);
			//删除了多少行
		
			return delete;
		}else {
			throw new IllegalArgumentException("路径不匹配 请检查");
			
		}
		
		
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		
		int code = matcher.match(uri);
		if (code == UPDATESUCESS) {
			SQLiteDatabase db = helper.getReadableDatabase();
			int update = db.update("info", values, selection, selectionArgs);
			//删除了多少行
		
			return update;
		}else {
			throw new IllegalArgumentException("路径不匹配 请检查");
			
		}
		
		
	}

}
```
	清单文件中配置provider

```
<provider
	android:name="com.itheima.transation.AccountProvider"
    android:authorities="com.itheima.account.provider" ></provider>
```
以上内容在实际开发中一般不常见。下面通过案例的方式，学习并使用ContentProvider共享数据的共享的数据。
___________________

##ContentProvider之短信备份案例
1、通过内容解析者getContentResolver()查询短信数据库。
2、把查询出来的结果通过xml文件保存起来。

```
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

 //不要忘记添加短信权限

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
```

##ContentProvider之短信插入案例
```
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
    //不要忘记添加短信权限
```
##ContentProvider之读取联系人案例
由于读取联系人是实际开发中比较常用的功能，因此在这里我把读取联系人抽取成了工具类，方便以后再开发中，直接哪来用。
这个读取联系人的工具类，只是最简单的实现功能，代码还有很多需要完善的地方，在此不做修改了，等用到的时候在优化。
```
public class ReadContactUtils {
    public List<ContactsBean> readContacts(Context context){
        //自定义一个javabean，用于存储短信格式，javabean在此就不贴出来了，根据实际需求自己定义。
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
```
##ContentProvider之插入联系人案例
```
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
```
**关于作者**
	- Email：[xiaweifeng@live.cn](https://login.live.com)
	- 项目地址:[https://github.com/swordman20/Hsia04ContentProviderDemo.git](https://github.com/swordman20/Hsia04ContentProviderDemo.git)