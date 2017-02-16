package com.example.turtlejk.myapplication.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.example.turtlejk.myapplication.R;

public class TreeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.tree_fragment, container, false);
        LinearLayout tosteppicture = (LinearLayout) layout.findViewById(R.id.tosteppicture);
        tosteppicture.setOnClickListener(tostepOnClick);
        WebView webView = (WebView) layout.findViewById(R.id.tree_webview);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        WebSettings webSettings = webView.getSettings();
        // 支持缩放(适配到当前屏幕)
        webSettings.setSupportZoom(true);
        // 将图片调整到合适的大小
        webSettings.setUseWideViewPort(true);
        //支持缩放
        webSettings.setBuiltInZoomControls(true);

        webView.loadUrl("http://www.weibo.com/");
        return layout;
    }

    View.OnClickListener tostepOnClick = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            FragmentsActivity factivity = (FragmentsActivity) getActivity();
            factivity.vpager.setCurrentItem(factivity.PAGE_STEP);
        }
    };

}
