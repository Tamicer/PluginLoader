package com.plugin.database;

import android.content.ContentValues;
import android.content.Context;

import com.plugin.content.PluginDescriptor;
import com.plugin.util.BitmapUtil;
import com.plugin.util.JsonUtil;
import com.plugin.util.PaLog;

import java.util.Hashtable;

/**
 * Created by LIUYONGKUI726 on 2015-12-09.
 */
public class PluginDBController {


    private Context mContext;
    /** instance */
    private static  PluginDBController sInstance;

    private PluginDBCallback mPluginDBCallback;

    /**
     * constructor
     *
     * @param aPluginDBCallback aPluginDBCallback
     */
    private PluginDBController(PluginDBCallback aPluginDBCallback) {
        if (aPluginDBCallback != null) {
            mPluginDBCallback = aPluginDBCallback;
        }

    }

    /**
     * 获取单例
     *
     * @param aPluginDBCallback aPluginDBCallback
     * @return 单例
     */
    public static synchronized PluginDBController getInstance(PluginDBCallback aPluginDBCallback) {
        if (sInstance == null) {
            synchronized (PluginDBController.class) {
                if (sInstance == null) {
                    sInstance = new PluginDBController(aPluginDBCallback);
                }
            }
        }
        return sInstance;
    }

    /**
     * insert
     * @param aDescriptor
     * @return
     */
    public long insert(PluginDescriptor aDescriptor) {
        if (mPluginDBCallback != null) {
            PaLog.d("plugin mPluginDBCallback not null >>>>>>");
           return mPluginDBCallback.onPluginAdd(aDescriptor);
        }
        return -1;

    }

    /**
     * @return PluginDescriptors
     */
    public Hashtable<String, PluginDescriptor> loadAll() {
        if (mPluginDBCallback != null) {
            PaLog.d("plugin mPluginDBCallback not null >>>>>>");
           return mPluginDBCallback.onPluginLoadAll();
        }
        return null;

    }
}
