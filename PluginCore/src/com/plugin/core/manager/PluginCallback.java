package com.plugin.core.manager;

import com.plugin.content.PluginDescriptor;

/**
 * PluginCallback
 * created Liuyongkui by 2015-11-16
 */
public interface PluginCallback {

	void onPluginLoaderInited();

	void onPluginInstalled(String packageName, String version);

	void onPluginInstalled(PluginDescriptor pluginDescriptor);

	void onPluginRemoved(PluginDescriptor pluginDescriptor);

	void onPluginRemoved(String packageName);

	void onPluginStarted(String packageName);

	void onPluginRemoveAll();

}
