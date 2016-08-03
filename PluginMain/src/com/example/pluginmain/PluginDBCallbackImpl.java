package com.example.pluginmain;

import android.content.Intent;

import com.plugin.content.PluginDescriptor;
import com.plugin.core.PluginLoader;
import com.plugin.database.PluginDBCallback;
import com.plugin.database.PluginDatabaseManager;
import com.plugin.util.PaLog;

import java.util.Hashtable;

/**
 * Created by lyk on 2015-12-09
 */
public class PluginDBCallbackImpl implements PluginDBCallback {


    @Override
    public long onPluginAdd(PluginDescriptor pluginDescriptor) {

        PaLog.d("PluginDatabaseManager insert");

       return PluginDatabaseManager.getInstance(PluginListActivity.getMyself()).insert(pluginDescriptor);

    }

    @Override
    public void onPluginRemoved(PluginDescriptor pluginDescriptor) {

    }

    @Override
    public void onPluginUpdate(PluginDescriptor pluginDescriptor) {
    }


    @Override
    public int onPluginRemoveAll() {

        return PluginDatabaseManager.getInstance(PluginListActivity.getMyself()).deleteAll();

    }

    @Override
    public Hashtable<String, PluginDescriptor> onPluginLoadAll() {
        PaLog.d("PluginDatabaseManager queryAll()");
        return PluginDatabaseManager.getInstance(PluginListActivity.getMyself()).queryAll();
    }

    @Override
    public PluginDescriptor onPluginLoadById(String pluginId) {

        return PluginDatabaseManager.getInstance(PluginListActivity.getMyself()).querybyId(pluginId);

    }

}
