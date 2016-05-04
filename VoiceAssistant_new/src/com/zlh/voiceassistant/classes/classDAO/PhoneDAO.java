package com.zlh.voiceassistant.classes.classDAO;

import java.util.ArrayList;
import java.util.List;

import com.zlh.voiceassistant.classes.Phone;
import com.zlh.voiceassistant.tool.WordTool;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.Data;

public class PhoneDAO {
	private Context context;
	public PhoneDAO(Context context){
		this.context=context;
	}
	public List<Phone> findAll() {
		Uri uri = Uri.parse("content://com.android.contacts/contacts");
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(uri, new String[] { Data._ID }, null,
				null, null);
		List<Phone> phoneList=new ArrayList<Phone>();
		while (cursor.moveToNext()) {
			Phone phone=new Phone();
			int id = cursor.getInt(0);
			phone.setId(id);
			uri = Uri.parse("content://com.android.contacts/contacts/" + id
					+ "/data");
			Cursor cursor2 = resolver.query(uri, new String[] { Data.DATA1,
					Data.MIMETYPE }, null, null, null);
			while (cursor2.moveToNext()) {
				String data = cursor2
						.getString(cursor2.getColumnIndex("data1"));
				if (cursor2.getString(cursor2.getColumnIndex("mimetype"))
						.equals("vnd.android.cursor.item/name")) {
					phone.setName(data);
				} else if (cursor2
						.getString(cursor2.getColumnIndex("mimetype")).equals(
								"vnd.android.cursor.item/phone_v2")) {
					phone.setPhoneNum(data);
				} else if (cursor2
						.getString(cursor2.getColumnIndex("mimetype")).equals(
								"vnd.android.cursor.item/email_v2")) {
					phone.setEmail(data);
				} else if (cursor2
						.getString(cursor2.getColumnIndex("mimetype")).equals(
								"vnd.android.cursor.item/postal-address_v2")) {
					phone.setAddress(data);
				} else if (cursor2
						.getString(cursor2.getColumnIndex("mimetype")).equals(
								"vnd.android.cursor.item/organization")) {
					phone.setOrganization(data);
				}
			}
			cursor2.close();
			phoneList.add(phone);
		}
		cursor.close();
		return phoneList;
	}
	
	public List<Phone> findByNameLike(String name){
		name=WordTool.getPinYin(name);
		List<Phone> phoneList=new ArrayList<Phone>();
		for(Phone phone:findAll()){
			if(name.equals(WordTool.getPinYin(phone.getName())))
				phoneList.add(phone);
		}
		return phoneList;
	}
}
