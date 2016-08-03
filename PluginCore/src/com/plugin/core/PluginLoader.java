package com.plugin.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.plugin.content.PluginDescriptor;
import com.plugin.content.PluginIntentFilter;
import com.plugin.core.manager.PluginCallbackImpl;
import com.plugin.core.manager.PluginManagerImpl;
import com.plugin.core.manager.PluginCallback;
import com.plugin.core.manager.PluginManager;
import com.plugin.util.BitmapUtil;
import com.plugin.util.PaLog;
import com.plugin.util.ManifestParser;
import com.plugin.util.FileUtil;
import com.plugin.util.PackageVerifyer;
import com.plugin.util.RefInvoker;

import dalvik.system.DexClassLoader;

/**
 * PlunginLoader 插件加载器
 * created Liuyongkui by 2015-11-12
 */
public class PluginLoader {

	private static final boolean NEED_VERIFY_CERT = true;
	/** install sucess */
	private static final int SUCCESS = 0;
	/** file 路劲未发现 */
	private static final int SRC_FILE_NOT_FOUND = 1;
	/** copy error */
	private static final int COPY_FILE_FAIL = 2;
	/** SIGNATURES_INVALIDATE */
	private static final int SIGNATURES_INVALIDATE = 3;
	/** SIGNATURES_NOT_SAME */
	private static final int VERIFY_SIGNATURES_FAIL = 4;
	/** 解析失败 */
	private static final int PARSE_MANIFEST_FAIL = 5;
	/** INSTALL_FAIL */
	private static final int INSTALL_FAIL = 6;
	/** IS_LOADED */
	private static final int HAS_LOADED_FAIL = 7;
	/** VERSION IS LOW */
	private static final int VERSION_LOW_FAIL = 8;

	private static Application mApplication;

	private static boolean isLoaderInited = false;

	private static PluginManager mPluginManager;

	private static PluginCallback mChangeListener;

	private static PluginDescriptor pluginDescriptor;

	private PluginLoader() {
	}

	public static synchronized void initLoader(Application app) {

		initLoader(app, new PluginManagerImpl((Context) RefInvoker.getFieldObject(app, ContextWrapper.class.getName(),
				"mBase")));
	}

	/**
	 * 初始化loader, 只可调用一次
	 *
	 * @param app
	 */
	public static synchronized void initLoader(Application app, PluginManager manager) {

		if (!isLoaderInited) {

			PaLog.d("pluginSdk loadding >>>...");

			isLoaderInited = true;

			mApplication = app;

			PluginInjector.injectBaseContext(mApplication);

			Object activityThread = PluginInjector.getActivityThread();
			PluginInjector.injectInstrumentation(activityThread);
			PluginInjector.injectHandlerCallback(activityThread);

			mPluginManager = manager;
			mChangeListener = new PluginCallbackImpl();
			mPluginManager.loadInstalledPlugins();
			mChangeListener.onPluginLoaderInited();

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

				mApplication.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
					@Override
					public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
					}

					@Override
					public void onActivityStarted(Activity activity) {
					}

					@Override
					public void onActivityResumed(Activity activity) {
					}

					@Override
					public void onActivityPaused(Activity activity) {
					}

					@Override
					public void onActivityStopped(Activity activity) {
					}

					@Override
					public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
					}

					@Override
					public void onActivityDestroyed(Activity activity) {
						PluginStubBinding.unBindLaunchModeStubActivity(activity.getClass().getName(), activity.getIntent());
					}
				});
			}
			PaLog.d("pluginSdk load Finish");
		}
	}

	/**
	 * Install Plungin
	 * @param srcPluginFile
	 * @return
	 */
	public static synchronized int installPlugin(String srcPluginFile) {
		PaLog.d("开始安装插件", srcPluginFile);
		if (TextUtils.isEmpty(srcPluginFile) || !new File(srcPluginFile).exists()) {
			return SRC_FILE_NOT_FOUND;
		}

		//step0:先将apk复制到宿主程序私有目录，防止在安装过程中文件被篡改

		if (TextUtils.isEmpty(copyPluginToHost(srcPluginFile))) {

			return COPY_FILE_FAIL;

		}


		// step 1 ，验证插件APK签名，如果被篡改过，将获取不到证书
		if (checkPluginSign(srcPluginFile) != SUCCESS) {

			return checkPluginSign(srcPluginFile);
		}

		// 第2步，解析Manifest，获得插件详情
		pluginDescriptor = parsePlugin(srcPluginFile);


		if (pluginDescriptor == null || TextUtils.isEmpty(pluginDescriptor.getPackageName())) {
			PaLog.e("解析插件Manifest文件失败", srcPluginFile);
			new File(srcPluginFile).delete();
			return PARSE_MANIFEST_FAIL;
		}
		//  this is PC getMothed forwith Test
		// pluginDescriptor.setApkMainActivity(getMainActivity(srcPluginFile)); PackageManager.GET_ACTIVITIES

		PackageInfo packageInfo = mApplication.getPackageManager().getPackageArchiveInfo(srcPluginFile, PackageManager.GET_ACTIVITIES);

		if (packageInfo != null) {
			pluginDescriptor.setApplicationTheme(packageInfo.applicationInfo.theme);
			pluginDescriptor.setApplicationIcon(packageInfo.applicationInfo.icon);
			pluginDescriptor.setApplicationLogo(packageInfo.applicationInfo.logo);
			pluginDescriptor.setAppIcon(BitmapUtil.getBytesByDrawable(getApkIcon(mApplication, srcPluginFile)));
			pluginDescriptor.setPluginName(getApkName(mApplication, srcPluginFile));
		}

		// 第3步，检查插件是否已经存在,若存在删除旧的
		PluginDescriptor oldPluginDescriptor = getPluginDescriptorByPluginId(pluginDescriptor.getPackageName());
		if (oldPluginDescriptor != null ) {
			if (!TextUtils.isEmpty(oldPluginDescriptor.getVersion())
					&& Long.parseLong(pluginDescriptor.getVersion()) > Long.parseLong(oldPluginDescriptor.getVersion())) {
				PaLog.e("已安装过，先删除低的旧版本", srcPluginFile);
				remove(oldPluginDescriptor.getPackageName());
				// 检查插件是否已经加载
				if (oldPluginDescriptor.getPluginContext() != null) {
					PaLog.e("插件已经加载, 现在不可执行安装操作");
					return HAS_LOADED_FAIL;
				} else {
					PaLog.e("先删除已安装的低版版本");
					remove(oldPluginDescriptor.getPackageName());
				}
			} else {
				return VERSION_LOW_FAIL;
			}
		}

		// 第4步骤，复制插件到插件目录
		String destPluginFile = mPluginManager.genInstallPath(pluginDescriptor.getPackageName(), pluginDescriptor.getVersion());
		boolean isCopySuccess = FileUtil.copyFile(srcPluginFile, destPluginFile);

		if (!isCopySuccess) {

			PaLog.d("复制插件到安装目录失败", srcPluginFile);
			new File(srcPluginFile).delete();
			return COPY_FILE_FAIL;
		} else {
			pluginDescriptor.setUrl(srcPluginFile);
			pluginDescriptor.setPluginID(pluginDescriptor.getPackageName());

			//第5步，复制插件so到插件so目录, 在构造插件Dexclassloader的时候，会使用这个so目录作为参数

			copyPluginSoToHost(destPluginFile, srcPluginFile);

			// 第6步 添加到已安装插件列表
			boolean isInstallSuccess = addPluginToList(destPluginFile);
			pluginDescriptor.setEnabled(isInstallSuccess);
			// 删除临时文件
			new File(srcPluginFile).delete();

			if (!isInstallSuccess) {

				PaLog.d("安装插件失败", srcPluginFile);
				return INSTALL_FAIL;
			} else {
				//通过创建classloader来触发dexopt，但不加载
				PaLog.d("正在进行DEXOPT...", pluginDescriptor.getInstalledPath());
				PluginCreator.createPluginClassLoader(pluginDescriptor.getInstalledPath(), pluginDescriptor.isStandalone());
				PaLog.d("DEXOPT完毕");
				mChangeListener.onPluginInstalled(pluginDescriptor.getPackageName(), pluginDescriptor.getVersion());
				PaLog.d("安装插件成功", srcPluginFile);
				return SUCCESS;
			}

		}
	}




	/**
	 * check PluginSigin.
	 */
	private static int checkPluginSign(String srcPluginFile) {

		PackageInfo PackageInfo = mApplication.getPackageManager().getPackageArchiveInfo(srcPluginFile, 0);
		//Signature[] pluginSignatures = PackageInfo.signatures;
		Signature[] pluginSignatures = PackageVerifyer.collectCertificates(srcPluginFile, false);
		boolean isDebugable = (0 != (mApplication.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
		if (pluginSignatures == null) {
			PaLog.e("插件签名验证失败", srcPluginFile);
			new File(srcPluginFile).delete();
			return SIGNATURES_INVALIDATE;
		} else if (NEED_VERIFY_CERT && !isDebugable) {
			//可选步骤，验证插件APK证书是否和宿主程序证书相同。
			//证书中存放的是公钥和算法信息，而公钥和私钥是1对1的
			//公钥相同意味着是同一个作者发布的程序
			Signature[] mainSignatures = null;
			try {
				PackageInfo pkgInfo = mApplication.getPackageManager().getPackageInfo(
						mApplication.getPackageName(), PackageManager.GET_SIGNATURES);
				mainSignatures = pkgInfo.signatures;
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}
			if (!PackageVerifyer.isSignaturesSame(mainSignatures, pluginSignatures)) {
				PaLog.e("插件证书和宿主证书不一致", srcPluginFile);
				new File(srcPluginFile).delete();
				return VERIFY_SIGNATURES_FAIL;
			}
		}
		return SUCCESS;
	}


	/**
	 * copyPlugin to Host speartorz .
	 */
	private static String copyPluginToHost(String srcPluginFile) {

		if (!srcPluginFile.startsWith(mApplication.getCacheDir().getAbsolutePath())) {
			String tempFilePath = mApplication.getCacheDir().getAbsolutePath()
					+ File.separator + System.currentTimeMillis() + ".apk";
			if (FileUtil.copyFile(srcPluginFile, tempFilePath)) {
				srcPluginFile = tempFilePath;
				return tempFilePath;
			} else {
				PaLog.e("复制插件文件失败失败", srcPluginFile, tempFilePath);
				return null;
			}
		}
		return null;
	}

	private static void copyPluginSoToHost(String destPluginFile , String srcPluginFile) {

		File tempDir = new File(new File(destPluginFile).getParentFile(), "temp");
		Set<String> soList = FileUtil.unZipSo(srcPluginFile, tempDir);
		if (soList != null) {
			for (String soName : soList) {
				FileUtil.copySo(tempDir, soName, new File(destPluginFile).getParent() + File.separator + "lib");
			}
			FileUtil.deleteAll(tempDir);
		}
	}

	/** parsePlugin
	 * @param srcPluginFile
	 * @return
	 */
	private static PluginDescriptor parsePlugin(String srcPluginFile) {

		return ManifestParser.parseManifest(srcPluginFile);
	}

	/**
	 * addPluginToList
	 */
	private static boolean addPluginToList(String destPluginFile) {

		pluginDescriptor.setInstalledPath(destPluginFile);

		return mPluginManager.addOrReplace(pluginDescriptor);
	}




	/**
	 * 根据插件中的classId加载一个插件中的class
	 *
	 * @param clazzId
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Class loadPluginFragmentClassById(String clazzId) {

		PluginDescriptor pluginDescriptor = getPluginDescriptorByFragmenetId(clazzId);

		if (pluginDescriptor != null) {

			ensurePluginInited(pluginDescriptor);

			DexClassLoader pluginClassLoader = pluginDescriptor.getPluginClassLoader();

			String clazzName = pluginDescriptor.getPluginClassNameById(clazzId);
			if (clazzName != null) {
				try {
					Class pluginClazz = ((ClassLoader) pluginClassLoader).loadClass(clazzName);
					PaLog.d("loadPluginClass for clazzId", clazzId, "clazzName", clazzName, "success");
					return pluginClazz;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

		PaLog.e("loadPluginClass for clazzId", clazzId, "fail");

		return null;

	}

	@SuppressWarnings("rawtypes")
	public static Class loadPluginClassByName(String clazzName) {

		PluginDescriptor pluginDescriptor = getPluginDescriptorByClassName(clazzName);

		if (pluginDescriptor != null) {

			ensurePluginInited(pluginDescriptor);

			DexClassLoader pluginClassLoader = pluginDescriptor.getPluginClassLoader();

			try {
				Class pluginClazz = ((ClassLoader) pluginClassLoader).loadClass(clazzName);
				PaLog.d("loadPluginClass Success for clazzName ", clazzName);
				return pluginClazz;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (java.lang.IllegalAccessError illegalAccessError) {
				illegalAccessError.printStackTrace();
				throw new IllegalAccessError("出现这个异常最大的可能是插件dex和" +
						"宿主dex包含了相同的class导致冲突, " +
						"请检查插件的编译脚本，确保排除了所有公共依赖库的jar");
			}

		}

		PaLog.e("loadPluginClass Fail for clazzName ", clazzName);

		return null;

	}

	/**
	 * 获取当前class所在插件的Context
	 * 每个插件只有1个DefaultContext,
	 * 是当前插件中所有class公用的Context
	 *
	 * @param clazz
	 * @return
	 */
	public static Context getDefaultPluginContext(@SuppressWarnings("rawtypes") Class clazz) {

		Context pluginContext = null;

		PluginDescriptor pluginDescriptor = getPluginDescriptorByClassName(clazz.getName());

		if (pluginDescriptor != null) {
			pluginContext = pluginDescriptor.getPluginContext();
		} else {
			PaLog.e("PluginDescriptor Not Found for ", clazz.getName());
		}

		if (pluginContext == null) {
			PaLog.e("Context Not Found for ", clazz.getName());
		}

		return pluginContext;

	}

	/**
	 * 根据当前class所在插件的默认Context, 为当前插件Class创建一个单独的context
	 *
	 * 原因在插件Activity中，每个Activity都应当建立独立的Context，
	 *
	 * 而不是都使用同一个defaultContext，避免不同界面的主题和样式互相影响
	 *
	 * @param clazz
	 * @return
	 */
	public static Context getNewPluginContext(@SuppressWarnings("rawtypes") Class clazz) {
		Context pluginContext = getDefaultPluginContext(clazz);

		return getNewPluginContext(pluginContext);
	}

	public static Context getNewPluginContext(Context pluginContext) {
		if (pluginContext != null) {
			pluginContext = PluginCreator.createPluginApplicationContext(((PluginContextTheme) pluginContext).getPluginDescriptor(),
					mApplication, pluginContext.getResources(),
					(DexClassLoader) pluginContext.getClassLoader());
			pluginContext.setTheme(mApplication.getApplicationContext().getApplicationInfo().theme);
		}
		return pluginContext;
	}

	/**
	 * 根据当前插件的默认Context, 为当前插件的组件创建一个单独的context
	 *
	 * @param pluginContext
	 * @param base  由系统创建的Context。 其实际类型应该是ContextImpl
	 * @return
	 */
	/*package*/ static Context getNewPluginComponentContext(Context pluginContext, Context base) {
		Context newContext = null;
		if (pluginContext != null) {
			newContext = PluginCreator.createPluginContext(((PluginContextTheme) pluginContext).getPluginDescriptor(),
					base, pluginContext.getResources(),
					(DexClassLoader) pluginContext.getClassLoader());
			newContext.setTheme(mApplication.getApplicationContext().getApplicationInfo().theme);
		}
		return newContext;
	}

	public static Context getNewPluginApplicationContext(Class clazz) {
		Context defaultContext = getDefaultPluginContext(clazz);
		Context newContext = null;
		if (defaultContext != null) {
			newContext = PluginCreator.createPluginContext(((PluginContextTheme) defaultContext).getPluginDescriptor(),
					mApplication, defaultContext.getResources(),
					(DexClassLoader) defaultContext.getClassLoader());
			newContext.setTheme(mApplication.getApplicationContext().getApplicationInfo().theme);
		}
		return newContext;
	}


	/**
	 * 构造插件信息
	 *
	 * @param
	 */
	static void ensurePluginInited(PluginDescriptor pluginDescriptor) {
		if (pluginDescriptor != null) {
			DexClassLoader pluginClassLoader = pluginDescriptor.getPluginClassLoader();
			if (pluginClassLoader == null) {
				PaLog.d("正在初始化插件Resources, DexClassLoader, Context, Application ");

				PaLog.d("是否为独立插件", pluginDescriptor.isStandalone());

				Resources pluginRes = PluginCreator.createPluginResource(mApplication, pluginDescriptor.getInstalledPath(),
						pluginDescriptor.isStandalone());

				pluginClassLoader = PluginCreator.createPluginClassLoader(pluginDescriptor.getInstalledPath(),
						pluginDescriptor.isStandalone());
				Context pluginContext = PluginCreator
						.createPluginContext(pluginDescriptor, mApplication, pluginRes, pluginClassLoader);

				pluginContext.setTheme(mApplication.getApplicationContext().getApplicationInfo().theme);
				pluginDescriptor.setPluginContext(pluginContext);
				pluginDescriptor.setPluginClassLoader(pluginClassLoader);

				//使用了openAtlasExtention之后就不需要Public.xml文件了
				//checkPluginPublicXml(pluginDescriptor, pluginRes);

				callPluginApplicationOnCreate(pluginDescriptor);

				PaLog.d("初始化插件" + pluginDescriptor.getPackageName() + "完成");
			}
		}
	}

	private static void callPluginApplicationOnCreate(PluginDescriptor pluginDescriptor) {

		Application application = null;

		if (pluginDescriptor.getPluginApplication() == null && pluginDescriptor.getPluginClassLoader() != null) {
			try {
				PaLog.d("创建插件Application", pluginDescriptor.getApplicationName());
				application = Instrumentation.newApplication(pluginDescriptor.getPluginClassLoader().
						loadClass(pluginDescriptor.getApplicationName()) , pluginDescriptor.getPluginContext());
				pluginDescriptor.setPluginApplication(application);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} 	catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		PluginInjector.installContentProviders(mApplication, pluginDescriptor.getProviderInfos().values());

		if (application != null) {
			application.onCreate();
		}

		mChangeListener.onPluginStarted(pluginDescriptor.getPackageName());
	}

	/**
	 * for eclipse & ant with public.xml
	 *
	 * unused
	 * @param pluginDescriptor
	 * @param res
	 * @return
	 */
	private static boolean checkPluginPublicXml(PluginDescriptor pluginDescriptor, Resources res) {

		// "plugin_layout_1"资源id时由public.xml配置的
		// 如果没有检测到这个资源，说明编译时没有引入public.xml,
		// 这里直接抛个异常出去。
		// 不同的系统版本获取id的方式不同，
		// 三星4.x等系统适用
		int publicStub = res.getIdentifier("plugin_layout_1", "layout", pluginDescriptor.getPackageName());
		if (publicStub == 0) {
			// 小米5.x等系统适用
			publicStub = res.getIdentifier("plugin_layout_1", "layout", mApplication.getPackageName());
		}
		if (publicStub == 0) {
			try {
				// 如果以上两种方式都检测失败，最后尝试通过反射检测
				Class layoutClass = ((ClassLoader) pluginDescriptor.getPluginClassLoader()).loadClass(pluginDescriptor
						.getPackageName() + ".R$layout");
				Integer layouId = (Integer) RefInvoker.getFieldObject(null, layoutClass, "plugin_layout_1");
				if (layouId != null) {
					publicStub = layouId;
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		if (publicStub == 0) {
			throw new IllegalStateException("\n插件工程没有使用public.xml给资源id分组！！！\n" + "插件工程没有使用public.xml给资源id分组！！！\n"
					+ "插件工程没有使用public.xml给资源id分组！！！\n" + "重要的事情讲三遍！！！");
		}
		return true;
	}

	public static boolean isInstalled(String pluginId, String pluginVersion) {
		PluginDescriptor pluginDescriptor = getPluginDescriptorByPluginId(pluginId);
		if (pluginDescriptor != null) {
			return pluginDescriptor.getVersion().equals(pluginVersion);
		}
		return false;
	}

	public static Application getApplicatoin() {
		return mApplication;
	}

	/**
	 * 清除列表并不能清除已经加载到内存当中的class,因为class一旦加载后后无法卸载
	 */
	public static synchronized void removeAll() {
		boolean isSuccess = mPluginManager.removeAll();
		if (isSuccess) {
			mChangeListener.onPluginRemoveAll();
		}
	}

	public static synchronized void remove(String pluginId) {
		boolean isSuccess = mPluginManager.remove(pluginId);
		if (isSuccess) {
			mChangeListener.onPluginRemoved(pluginId);
		}
	}

	@SuppressWarnings("unchecked")
	public static Collection<PluginDescriptor> getPlugins() {
		return mPluginManager.getPlugins();
	}

	/**
	 * for Fragment
	 *
	 * @param clazzId
	 * @return
	 */
	public static PluginDescriptor getPluginDescriptorByFragmenetId(String clazzId) {
		return mPluginManager.getPluginDescriptorByFragmenetId(clazzId);
	}

	public static PluginDescriptor getPluginDescriptorByPluginId(String pluginId) {
		return mPluginManager.getPluginDescriptorByPluginId(pluginId);
	}

	public static PluginDescriptor getPluginDescriptorByClassName(String clazzName) {
		return mPluginManager.getPluginDescriptorByClassName(clazzName);
	}

	public static PluginDescriptor getPluginsDescriptorByPath(String srcDirFile) {
		return mPluginManager.getPluginsDescriptorByPath(srcDirFile);
	}

	public static synchronized void enablePlugin(String pluginId, boolean enable) {
		mPluginManager.enablePlugin(pluginId, enable);
	}

	public static PluginDescriptor initPluginByPluginId(String pluginId) {

		PluginDescriptor pluginDescriptor = getPluginDescriptorByPluginId(pluginId);
		if (pluginDescriptor != null) {
			ensurePluginInited(pluginDescriptor);
		}
		return pluginDescriptor;
	}


	/**
	 * //If getComponent returns an explicit class, that is returned without any
	 * further consideration. //If getAction is non-NULL, the activity must
	 * handle this action. //If resolveType returns non-NULL, the activity must
	 * handle this type. //If addCategory has added any categories, the activity
	 * must handle ALL of the categories specified. //If getPackage is non-NULL,
	 * only activity components in that application package will be considered.
	 *
	 * @param intent
	 * @return
	 */
	public static String matchPlugin(Intent intent) {

		Iterator<PluginDescriptor> itr = getPlugins().iterator();

		while (itr.hasNext()) {
			PluginDescriptor plugin = itr.next();
			// 如果是通过组件进行匹配的
			if (intent.getComponent() != null) {
				if (plugin.containsName(intent.getComponent().getClassName())) {
					return intent.getComponent().getClassName();
				}
			} else {
				// 如果是通过IntentFilter进行匹配的
				String clazzName = findClassNameByIntent(intent, plugin.getActivitys());

				if (clazzName == null) {
					clazzName = findClassNameByIntent(intent, plugin.getServices());
				}

				if (clazzName == null) {
					clazzName = findClassNameByIntent(intent, plugin.getReceivers());
				}

				if (clazzName != null) {
					return clazzName;
				}
			}

		}
		return null;
	}

	/**
	 * 获取目标类型，activity or service or broadcast
	 * @param intent
	 * @return
	 */
	public static int getTargetType(Intent intent) {

		Iterator<PluginDescriptor> itr = getPlugins().iterator();

		while (itr.hasNext()) {
			PluginDescriptor plugin = itr.next();
			// 如果是通过组件进行匹配的
			if (intent.getComponent() != null) {
				if (plugin.containsName(intent.getComponent().getClassName())) {
					return plugin.getType(intent.getComponent().getClassName());
				}
			} else {
				String clazzName = findClassNameByIntent(intent, plugin.getActivitys());

				if (clazzName == null) {
					clazzName = findClassNameByIntent(intent, plugin.getServices());
				}

				if (clazzName == null) {
					clazzName = findClassNameByIntent(intent, plugin.getReceivers());
				}

				if (clazzName != null) {
					return plugin.getType(clazzName);
				}
			}
		}
		return PluginDescriptor.UNKOWN;
	}

	/** 查找组件名.
	 * @param intent
	 * @param intentFilter
	 * @return
	 */
	private static String findClassNameByIntent(Intent intent, HashMap<String, ArrayList<PluginIntentFilter>> intentFilter) {
		if (intentFilter != null) {

			Iterator<Entry<String, ArrayList<PluginIntentFilter>>> entry = intentFilter.entrySet().iterator();
			while (entry.hasNext()) {
				Entry<String, ArrayList<PluginIntentFilter>> item = entry.next();
				Iterator<PluginIntentFilter> values = item.getValue().iterator();
				while (values.hasNext()) {
					PluginIntentFilter filter = values.next();
					int result = filter.match(intent.getAction(), intent.getType(), intent.getScheme(),
							intent.getData(), intent.getCategories());

					if (result != PluginIntentFilter.NO_MATCH_ACTION
							&& result != PluginIntentFilter.NO_MATCH_CATEGORY
							&& result != PluginIntentFilter.NO_MATCH_DATA
							&& result != PluginIntentFilter.NO_MATCH_TYPE) {
						return item.getKey();
					}
				}
			}
		}
		return null;
	}

	/**
	 * 查询出APK文件的包名和主Activity
	 * aapt命令获得：
	 * 			aapt dump badging PATH
	 * @param path
	 */
	public static String getMainActivity(String path) {
		String line = null;
		try {
			Process pro = Runtime.getRuntime().exec("aapt dump badging " + path);
			BufferedReader buf = new BufferedReader(new InputStreamReader(pro.getInputStream()));
			while((line = buf.readLine()) != null){
				if(line.startsWith("package: name=")){
					String[] s = line.split(" ");
					String[] ss = s[1].split("'");
					//apk.setPackagename(ss[1]);
				}
				if(line.startsWith("launchable-activity: name=")) {
					String[] s = line.split(" ");
					String[] ss = s[1].split("'");
					//apk.setActivity(ss[1]);
					return ss[1];
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return null;

	}

	/*
	 * getApkIcon
	 * appInfo.publicSourceDir = apkPath;来修正这个问题
	 */
	public static Drawable getApkIcon(Context context, String apkPath) {
		PackageManager pm = context.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(apkPath,
				PackageManager.GET_ACTIVITIES);
		if (info != null) {
			ApplicationInfo appInfo = info.applicationInfo;
			appInfo.sourceDir = apkPath;
			appInfo.publicSourceDir = apkPath;
			try {
				return appInfo.loadIcon(pm);
			} catch (OutOfMemoryError e) {
				PaLog.e("**Plugin icon load error", e.toString());
			}
		}
		return null;
	}


	/**
	 * @param context
	 * @param apkPath
	 * @return
	 */
	public static String getApkName(Context context, String apkPath) {
		PackageManager pm = context.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(apkPath,
				PackageManager.GET_ACTIVITIES);
		if (info != null) {
			ApplicationInfo appInfo = info.applicationInfo;
			appInfo.sourceDir = apkPath;
			appInfo.publicSourceDir = apkPath;
			try {
				return appInfo.loadLabel(pm).toString();
			} catch (OutOfMemoryError e) {
				PaLog.e("**Plugin name load error", e.toString());
			}
		}
		return null;
	}

}
