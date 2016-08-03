package com.plugin.core.manager;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;

import com.plugin.config.PluginConfig;
import com.plugin.content.PluginDescriptor;
import com.plugin.core.PluginLoader;
import com.plugin.database.PluginDatabaseManager;
import com.plugin.util.FileUtil;
import com.plugin.util.JsonUtil;
import com.plugin.util.PaLog;
import com.plugin.util.RefInvoker;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

public class PluginManagerImpl implements PluginManager {


	private Context mContext;

	private PluginDatabaseManager mDBManager;

	/** instance */
	private static  PluginManagerImpl sInstance;


	public PluginManagerImpl() {

	}

	public PluginManagerImpl(Context aContext) {
		mContext = aContext;
		mDBManager = PluginDatabaseManager.getInstance(mContext);
	}

	/**
	 * getInstance
	 *
	 * @param aContext context
	 * @return PluginManagerImpl
	 */
	public static synchronized PluginManagerImpl getInstance(Context aContext) {
		if (sInstance == null) {
			sInstance = new PluginManagerImpl(aContext);
		}
		return sInstance;
	}

	public Context getContext() {
		return mContext;
	}

	public void setContext(Context mContext) {
		this.mContext = mContext;
	}

	private final Hashtable<String, PluginDescriptor> sInstalledPlugins = new Hashtable<String, PluginDescriptor>();


	@Override
	public String genInstallPath(String pluginId, String pluginVersoin) {
		return PluginLoader.getApplicatoin().getDir(PluginConfig.DF_PLUGIN_INSTALLED_DIR, Context.MODE_PRIVATE).getAbsolutePath() + "/" + pluginId + "/"
				+ pluginVersoin + "/" + pluginId + ".apk";
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized void loadInstalledPlugins() {
		if (sInstalledPlugins.size() == 0) {
			Hashtable<String, PluginDescriptor> installedPlugins = null;
			if (mDBManager == null) {
				mDBManager = PluginDatabaseManager.getInstance((Context) RefInvoker.getFieldObject(PluginLoader.getApplicatoin(), ContextWrapper.class.getName(),
						"mBase"));
			}
			installedPlugins = mDBManager.queryAll();
			if (installedPlugins != null ) {
				sInstalledPlugins.putAll(installedPlugins);
			} else {
				/*Object object = getPluginsBySp();
				if (object != null) {
					installedPlugins = (Hashtable<String, PluginDescriptor>) object;
					Iterator<PluginDescriptor> itr = installedPlugins.values().iterator();
					*//*while (itr.hasNext()) {
						PluginDescriptor descriptor = itr.next();
						//mDBManager.insert(descriptor);
					}*//*
					sInstalledPlugins.putAll(installedPlugins);
				}*/
			}
		}
	}

	@Override
	public boolean addOrReplace(PluginDescriptor pluginDescriptor) {
		sInstalledPlugins.put(pluginDescriptor.getPackageName(), pluginDescriptor);
		long stats = mDBManager.insert(pluginDescriptor);
		return saveInstalledPlugins() && stats != -1;
	}

	@Override
	public synchronized boolean removeAll() {
		sInstalledPlugins.clear();
		return saveInstalledPlugins() && !(mDBManager.deleteAll() == 0);
	}

	@Override
	public synchronized boolean remove(String pluginId) {
		PluginDescriptor old = sInstalledPlugins.remove(pluginId);
		if (old != null) {
			int stats = mDBManager.delete(new String[]{pluginId});
			boolean isSuccess = saveInstalledPlugins();
			boolean deleteSuccess = FileUtil.deleteAll(new File(old.getInstalledPath()).getParentFile());
			PaLog.d("delete old", isSuccess, deleteSuccess, old.getInstalledPath(), old.getPackageName());
			return isSuccess && (stats == 1);
		}
		return false;
	}

	@Override
	public Collection<PluginDescriptor> getPlugins() {
		if (sInstalledPlugins.size() == 0) {
			loadInstalledPlugins();
		}
		return sInstalledPlugins.values();
	}

	@Override
	public synchronized void enablePlugin(String pluginId, boolean enable) {
		PluginDescriptor pluginDescriptor = sInstalledPlugins.get(pluginId);
		if (pluginDescriptor != null && !pluginDescriptor.isEnabled()) {
			pluginDescriptor.setEnabled(enable);
			saveInstalledPlugins();
		}
	}

	/**
	 * for Fragment
	 *
	 * @param clazzId
	 * @return
	 */
	@Override
	public PluginDescriptor getPluginDescriptorByFragmenetId(String clazzId) {
		Iterator<PluginDescriptor> itr = sInstalledPlugins.values().iterator();
		while (itr.hasNext()) {
			PluginDescriptor descriptor = itr.next();
			if (descriptor.containsFragment(clazzId)) {
				return descriptor;
			}
		}
		return null;
	}

	@Override
	public PluginDescriptor getPluginDescriptorByPluginId(String pluginId) {
		PluginDescriptor pluginDescriptor = sInstalledPlugins.get(pluginId);
		if (pluginDescriptor != null && pluginDescriptor.isEnabled()) {
			return pluginDescriptor;
		}
		return null;
	}

	@Override
	public PluginDescriptor getPluginDescriptorByClassName(String clazzName) {
		Iterator<PluginDescriptor> itr = sInstalledPlugins.values().iterator();
		while (itr.hasNext()) {
			PluginDescriptor descriptor = itr.next();
			if (descriptor.containsName(clazzName)) {
				return descriptor;
			}
		}
		return null;
	}

	@Override
	public PluginDescriptor getPluginsDescriptorByPath(String aSrcDirPath) {
		Iterator<PluginDescriptor> itr = sInstalledPlugins.values().iterator();
		while (itr.hasNext()) {
			PluginDescriptor descriptor = itr.next();
			if (descriptor.containsPath(aSrcDirPath)) {
				return descriptor;
			}
		}
		return null;
	}

	private static SharedPreferences getSharedPreference() {
		SharedPreferences sp = PluginLoader.getApplicatoin().getSharedPreferences(PluginConfig.SP_PLUGIN_INSTALLED_KEY,
				Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ? Context.MODE_PRIVATE : Context.MODE_PRIVATE | 0x0004);
		return sp;
	}

	private synchronized boolean saveInstalledPlugins() {

		String jsonStr = JsonUtil.toJSONString(sInstalledPlugins);
		ObjectOutputStream objectOutputStream = null;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(sInstalledPlugins);
			objectOutputStream.flush();

			byte[] data = byteArrayOutputStream.toByteArray();
			String list = Base64.encodeToString(data, Base64.DEFAULT);

			PaLog.e(">>>>>>>>>>>>>", jsonStr);

			getSharedPreference().edit().putString(PluginConfig.SP_PLUGIN_KEY, list).commit();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (objectOutputStream != null) {
				try {
					objectOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (byteArrayOutputStream != null) {
				try {
					byteArrayOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	/** 从sp读取插件信息
	 * @return
	 */
	private synchronized Object getPluginsBySp() {

		String list = getSharedPreference().getString(PluginConfig.SP_PLUGIN_KEY, "");
		Serializable object = null;
		if (!TextUtils.isEmpty(list)) {
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
					Base64.decode(list, Base64.DEFAULT));
			ObjectInputStream objectInputStream = null;
			try {
				objectInputStream = new ObjectInputStream(byteArrayInputStream);
				object = (Serializable) objectInputStream.readObject();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (objectInputStream != null) {
					try {
						objectInputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (byteArrayInputStream != null) {
					try {
						byteArrayInputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return object;

	}



}