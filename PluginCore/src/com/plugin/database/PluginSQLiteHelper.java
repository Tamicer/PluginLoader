package com.plugin.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.plugin.util.PaCursorUtils;
import com.plugin.util.PaLog;

/**
 * Plugin sqlite helper
 * Created by LIUYONGKUI on 2015-12-02.
 */
public class PluginSQLiteHelper extends SQLiteOpenHelper {

	/** table name */
	private static final String TABLE_NAME = "pahf_plugintable";
	/** suffix */
	private static final String SUFFIX = "=?";

	/**
	 * constructor
	 *
	 * @param aContext context
	 */
	public PluginSQLiteHelper(Context aContext) {
		super(aContext, PluginDatabaseManager.DB_NAME, null, PluginDatabaseManager.DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        PaLog.d("PluginDB", "oldversion: " + oldVersion + " new version: " + newVersion);

	}

	/**
	 * 增
	 * 
	 * @param db db
	 * @param values values
	 * @return result
	 */
	public static long insert(SQLiteDatabase db, ContentValues values) {
		if (db == null) {
			PaLog.e("PluginDB is null");
			return -1;
		}
		try {
			long ret = db.replace(TABLE_NAME, null, values);
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * 删
	 * 
	 * @param db db
	 * @param selectionArgs args
	 * @return result
	 */
	public static int delete(SQLiteDatabase db, String[] selectionArgs) {
        try {
            return db.delete(TABLE_NAME, PluginDatabaseManager.Columns.PLUGIN_ID + SUFFIX, selectionArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
	}

	/**
	 * 删全部
	 *
	 * @param db db
	 * @return result
	 */
	public static int deleteAll(SQLiteDatabase db) {
		try {
			return db.delete(TABLE_NAME, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 改
	 * 
	 * @param db db
	 * @param values values
	 * @param selectionArgs args
	 * @return result
	 */
	public static long update(SQLiteDatabase db, ContentValues values, String[] selectionArgs) {
        try {
            return db.replace(TABLE_NAME, null,values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
	}

	/**
	 * 查
	 * 
	 * @param db db
	 * @param columns colums
	 * @param selectionArgs args
	 * @param groupBy groupby
	 * @param having having
	 * @param orderBy orderby
	 * @return cursor
	 */
	public static Cursor query(SQLiteDatabase db, String[] columns, String[] selectionArgs, String groupBy,
			String having, String orderBy) {
		PaCursor cursor = null;
		try {
			cursor = PaCursorUtils.getCursor(db.query(TABLE_NAME, columns, null,
					selectionArgs, groupBy, having, orderBy));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cursor;
	}

	/**
	 * create table
	 * 
	 * @param db db
	 */
	private void createTable(SQLiteDatabase db) {
		try {
			String cmd = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( "
					+ PluginDatabaseManager.Columns.PLUGIN_ID + " TEXT PRIMARY KEY NOT NULL,"
					+ PluginDatabaseManager.Columns.URL + " TEXT NOT NULL,"
					+ PluginDatabaseManager.Columns.PACKAGE_NAME + " TEXT NOT NULL,"
					+ PluginDatabaseManager.Columns.PLUGIN_NAME + " TEXT DEFAULT '',"
					+ PluginDatabaseManager.Columns.INSTALL_PATH + " TEXT NOT NULL,"
					+ PluginDatabaseManager.Columns.VERSION + " TEXT DEFAULT '',"
					+ PluginDatabaseManager.Columns.TYPE + " INTEGER DEFAULT 0,"
					+ PluginDatabaseManager.Columns.STATUS + " INTEGER DEFAULT 1, "
					+ PluginDatabaseManager.Columns.APK_MAINACTIVITY + " TEXT DEFAULT '',"
					+ PluginDatabaseManager.Columns.DESCRIPTION + " TEXT DEFAULT '',"
					+ PluginDatabaseManager.Columns.APP_ICON + " BLOB,"
					+ PluginDatabaseManager.Columns.IS_STANDALONE + " INTEGER DEFAULT 0,"
					+ PluginDatabaseManager.Columns.IS_ENABLED + " INTEGER DEFAULT 0,"
					+ PluginDatabaseManager.Columns.APPLICATION_NAME + " TEXT DEFAULT '',"
					+ PluginDatabaseManager.Columns.APPLICATION_LOGO + " INTEGER DEFAULT 0,"
					+ PluginDatabaseManager.Columns.APPLICATION_ICON + " INTEGER DEFAULT 0,"
					+ PluginDatabaseManager.Columns.APPLICATION_THEME + " INTEGER DEFAULT 0,"
                    + PluginDatabaseManager.Columns.ACTIVITY_INFOS + " TEXT DEFAULT '',"
					+ PluginDatabaseManager.Columns.ACTIVITYS + " TEXT DEFAULT '',"
					+ PluginDatabaseManager.Columns.SERVICES + " TEXT DEFAULT '',"
					+ PluginDatabaseManager.Columns.PROVIDER_INFOS + " TEXT DEFAULT '',"
					+ PluginDatabaseManager.Columns.FRAGMENTS + " TEXT DEFAULT '',"
					+ PluginDatabaseManager.Columns.METADATA + " TEXT DEFAULT '',"
					+ PluginDatabaseManager.Columns.RECEIVERS + " TEXT DEFAULT ''"
                    + " ) ";
			db.execSQL(cmd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
