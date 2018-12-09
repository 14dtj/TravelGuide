package cn.edu.sjtu.travelguide.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.sjtu.travelguide.R;

public class RecommendDetailFragment extends BaseFragment {
    @BindView(R.id.webview)
    WebView webView;
    private String url;

    @Override
    protected View onCreateView() {
        FrameLayout layout = (FrameLayout) LayoutInflater.from(getActivity()).inflate(R.layout.fragment_recommend_detail, null);
        ButterKnife.bind(this, layout);
        Bundle bundle = getArguments();
        url = bundle.getString("url");
        webView.loadUrl(url);
        return layout;
    }
}