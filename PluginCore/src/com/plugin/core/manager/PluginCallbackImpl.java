package com.plugin.core.manager;

import android.content.Intent;

import com.plugin.config.PluginConfig;
import com.plugin.content.PluginDescriptor;
import com.plugin.core.PluginLoader;
import com.plugin.util.PaLog;

/**
 * Created by lyk on 2015/16/.
 */
public class PluginCallbackImpl implements PluginCallback {
    @Override
    public void onPluginLoaderInited() {
        PaLog.d("PluginLoader inited");
    }

    @Override
    public void onPluginInstalled(String packageName, String version) {
        Intent intent = new Intent(PluginConfig.PLUGIN_CHANGED_ACTION);
        intent.putExtra("type", "install");
        intent.putExtra("id", packageName);
        intent.putExtra("version", version);
        PluginLoader.getApplicatoin().sendBroadcast(intent);
    }

    @Override
    public void onPluginInstalled(PluginDescriptor pluginDescriptor) {

    }

    @Override
    public void onPluginRemoved(PluginDescriptor pluginDescriptor) {

    }

    @Override
    public void onPluginRemoved(String packageName) {
        Intent intent = new Intent(PluginConfig.PLUGIN_CHANGED_ACTION);
        intent.putExtra("type", "remove");
        intent.putExtra("id", packageName);
        PluginLoader.getApplicatoin().sendBroadcast(intent);
    }

    @Override
    public void onPluginStarted(String packageName) {
        Intent intent = new Intent(PluginConfig.PLUGIN_CHANGED_ACTION);
        intent.putExtra("type", "init");
        intent.putExtra("id", packageName);
        PluginLoader.getApplicatoin().sendBroadcast(intent);
    }

    @Override
    public void onPluginRemoveAll() {
        Intent intent = new Intent(PluginConfig.PLUGIN_CHANGED_ACTION);
        intent.putExtra("type", "remove_all");
        PluginLoader.getApplicatoin().sendBroadcast(intent);
    }

}
