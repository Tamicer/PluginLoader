package com.plugin.database;

import com.plugin.content.PluginDescriptor;

import java.util.Hashtable;

/**
 * PluginBdCallback
 * created Liuyongkui by 2015-12-09
 */
public interface PluginDBCallback {

	long onPluginAdd(PluginDescriptor pluginDescriptor);

	void onPluginRemoved(PluginDescriptor pluginDescriptor);

	void onPluginUpdate(PluginDescriptor pluginDescriptor);

	int onPluginRemoveAll();

	Hashtable<String, PluginDescriptor> onPluginLoadAll();

	PluginDescriptor onPluginLoadById(String pluginId);

}
