package com.example.downloadfileopentbswebview;

import android.app.Application;
import android.util.Log;

import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;

import java.util.HashMap;

public class TBSApplication extends Application {

    private String TAG = "TBSApplication==";

    @Override
    public void onCreate() {
        super.onCreate();
        initTBSWebView();
    }

    private void initTBSWebView() {
        // 在调用TBS初始化、创建WebView之前进行如下配置
        HashMap map = new HashMap();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);

        QbSdk.initX5Environment(this, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                Log.e(TAG, "onCoreInitFinished=");
            }

            @Override
            public void onViewInitFinished(boolean b) {
                Log.e(TAG, "onViewInitFinished=" + b);
            }
        });

        QbSdk.setTbsListener(new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {
                //tbs内核下载完成回调
                Log.e(TAG, "onDownloadFinish="+i);
            }

            @Override
            public void onInstallFinish(int i) {
                //内核安装完成回调，
                Log.e(TAG, "onInstallFinish="+i);
            }

            @Override
            public void onDownloadProgress(int i) {
                //下载进度监听
                Log.e(TAG, "onDownloadProgress="+i);
            }
        });
    }
}
