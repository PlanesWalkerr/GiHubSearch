package com.makhovyk.android.githubsearch.View;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.makhovyk.android.githubsearch.Presenter.LoginPresenterImpl;
import com.makhovyk.android.githubsearch.R;
import com.makhovyk.android.githubsearch.Utils.SessionManager;


public class LoginFragment extends Fragment implements LoginView{

    String OAUTH_URL = "https://github.com/login/oauth/authorize";
    String CLIENT_ID = "260a01d3966085fdefb4";
    String CLIENT_SECRET = "3fc85da7cf473b0a2624ea95b64019e4238eee21";
    String CALLBACK_URL = "githubsearch://callback";

    private WebView mWebView;
    private SessionManager mSessionManager;
    private LoginPresenterImpl mLoginPresenter;
    private OnUserDataUpdated callback;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSessionManager = new SessionManager(getActivity().getApplicationContext());
        mLoginPresenter = new LoginPresenterImpl(this,mSessionManager);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        String url = OAUTH_URL + "?client_id=" + CLIENT_ID + "&scope=repo&redirect_uri=" + CALLBACK_URL;

        mWebView = (WebView)v.findViewById(R.id.login_web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAppCacheEnabled(false);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        CookieSyncManager.createInstance(getActivity());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                if (url.startsWith(CALLBACK_URL)) {
                    Uri uri = Uri.parse(url);
                    String code = uri.getQueryParameter("code");
                    mSessionManager.setAccessCode(code);
                    mLoginPresenter.getAccessToken(code);

                }else {
                    view.loadUrl(url);
                }

                return true;
            }
        });
        mWebView.loadUrl(url);

        return v;
    }


    @Override
    public void navigateToUserRepos() {
        callback.onUserFetched();
    }

    public interface OnUserDataUpdated {
        public void onUserFetched();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (OnUserDataUpdated) context;
    }
}
