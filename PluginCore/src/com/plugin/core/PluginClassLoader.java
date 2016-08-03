package com.plugin.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.plugin.content.PluginProviderInfo;
import com.plugin.util.PaLog;

import dalvik.system.DexClassLoader;

/**
 * 支持Receiver和ContentProvider，增加此类。
 * 
 * @author Liuyongkui
 * 
 */
public class PluginClassLoader extends DexClassLoader {

	/**
	 * unused
	 */
	private final BlockingQueue<String> mClassQueue = new LinkedBlockingQueue<String>();;

	/**
	 * unused
	 */
	public void offer(String className) {
		mClassQueue.offer(className);
	}

	/**
	 * consrtructs
	 * @param dexPath
	 * @param optimizedDirectory
	 * @param libraryPath
	 * @param parent
	 */
	public PluginClassLoader(String dexPath, String optimizedDirectory, String libraryPath, ClassLoader parent) {
		super(dexPath, optimizedDirectory, libraryPath, parent);
	}

	@Override
	public String findLibrary(String name) {
		PaLog.d("findLibrary", name);
		return super.findLibrary(name);
	}

	@Override
	protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {


		if (className.startsWith(PluginIntentResolver.prefix)) {
			// for Receiver
			String realName = className.replace(PluginIntentResolver.prefix, "");
			PaLog.d("className ", className, "target", realName);
			Class clazz = PluginLoader.loadPluginClassByName(realName);
			if (clazz != null) {
				return clazz;
			}

		} else if (className.startsWith(PluginProviderInfo.prefix)) {
			//for contentprovider
			String realName = className.replace(PluginProviderInfo.prefix, "");
			PaLog.d("className ", className, "target", realName);
			Class clazz = PluginLoader.loadPluginClassByName(realName);
			if (clazz != null) {
				return clazz;
			}
		}
		return super.loadClass(className, resolve);
	}

}
