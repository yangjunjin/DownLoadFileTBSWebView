package com.example.downloadfileopentbswebview;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.downloadfileopentbswebview.TBSWebView.DownloadUtil;
import com.example.downloadfileopentbswebview.TBSWebView.FileUtil;
import com.example.downloadfileopentbswebview.TBSWebView.FileVo;
import com.example.downloadfileopentbswebview.TBSWebView.RxUtils;
import com.example.downloadfileopentbswebview.TBSWebView.WebViewInitUtil;
import com.tencent.smtt.sdk.TbsReaderView;
import com.tencent.smtt.sdk.WebView;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
//
public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private String TAG = "MainActivity===";
    private DownloadUtil downloadUtil;
    private RelativeLayout root;
    private TbsReaderView mTbsReaderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webView);
        root = findViewById(R.id.root);
        mTbsReaderView = new TbsReaderView(this, readerCallback);
        root.addView(mTbsReaderView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        WebViewInitUtil webViewInitUtil = new WebViewInitUtil(this);
        webViewInitUtil.initWebView(webView);
        downloadUtil = new DownloadUtil();
        downLoadFile();
    }

    @SuppressLint("CheckResult")
    private void downLoadFile() {
        Observable.create(new ObservableOnSubscribe<FileVo>() {
            @Override
            public void subscribe(final ObservableEmitter<FileVo> e) throws Exception {
                final FileVo fileVo = new FileVo();
                String officeUrl = "https://css4.pub/2015/textbook/somatosensory.pdf", officeSaveName = "somatosensory.pdf";
                String path = FileUtil.getCachePath(MainActivity.this);
                downloadUtil.download(officeUrl, path, officeSaveName, new DownloadUtil.OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess(File file) {
                        fileVo.setFile(file);
                        e.onNext(fileVo);
                        e.onComplete();
                    }

                    @Override
                    public void onDownloading(int progress) {
                        //showProgress(progress);
                        Log.e(TAG, "progress=" + progress);
                    }

                    @Override
                    public void onDownloadFailed(Exception e) {
                    }
                });
            }

        }).compose(RxUtils.schedulersTransformer()).subscribe(new Consumer<FileVo>() {
            @Override
            public void accept(FileVo fileVo) {
                openFile(fileVo.getFile().getAbsolutePath());
                Log.e(TAG, "accept=" + fileVo.getFile().getAbsolutePath());
            }
        });
    }

    private void openFile(String url) {
        File file = new File(url);
        if (!file.exists()) {
            Toast.makeText(this, "???????????????", Toast.LENGTH_LONG).show();
        }
        Bundle bundle = new Bundle();
        bundle.putString("filePath", url);
        bundle.putString("tempPath", url);
        boolean result = mTbsReaderView.preOpen(parseFormat(parseName(url)), false);
        if (result) {
            mTbsReaderView.openFile(bundle);
        }
    }

    private String parseFormat(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private String parseName(String url) {
        String fileName = null;
        try {
            fileName = url.substring(url.lastIndexOf("/") + 1);
        } finally {
            if (TextUtils.isEmpty(fileName)) {
                fileName = String.valueOf(System.currentTimeMillis());
            }
        }
        return fileName;
    }

    TbsReaderView.ReaderCallback readerCallback = (integer, o, o1) -> {

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //?????????????????????????????????????????????????????????????????????????????????
        mTbsReaderView.onStop();
    }
}