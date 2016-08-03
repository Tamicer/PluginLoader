package com.plugin.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.plugin.content.PluginActivityInfo;
import com.plugin.content.PluginDescriptor;
import com.plugin.content.PluginIntentFilter;
import com.plugin.content.PluginProviderInfo;
import com.plugin.core.PluginLoader;
import com.plugin.util.BitmapUtil;
import com.plugin.util.JsonUtil;
import com.plugin.util.PaCursorUtils;
import com.plugin.util.PaLog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/**
 * Plugin database manager
 * Created by LIUYONGKUI on 2015-12-02.
 */
public final class PluginDatabaseManager {
	/** db name */
	public static final String DB_NAME = "pahf_plugins.db";
	/** db version for pligun 1.0*/
	public static final int DB_VERSION = 1;
	/** instance */
	private static  PluginDatabaseManager sInstance;
	/** context */
	private Context mContext;
	/** mDB */
	private SQLiteDatabase mDB;



	PluginSQLiteHelper helper;

	/**
	 * constructor
	 * 
	 * @param aContext context
	 */
	private PluginDatabaseManager(Context aContext) {
		if (aContext != null) {
			mContext = aContext;
		}
		helper = new PluginSQLiteHelper(mContext);
	}

	/**
	 * 获取单例
	 *
	 * @param aContext context
	 * @return 单例
	 */
	public static synchronized PluginDatabaseManager getInstance(Context aContext) {
		if (sInstance == null) {
			synchronized (PluginDBController.class) {
				if (sInstance == null) {
					sInstance = new PluginDatabaseManager(aContext);
				}
			}
		}
		return sInstance;
	}

	/**
	 * init db
	 */
	public synchronized void init() {

		 getDB();
	}

    /**
     * get db
     * 
     * @return db
     */
    private synchronized SQLiteDatabase getDB() {


        if (mDB == null) {
            try {
				if (helper == null) {
					PaLog.e("Plugin helper is null" );
					helper = new PluginSQLiteHelper(mContext);
				}
				//PluginSQLiteHelper helper = new PluginSQLiteHelper(mContext);
                mDB = helper.getWritableDatabase();
            } catch (Exception e) {
                e.printStackTrace();
                mDB = null;
            }
        }
        return mDB;
    }


	/**
	 * @param aId
	 * @param aUrl
	 * @param aPackageName
	 * @param aPluginName
	 * @param aVersion
	 * @param isStandalone
	 * @param aType
	 * @param isEnabled
	 * @param aInstalledPath
	 * @param aApplicationName
	 * @param aApplicationIcon
	 * @param aApplicationLogo
	 * @param aApplicationTheme
	 * @param aApkMainActivity
	 * @param aActivityInfos
	 * @param aProviderInfos
	 * @param aActivitys
	 * @param aFragments
	 * @param aServices
	 * @param aReceivers
	 * @param aMaetaData
	 * @return
	 */
	public long insert(String aId, String aUrl, String aPackageName, String aPluginName, String aVersion, int isStandalone, int aType,
			int isEnabled, String aInstalledPath, String aApplicationName, int aApplicationIcon, int aApplicationLogo,
					   int aApplicationTheme, String aApkMainActivity, String aActivityInfos, String aProviderInfos,
					   String aActivitys, String aFragments, String aServices, String aReceivers, String aMaetaData) {
		ContentValues values = new ContentValues();
		values.put(Columns.PLUGIN_ID, aId);
		values.put(PluginDatabaseManager.Columns.URL, aUrl);
		values.put(Columns.PACKAGE_NAME, aPackageName);
		values.put(Columns.PLUGIN_NAME, aPluginName);
		values.put(Columns.IS_STANDALONE, isStandalone);
		values.put(PluginDatabaseManager.Columns.TYPE, aType);
		values.put(Columns.IS_ENABLED, isEnabled);
		values.put(Columns.INSTALL_PATH, aInstalledPath);
		values.put(Columns.APPLICATION_NAME, aApplicationName);
		values.put(Columns.APPLICATION_ICON, aApplicationIcon);
		values.put(Columns.APPLICATION_LOGO, aApplicationLogo);
		values.put(Columns.APPLICATION_THEME, aApplicationTheme);
		values.put(Columns.APK_MAINACTIVITY, aApkMainActivity);
		values.put(Columns.ACTIVITY_INFOS, aActivityInfos);
		values.put(Columns.PROVIDER_INFOS, aProviderInfos);
		values.put(Columns.ACTIVITYS, aActivitys);
		values.put(Columns.FRAGMENTS, aFragments);
		values.put(Columns.SERVICES, aServices);
		values.put(Columns.RECEIVERS, aReceivers);
		values.put(Columns.METADATA, aMaetaData);

		return PluginSQLiteHelper.insert(getDB(), values);
	}

	/**
	 * insert or upate
	 * @param aDescriptor
	 * @return
	 */
    public long insert(PluginDescriptor aDescriptor) {
        ContentValues values = new ContentValues();
		values.put(Columns.PLUGIN_ID, aDescriptor.getPackageName());
		values.put(PluginDatabaseManager.Columns.URL, aDescriptor.getUrl());
		values.put(Columns.PACKAGE_NAME, aDescriptor.getPackageName());
		values.put(Columns.PLUGIN_NAME, aDescriptor.getPluginName());
		values.put(Columns.IS_STANDALONE, aDescriptor.isStandalone() ? 0 : 1);
		values.put(PluginDatabaseManager.Columns.TYPE, aDescriptor.getPluginType());
		values.put(Columns.IS_ENABLED, aDescriptor.isEnabled() ? 0 : 1);
		values.put(Columns.INSTALL_PATH, aDescriptor.getInstalledPath());
		values.put(Columns.APPLICATION_NAME, aDescriptor.getApplicationName());
		values.put(Columns.APPLICATION_ICON, aDescriptor.getApplicationIcon());
		values.put(Columns.APPLICATION_LOGO, aDescriptor.getApplicationLogo());
		values.put(Columns.APPLICATION_THEME, aDescriptor.getApplicationTheme());
		values.put(Columns.APK_MAINACTIVITY, aDescriptor.getApkMainActivity());
		values.put(Columns.DESCRIPTION, aDescriptor.getDescription());
		values.put(Columns.TYPE, aDescriptor.getPluginType());
		values.put(Columns.STATUS, aDescriptor.getStatus());
		if (aDescriptor.getAppIcon() != null) {
			values.put(Columns.APP_ICON, aDescriptor.getAppIcon());
		}
		values.put(Columns.ACTIVITY_INFOS,
				JsonUtil.toJSONString(aDescriptor.getActivityInfos()));
		values.put(Columns.PROVIDER_INFOS,
				JsonUtil.toJSONString(aDescriptor.getProviderInfos()));
		values.put(Columns.ACTIVITYS,
				JsonUtil.toJSONString(aDescriptor.getActivitys()));
		values.put(Columns.FRAGMENTS,
				JsonUtil.toJSONString(aDescriptor.getFragments()));
		values.put(Columns.SERVICES,
				JsonUtil.toJSONString(aDescriptor.getServices()));
		values.put(Columns.RECEIVERS,
				JsonUtil.toJSONString(aDescriptor.getReceivers()));
		values.put(Columns.METADATA,
				JsonUtil.toJSONString(aDescriptor.getMetaData()));
        return PluginSQLiteHelper.insert(getDB(), values);
    }

	/**
	 * delete
	 * 
	 * @param selectionArgs args
	 * @return result code
	 */
	public int delete(String[] selectionArgs) {
		return PluginSQLiteHelper.delete(helper.getWritableDatabase(), selectionArgs);
	}

	/**
	 * delete
	 * @return result code
	 */
	public int deleteAll() {
		return PluginSQLiteHelper.deleteAll(helper.getWritableDatabase());
	}

	/**
	 * update
	 * 
	 * @param values values
	 * @param selectionArgs args
	 * @return result code
	 */
	public long update(ContentValues values, String[] selectionArgs) {
		return PluginSQLiteHelper.update(getDB(), values, selectionArgs);
	}

	/**
	 * 查询所有
	 * 
	 * @return 查询结果list
	 */
	public Hashtable<String, PluginDescriptor> queryAll() {
		PaCursor cursor = null;
		try {
			String current_sql_sel = "SELECT * FROM " + "pahf_plugintable";
			//Cursor c = mDatabase.rawQuery(current_sql_sel, null);
			cursor = PaCursorUtils.getCursor(getDB().rawQuery(current_sql_sel, null));
			//cursor = PaCursorUtils.getCursor(PluginSQLiteHelper.query(getDB(), null, null, null, null, null));
			if (cursor != null) {
				return convertToPlugins(cursor);
			}
		} catch (Exception e) {
			PaLog.printStackTrace();
		} finally {
			if (cursor != null) {
				try {
					cursor.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				cursor = null;
			}
		}

		return null;
	}

	/**
	 * query one
	 * 
	 * @param aKey key
	 * @return count
	 */
	public int queryCount(String aKey) {
		PaCursor cursor = null;
		try {
			cursor = PaCursorUtils.getCursor(PluginSQLiteHelper.query(getDB(), null,
					new String[]{aKey}, null, null, null));
			if (cursor != null) {
				return cursor.getCount();
			}
		} catch (Exception e) {
			PaLog.printStackTrace();
		} finally {
			if (cursor != null) {
				try {
					cursor.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				cursor = null;
			}
		}
		return 0;
	}

	/**
	 * query one
	 *
	 * @param aKey key
	 * @return count
	 */
	public PluginDescriptor querybyId(String aKey) {
		PaCursor cursor = null;
		try {
			cursor = PaCursorUtils.getCursor(PluginSQLiteHelper.query(getDB(), null, new String[]{aKey}, null, null, null));
			if (cursor != null) {
				return convertToPlugin(cursor);
			}
		} catch (Exception e) {
			PaLog.printStackTrace();
		} finally {
			if (cursor != null) {
				try {
					cursor.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				cursor = null;
			}
		}

		return null;
	}

	/**
	 * convert to info list
	 * 
	 * @param cursor cursor
	 * @return infos
	 */
	private Hashtable<String, PluginDescriptor> convertToPlugins(Cursor cursor) {
		if (cursor == null || cursor.getCount() == 0) {
			return null;
		} else {
			Hashtable<String, PluginDescriptor> pluginDescriptors = new Hashtable<String, PluginDescriptor>();
			PluginDescriptor pluginDescriptor = null;
			while (cursor.moveToNext()) {

				String pluginId = cursor.getString(cursor.getColumnIndex(Columns.PLUGIN_ID));
				String url = cursor.getString(cursor.getColumnIndex(Columns.URL));
				String pluginName = cursor.getString(cursor.getColumnIndex(Columns.PLUGIN_NAME));
				String packageName = cursor.getString(cursor.getColumnIndex(Columns.PACKAGE_NAME));
				String path = cursor.getString(cursor.getColumnIndex(Columns.INSTALL_PATH));
				String version = cursor.getString(cursor.getColumnIndex(Columns.VERSION));
				String mianAt = cursor.getString(cursor.getColumnIndex(Columns.APK_MAINACTIVITY));
				int status = cursor.getInt(cursor.getColumnIndex(Columns.STATUS));
				int type = cursor.getInt(cursor.getColumnIndex(Columns.TYPE));
				String descripion = cursor.getString(cursor.getColumnIndex(Columns.DESCRIPTION));
				int isEnableValue = cursor.getInt(cursor.getColumnIndex(Columns.IS_ENABLED));
				int isStandaloneValue = cursor.getInt(cursor.getColumnIndex(Columns.IS_STANDALONE));
				byte[] appIconByte = cursor.getBlob(cursor.getColumnIndex(Columns.APP_ICON));
				String applicationName = cursor.getString(cursor.getColumnIndex(Columns.APPLICATION_NAME));
				int applicationLogo = cursor.getInt(cursor.getColumnIndex(Columns.APPLICATION_LOGO));
				int apiacationIcon = cursor.getInt(cursor.getColumnIndex(Columns.APPLICATION_ICON));
				int applicationTheme = cursor.getInt(cursor.getColumnIndex(Columns.APPLICATION_THEME));
				String activityInfos = cursor.getString(cursor.getColumnIndex(Columns.ACTIVITY_INFOS));
				String providerInfos = cursor.getString(cursor.getColumnIndex(Columns.PROVIDER_INFOS));
				String activitys = cursor.getString(cursor.getColumnIndex(Columns.ACTIVITYS));
				String fragments = cursor.getString(cursor.getColumnIndex(Columns.FRAGMENTS));
				String services = cursor.getString(cursor.getColumnIndex(Columns.SERVICES));
				String receivers = cursor.getString(cursor.getColumnIndex(Columns.RECEIVERS));
				String mata = cursor.getString(cursor.getColumnIndex(Columns.METADATA));
				String progressMap = cursor.getString(cursor.getColumnIndex(Columns.PROGRESS_MAP));

				// constucts pluginModel
				pluginDescriptor = new PluginDescriptor();
		        pluginDescriptor.setPluginID(pluginId);
				pluginDescriptor.setPackageName(packageName);
				pluginDescriptor.setPluginName(pluginName);
				pluginDescriptor.setApkMainActivity(mianAt);
				pluginDescriptor.setVersion(version);
				pluginDescriptor.setPluginType(type);
				pluginDescriptor.setUrl(url);
				pluginDescriptor.setInstalledPath(path);
				pluginDescriptor.setStatus(status);
				pluginDescriptor.setApkMainActivity(mianAt);
				pluginDescriptor.setDescription(descripion);
				pluginDescriptor.setApplicationIcon(apiacationIcon);
				pluginDescriptor.setApplicationName(applicationName);
				pluginDescriptor.setApplicationTheme(applicationTheme);
				pluginDescriptor.setApplicationLogo(applicationLogo);
				pluginDescriptor.setEnabled(isEnableValue == 0);
				pluginDescriptor.setStandalone(isStandaloneValue == 0);
				pluginDescriptor.setAppIcon(appIconByte);
				pluginDescriptor.setActivityInfos(
						JsonUtil.parseObject(activityInfos, new TypeReference<HashMap<String, PluginActivityInfo>>() {
						}));
				pluginDescriptor.setActivitys(
						JsonUtil.parseObject(activitys, new TypeReference<HashMap<String, ArrayList<PluginIntentFilter>>>() {
						}));
				pluginDescriptor.setProviderInfos(
						JsonUtil.parseObject(providerInfos, new TypeReference<HashMap<String, PluginProviderInfo>>() {
						}));
				pluginDescriptor.setfragments(
						JsonUtil.parseObject(fragments, new TypeReference<HashMap<String, String>>() {
						}));
				pluginDescriptor.setReceivers(
						JsonUtil.parseObject(receivers, new TypeReference<HashMap<String, ArrayList<PluginIntentFilter>>>() {

						}));
				pluginDescriptor.setServices(
						JsonUtil.parseObject(services, new TypeReference<HashMap <String, ArrayList< PluginIntentFilter >>>(){

						}));
				pluginDescriptor.setMetaData(
						JsonUtil.parseObject(mata, new TypeReference<HashMap<String, String>>(){ }));
				pluginDescriptors.put(pluginId, pluginDescriptor);
			}
			return pluginDescriptors;
		}
	}

	/**
	 * convert to info list
	 *
	 * @param cursor cursor
	 * @return infos
	 */
	private PluginDescriptor convertToPlugin(Cursor cursor) {
		if (cursor == null || cursor.getCount() == 0) {
			return null;
		} else {
			PluginDescriptor pluginDescriptor = new PluginDescriptor();
			String pluginId = cursor.getString(cursor.getColumnIndex(Columns.PLUGIN_ID));
			String url = cursor.getString(cursor.getColumnIndex(Columns.URL));
			String pluginName = cursor.getString(cursor.getColumnIndex(Columns.PLUGIN_NAME));
			String packageName = cursor.getString(cursor.getColumnIndex(Columns.PACKAGE_NAME));
			String path = cursor.getString(cursor.getColumnIndex(Columns.INSTALL_PATH));
			String version = cursor.getString(cursor.getColumnIndex(Columns.VERSION));
			String mianAt = cursor.getString(cursor.getColumnIndex(Columns.APK_MAINACTIVITY));
			int status = cursor.getInt(cursor.getColumnIndex(Columns.STATUS));
			int type = cursor.getInt(cursor.getColumnIndex(Columns.TYPE));
			String descripion = cursor.getString(cursor.getColumnIndex(Columns.DESCRIPTION));
			int isEnableValue = cursor.getInt(cursor.getColumnIndex(Columns.IS_ENABLED));
			int isStandaloneValue = cursor.getInt(cursor.getColumnIndex(Columns.IS_STANDALONE));
			byte[] appIconByte = cursor.getBlob(cursor.getColumnIndex(Columns.APP_ICON));
			String applicationName = cursor.getString(cursor.getColumnIndex(Columns.APPLICATION_NAME));
			int applicationLogo = cursor.getInt(cursor.getColumnIndex(Columns.APPLICATION_LOGO));
			int apiacationIcon = cursor.getInt(cursor.getColumnIndex(Columns.APPLICATION_ICON));
			int applicationTheme = cursor.getInt(cursor.getColumnIndex(Columns.APPLICATION_THEME));
			String activityInfos = cursor.getString(cursor.getColumnIndex(Columns.ACTIVITY_INFOS));
			String providerInfos = cursor.getString(cursor.getColumnIndex(Columns.PROVIDER_INFOS));
			String activitys = cursor.getString(cursor.getColumnIndex(Columns.ACTIVITYS));
			String fragments = cursor.getString(cursor.getColumnIndex(Columns.FRAGMENTS));
			String services = cursor.getString(cursor.getColumnIndex(Columns.SERVICES));
			String receivers = cursor.getString(cursor.getColumnIndex(Columns.RECEIVERS));
			String mata = cursor.getString(cursor.getColumnIndex(Columns.METADATA));
			String progressMap = cursor.getString(cursor.getColumnIndex(Columns.PROGRESS_MAP));

			// constucts pluginModel
			pluginDescriptor.setPluginID(pluginId);
			pluginDescriptor.setPackageName(packageName);
			pluginDescriptor.setPluginName(pluginName);
			pluginDescriptor.setApkMainActivity(mianAt);
			pluginDescriptor.setVersion(version);
			pluginDescriptor.setPluginType(type);
			pluginDescriptor.setUrl(url);
			pluginDescriptor.setInstalledPath(path);
			pluginDescriptor.setStatus(status);
			pluginDescriptor.setApkMainActivity(mianAt);
			pluginDescriptor.setDescription(descripion);
			pluginDescriptor.setApplicationIcon(apiacationIcon);
			pluginDescriptor.setApplicationName(applicationName);
			pluginDescriptor.setApplicationTheme(applicationTheme);
			pluginDescriptor.setApplicationLogo(applicationLogo);
			pluginDescriptor.setEnabled(isEnableValue == 0);
			pluginDescriptor.setStandalone(isStandaloneValue == 0);
			pluginDescriptor.setAppIcon(appIconByte);
			pluginDescriptor.setActivityInfos(
					JsonUtil.parseObject(activityInfos, new TypeReference<HashMap<String, PluginActivityInfo>>() {
					}));
			pluginDescriptor.setActivitys(
					JsonUtil.parseObject(activitys, new TypeReference<HashMap<String, ArrayList<PluginIntentFilter>>>() {
					}));
			pluginDescriptor.setProviderInfos(
					JsonUtil.parseObject(providerInfos, new TypeReference<HashMap<String, PluginProviderInfo>>() {
					}));
			pluginDescriptor.setfragments(
					JsonUtil.parseObject(fragments, new TypeReference<HashMap<String, String>>() {
					}));
			pluginDescriptor.setReceivers(
					JsonUtil.parseObject(receivers, new TypeReference<HashMap<String, ArrayList<PluginIntentFilter>>>() {

					}));
			pluginDescriptor.setServices(
					JsonUtil.parseObject(services, new TypeReference<HashMap <String, ArrayList< PluginIntentFilter >>>(){

					}));
			pluginDescriptor.setMetaData(
					JsonUtil.parseObject(mata, new TypeReference<HashMap<String, String>>(){ }));
			return pluginDescriptor;
		}
	}


	/**
	 * colums
	 */
	public static final class Columns implements BaseColumns {
		/** key */
		public static final String PLUGIN_ID = "pluginId";
		/** 本地路径/网络地址均可 */
		public static final String URL = "url";
		/** pluginName */
		public static final String PLUGIN_NAME = "pluginName";
		/** filename */
		public static final String PACKAGE_NAME = "packageName";
		/** savepath */
		public static final String INSTALL_PATH = "installPath";
		/** description */
		public static final String DESCRIPTION = "description";
		/** Theme */
		public static final String APPLICATION_THEME = "applicationTheme";
		/** status */
		public static final String STATUS = "status";
		/** version */
		public static final String VERSION = "version";
		/** apkMainActivity */
		public static final String APK_MAINACTIVITY = "apkMainActivity";
		/** 插件类型 */
		public static final String TYPE = "type";
		/** IS_STANDALONE */
		public static final String IS_STANDALONE = "isStandalone";
		/** IS_ENABLED */
		public static final String IS_ENABLED = "isEnabled";
		/** Icon */
		public static final String APPLICATION_ICON = "applicationIcon";
		/** applicationLogo */
		public static final String APPLICATION_LOGO = "logo";
		/** applicationName */
		public static final String APPLICATION_NAME = "applicationName";
		/** appIcon */
		public static final String APP_ICON = "appIcon";
        /** activityInfos */
        public static final String ACTIVITY_INFOS = "activityInfos";
		/** activitys map */
		public static final String ACTIVITYS = "activitys";
		/** receivers */
		public static final String RECEIVERS = "receivers";
		/** services List */
		public static final String SERVICES = "services";
		/** providerInfos List */
		public static final String PROVIDER_INFOS = "providerInfos";
		/** fragments List */
		public static final String FRAGMENTS = "fragments";
		/** services List */
		public static final String METADATA = "metaData";
		/** progress map */
		public static final String PROGRESS_MAP = "progressmap";
	}


}
