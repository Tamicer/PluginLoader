package com.plugin.core.manager;

import com.plugin.content.PluginDescriptor;
import java.util.Collection;

/**
 * PluginManager 插件管理接口
 * created Liuyongkui by 2015-11-12
 */
public interface PluginManager {

	/**
	 * load 插件
	 */
	void loadInstalledPlugins();

	/** 添加插件（新增或替换）
	 * @param pluginDescriptor
	 * @return
	 */
	boolean addOrReplace(PluginDescriptor pluginDescriptor);

	/** 移除
	 * @param packageName
	 * @return
	 */
	boolean remove(String packageName);

	/** 移除ALL
	 * @return
	 */
	boolean removeAll();

	/** enablePluin
	 * @param pluginId
	 * @param enable
	 */
	void enablePlugin(String pluginId, boolean enable);

	/** getInstallPluginPath
	 * @param pluginId
	 * @param pluginVersoin
	 * @return
	 */
	String genInstallPath(String pluginId, String pluginVersoin);


	/** get plugin all
	 * @return
	 */
	Collection<PluginDescriptor> getPlugins();

	/**
	 * ByFragmenetId
	 * @param clazzId
	 * @return
	 */
	PluginDescriptor getPluginDescriptorByFragmenetId(String clazzId);

	/** 通过ID查找Plugin
	 * @param pluginId
	 * @return
	 */
	PluginDescriptor getPluginDescriptorByPluginId(String pluginId);

	/**
	 * 通过类名查找Plugin
	 * @param clazzName
	 * @return
	 */
	PluginDescriptor getPluginDescriptorByClassName(String clazzName);

	/**
	 * 通过目录查找所有Plugins
	 * @param SrcDirFile
	 * @return
	 */
	PluginDescriptor getPluginsDescriptorByPath(String SrcDirFile);



}