package com.example.turtlejk.myapplication.util;

import java.io.File;

import com.example.turtlejk.myapplication.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import cz.msebera.android.httpclient.Header;

public class FileUploadActivity extends AppCompatActivity {

    private static final int FILE_SELECT_CODE = 0;

    protected static final String TAG = "QL";

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                String path = (String) msg.obj;
                editText.setText(path);
            }
            if (msg.what == 2) {
                int progress = (int) msg.arg1;
                FileUploadActivity.this.progressBar.setProgress(progress);
                int a = msg.arg2;
                textView.setText(a + "%");
            }
            if (msg.what == 3) {
                String string = (String) msg.obj;
                textView2.setText("" + string);
            }
        }
    };

    private EditText editText;
    private ProgressBar progressBar;
    private TextView textView;

    private TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fileupload);

        editText = (EditText) findViewById(R.id.editText1);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        textView = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);

        Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                UpLoad();
            }
        });
    }

    /**
     * 上传
     */
    protected void UpLoad() {
        progressBar.setProgress(0);
        AsyncHttpClient client = new AsyncHttpClient();//实例化上传对象
        String url = "";//url组成：ip:端口 + 服务端工程名 + servlet名
        String path = textView.getText().toString().trim();

        if (null != path && "" != path) {
            File file = new File(path);
            if (file.exists() && file.length() > 0) {
                RequestParams params = new RequestParams();
                try {
                    params.put("profile", file);//将文件加入参数
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //上传文件
                client.post(url, params, new AsyncHttpResponseHandler() {

                    @Override//失败的监听
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Message msg = handler.obtainMessage();
                        msg.what = 3;
                        msg.obj = "上传失败！";
                        handler.sendMessage(msg);
                        error.printStackTrace();
                    }

                    @Override//成功的监听
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Message msg = handler.obtainMessage();
                        msg.what = 3;
                        msg.obj = "上传成功！";
                        handler.sendMessage(msg);
                    }

                    @Override//动态变化
                    public void onProgress(final long bytesWritten, final long totalSize) {
                        super.onProgress(bytesWritten, totalSize);
                        progressBar.setMax((int) totalSize);
                        float a = (float) bytesWritten / (float) totalSize;
                        Message msg = handler.obtainMessage();
                        msg.what = 2;
                        msg.arg1 = (int) bytesWritten;
                        msg.arg2 = (int) (a * 100f);
                        handler.sendMessage(msg);
                    }
                });
            }

        }
    }

    /**
     * 选择文件
     */
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//过滤文件类型（所有）
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "请选择文件！"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "未安装文件管理器！", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_SELECT_CODE) {
            if (resultCode == RESULT_OK) {
                System.out.println("-----------");
                Uri uri = data.getData();
                System.out.println("-----------" + uri.toString());
                GetPathFromUri4kitkat getPathFromUri = new GetPathFromUri4kitkat();
                String path = getPathFromUri.getPath(FileUploadActivity.this,uri);
                System.out.println("-----------" + path);
                String type = getExtensionName(path);
                System.out.println("-----------" + type);
                textView.setText(path);
                Message msg = handler.obtainMessage();
                msg.what = 1;
                msg.obj = path;
                handler.sendMessage(msg);
            }
        }
    }

    public String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

}
