package com.odoo.orm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.odoo.util.ODate;

public class OContentResolver {
	private OModel mModel = null;
	private Context mContext;

	public OContentResolver(OModel model, Context context) {
		mModel = model;
		mContext = context;
	}

	public void delete(int id) {
		mContext.getContentResolver().delete(mModel.uri(),
				OColumn.ROW_ID + " = ? ", new String[] { id + "" });
	}

	public int insert(OValues values) {
		ContentValues vals = values.toContentValues();
		if (!vals.containsKey("id"))
			vals.put("id", "0");
		if (!vals.containsKey("odoo_name"))
			vals.put("odoo_name", mModel.getUser().getAndroidName());
		vals.put("is_active", "true");
		vals.put("is_dirty", "false");
		vals.put("local_write_date", ODate.getUTCDate(ODate.DEFAULT_FORMAT));
		Uri uri = mContext.getContentResolver().insert(mModel.uri(), vals);
		return Integer.parseInt(uri.getLastPathSegment());
	}

	public void update(Integer id, OValues values) {
		ContentValues vals = values.toContentValues();
		if (!vals.containsKey("odoo_name"))
			vals.put("odoo_name", mModel.getUser().getAndroidName());
		if (!values.contains("is_dirty"))
			vals.put("is_dirty", "true");
		vals.put("local_write_date", ODate.getUTCDate(ODate.DEFAULT_FORMAT));
		Uri uri = mModel.uri().buildUpon().appendPath(id + "").build();
		mContext.getContentResolver().update(uri, vals, null, null);

	}

	public Cursor query(int id) {
		return query(OColumn.ROW_ID + " = ? ", new String[] { id + "" }, null);
	}

	public Cursor query(String selection, String[] args, String sort) {
		Uri uri = mModel.uri();
		return mContext.getContentResolver().query(uri, mModel.projection(),
				selection, args, sort);
	}
}
