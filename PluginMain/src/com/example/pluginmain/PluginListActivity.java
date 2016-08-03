package com.example.pluginmain;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pluginsharelib.SharePOJO;
import com.plugin.config.PluginConfig;
import com.plugin.content.PluginDescriptor;
import com.plugin.core.PluginLoader;
import com.plugin.core.PluginThemeHelper;
import com.plugin.database.*;
import com.plugin.util.BitmapUtil;
import com.plugin.util.FileUtil;
import com.plugin.util.PaLog;
import com.lyk.pluginmain.R;

public class PluginListActivity extends Activity {

	private ViewGroup mList;
	private Button install;
	private ListView mUnInstall;
	private ArrayAdapter mPluginAdapter;
	private EditText mPluginDirTxt;
	private Button mPluginLoader;
	private ProgressDialog dialogLoading;
	boolean isInstalled = false;
	public  static final String PLUGIN_NAME = "apk";
	private static final String TAG = "PluginListActivity";

	private static final String sdcard = Environment
			.getExternalStorageDirectory().getPath();

	/** 当前 ACTIVITY 实例 **/
	private static PluginListActivity myself;

	public static PluginListActivity getMyself() {
		return myself;
	}

	public static void setMyself(PluginListActivity myself) {
		PluginListActivity.myself = myself;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setMyself(this);
		PluginDatabaseManager.getInstance(this).init();
		PluginDBController.getInstance(new PluginDBCallbackImpl());

		int skin = PreferenceManager.getDefaultSharedPreferences(this).getInt("shinId", 0);

		if (skin != 0) {
			//两个参数：1、插件id，插件主题id
			String pluginId = "com.example.plugintest";
			PluginThemeHelper.applyPluginTheme(this, pluginId, skin);
		}

		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_activity);

		setTitle("插件列表");

		// 监听插件安装 安装新插件后刷新当前页面
		registerReceiver(pluginChange, new IntentFilter(PluginConfig.PLUGIN_CHANGED_ACTION));

		initView();

		listAll();

	}

	private void initView() {

		mList = (ViewGroup) findViewById(R.id.list);
		install = (Button) findViewById(R.id.install);
		mUnInstall = (ListView) findViewById(R.id.unInstall_id);
		mPluginDirTxt = (EditText) findViewById(R.id.pluginDirTxt);
		mPluginLoader = (Button) findViewById(R.id.pluginLoader);
		final String pluginSrcDir = sdcard + "/Download/";
		// test
		mPluginDirTxt.setHint(pluginSrcDir + "安安租.apk");
		final String[] mPlugins = findPlugin();
		if (mPlugins.length > 0 && mPlugins != null) {
			mPluginAdapter = new ArrayAdapter<>(PluginListActivity.this, android.R.layout.simple_list_item_1, mPlugins);
			mUnInstall.setAdapter(mPluginAdapter);
			mUnInstall.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

					dialogLoading = ProgressDialog.show(
							PluginListActivity.this, "请稍后", "安装中...", true);
					//PluginLoader.

					copyAndInstall(mPlugins[position]);
					dialogLoading.dismiss();
				}
			});
		}

		install.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				PluginLoader.removeAll();

			}


		});

		mPluginLoader.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				dialogLoading = ProgressDialog.show(
						PluginListActivity.this, "请稍后", "安装中...", true);

				String mPluginSrcDir;

				if (mPluginDirTxt.getText().toString().trim() != null && mPluginDirTxt.getText().toString().trim().length() > 0) {


					mPluginSrcDir = pluginSrcDir + mPluginDirTxt.getText().toString();

				} else {
					mPluginSrcDir = pluginSrcDir + "安安租.apk";
				}

				if (mPluginSrcDir != null) {

					if (PluginLoader.installPlugin(mPluginSrcDir) != 0) {

						Toast.makeText(PluginListActivity.this, "安装失败或者解压出错！", Toast.LENGTH_LONG).show();

					}

					dialogLoading.dismiss();
				}

			}


		});

		findViewById(R.id.allThemes).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String pluginId = "com.example.plugintest";
				HashMap<String, Integer> themes = PluginThemeHelper.getAllPluginThemes(pluginId);
				Iterator<Map.Entry<String, Integer>> itr = themes.entrySet().iterator();
				String text = "";
				while (itr.hasNext()) {
					Map.Entry<String, Integer> entry = itr.next();
					text = text + entry.getKey() + ":" + entry.getValue() + "\n";
				}

				AlertDialog.Builder builder = new AlertDialog.Builder(PluginListActivity.this);
				TextView tv = new TextView(PluginListActivity.this);
				builder.setView(tv);
				tv.setText(text);
				builder.setTitle("插件可选主题列表");

				AlertDialog dialog = builder.create();
				dialog.setCanceledOnTouchOutside(true);
				dialog.show();
			}
		});

		findViewById(R.id.blue).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//需要先从插件中获取插件提供的可选主题列表
				String pluginId = "com.example.plugintest";

				int themeId = PluginThemeHelper.getPluginThemeIdByName(pluginId, "PluginTheme2");

				PreferenceManager.getDefaultSharedPreferences(PluginListActivity.this)
						.edit().putInt("shinId", themeId).commit();

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					//重启使主题生效
					PluginListActivity.this.recreate();
				}
			}
		});

		findViewById(R.id.red).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//需要先从插件中获取插件提供的可选主题列表
				String pluginId = "com.example.plugintest";

				int themeId = PluginThemeHelper.getPluginThemeIdByName(pluginId, "PluginTheme4");

				PreferenceManager.getDefaultSharedPreferences(PluginListActivity.this)
						.edit().putInt("shinId", themeId).commit();

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					//重启使主题生效
					PluginListActivity.this.recreate();
				}
			}
		});

		findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PreferenceManager.getDefaultSharedPreferences(PluginListActivity.this).edit().remove("shinId").commit();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					//重启使主题生效
					PluginListActivity.this.recreate();
				}
			}
		});

	}

	private void unInstall(String pluginId) {
		PluginLoader.remove(pluginId);
	}

	private String[] findPlugin() {
		AssetManager assetManager = getAssets();
		String[] files = null;
		try {
			files = assetManager.list(PLUGIN_NAME);
		} catch (IOException e) {
			PaLog.e(TAG, e.getMessage());
		}
		return files;
	}


	/**
	 * insatllPlugin
	 * @param name
	 */
	private void copyAndInstall(String name) {
		try {
			String tip = "";

			int stats = 0;
			String comandPath = PLUGIN_NAME + File.separator + name;
			InputStream assestInput = getAssets().open(comandPath);
			String dest = sdcard + File.separator + comandPath;

			if (FileUtil.copyFile(assestInput, dest)) {
				stats = PluginLoader.installPlugin(dest);
			} else {
				assestInput = getAssets().open(comandPath);
				dest = getCacheDir().getAbsolutePath() + File.separator + comandPath;
				if (FileUtil.copyFile(assestInput, dest)) {
					stats = PluginLoader.installPlugin(dest);

				} else {
					Toast.makeText(PluginListActivity.this, "解压Apk失败" + dest, Toast.LENGTH_LONG).show();
				}
			}

			switch (stats) {
				case 1:
					tip = "安装完成";
					break;
				case 2:
					tip = "复制安装包失败";
					break;
				case 3:
					tip = "签名无效";
					break;
				case 4:
					tip = "新旧插件版本签名不一致";
					break;
				case 5:
					tip = "解压失败";
					break;
				case 6:
					tip = "安装出错";
					break;
				case 7:
					tip = "正在安装 请无需重复操作";
					break;
				case 8:
					tip = "当前版本过低或者同个版本 无法覆盖";
					break;

			}
			Toast.makeText(PluginListActivity.this, tip, Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(PluginListActivity.this, "安装失败", Toast.LENGTH_LONG).show();
		}
	}

	private void listAll() {
		ViewGroup root = mList;
		root.removeAllViews();
		// 列出所有已经安装的插件
		Collection<PluginDescriptor> plugins = PluginLoader.getPlugins();
		Iterator<PluginDescriptor> itr = plugins.iterator();
		LayoutParams iconlayoutParam = new LayoutParams(300, 300);
		while (itr.hasNext()) {
			final PluginDescriptor pluginDescriptor = itr.next();
			ImageView icon = new ImageView(this);
			icon.setLayoutParams(iconlayoutParam);
			if (pluginDescriptor.getAppIcon() == null) {
				icon.setImageResource(pluginDescriptor.getApplicationIcon());
			} else {
				icon.setImageDrawable(BitmapUtil.getDrawableByByte(pluginDescriptor.getAppIcon()));
			}
			Button button = new Button(this);
			button.setPadding(10, 10, 10, 10);

			button.setText("插件：" + pluginDescriptor.getPluginName() + ", 插件id：" + pluginDescriptor.getPackageName() + "，单击启动, 长按卸载");
			button.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = null;
					String pluginId = pluginDescriptor.getPackageName();
					if (pluginId == null) {
						Toast.makeText(PluginListActivity.this, "缺少plugin_id参数", Toast.LENGTH_LONG).show();
						return;
					}
					String targitActivity = pluginDescriptor.getApkMainActivity();
					if (TextUtils.isEmpty(targitActivity)) {
						// 未知非独立插件启动插件详情
						intent = new Intent(PluginListActivity.this, PluginDetailActivity.class);
						intent.putExtra("plugin_id", pluginDescriptor.getPackageName());
					} else {
						// 已知插件apk信息情况下或独立插件 可指定启动的主Activity
						intent = new Intent();
						intent.setClassName(PluginListActivity.this, pluginDescriptor.getApkMainActivity());
						intent.putExtra("testParam", "testParam");
						intent.putExtra("paramVO", new SharePOJO("测试VO"));
					}
					startActivity(intent);
				}
			});

			button.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {

                    dialogShow(pluginDescriptor.getPluginID());

					return false;
				}

			});

			LayoutParams layoutParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layoutParam.topMargin = 10;
			layoutParam.bottomMargin = 10;
			layoutParam.leftMargin = 300;
			LayoutParams iconNewlayoutParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			iconlayoutParam.topMargin = 10;
			iconlayoutParam.bottomMargin = 10;
			layoutParam.leftMargin = 10;
			LinearLayout mPannel = new LinearLayout(this);
			mPannel.setOrientation(LinearLayout.HORIZONTAL);
			mPannel.setGravity(Gravity.CENTER);
			mPannel.addView(icon, iconNewlayoutParam);
			mPannel.addView(button, layoutParam);
			root.addView(mPannel, layoutParam);
		}
	}

	private final BroadcastReceiver pluginChange = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Toast.makeText(PluginListActivity.this,
					"插件"  + intent.getStringExtra("id") + " "+ intent.getStringExtra("type") + "完成",
					Toast.LENGTH_SHORT).show();
			listAll();
		};
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(pluginChange);

	}


	private void dialogShow(final String id) {

		new AlertDialog.Builder(this)
				.setTitle("拆卸插件")
				.setMessage("确定要卸载：" + id)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						unInstall(id);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

					}
				})
				.show();
	}




}
