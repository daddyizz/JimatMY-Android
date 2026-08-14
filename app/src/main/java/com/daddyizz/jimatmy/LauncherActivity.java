package com.daddyizz.jimatmy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentInformation.PrivacyOptionsRequirementStatus;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;

public class LauncherActivity extends Activity {
    private static final int FILE_CHOOSER_REQUEST_CODE = 1001;
    private static final int MENU_PRIVACY_POLICY = 1;
    private static final int MENU_AD_PRIVACY = 2;
    private static final int MENU_OPEN_BROWSER = 3;
    private static final int MENU_ABOUT = 4;
    private static final String INTERNAL_HOST = "jimatmy.lovable.app";
    private static final String HOME_URL = "https://jimatmy.lovable.app/?source=android";
    private static final String PRIVACY_URL = "https://jimatmy.lovable.app/privacy";
    private static final String ADMIN_URL = "https://jimatmy.lovable.app/admin/products";

    private WebView webView;
    private SwipeRefreshLayout swipeRefresh;
    private View offlineView;
    private FrameLayout adContainer;
    private AdView adView;
    private ConsentInformation consentInformation;
    private ValueCallback<Uri[]> fileChooserCallback;
    private boolean mobileAdsInitialized = false;
    private boolean isAdminPage = false;
    private boolean pageLoadFailed = false;
    private int adminTapCount = 0;
    private long adminTapWindowStartedAt = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configureSystemBars();
        bindViews();
        configureWebView();
        configureNativeActions();
        installAdminShortcut();
        requestAdConsent();

        if (savedInstanceState == null) {
            Uri deepLink = getIntent().getData();
            loadUrl(deepLink != null ? deepLink.toString() : getString(R.string.start_url));
        } else {
            webView.restoreState(savedInstanceState);
        }
    }

    private void bindViews() {
        webView = findViewById(R.id.web_view);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        offlineView = findViewById(R.id.offline_view);
        adContainer = findViewById(R.id.ad_view_container);
    }

    private void configureSystemBars() {
        View root = findViewById(R.id.root_layout);
        root.setOnApplyWindowInsetsListener((view, windowInsets) -> {
            int left;
            int top;
            int right;
            int bottom;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                android.graphics.Insets bars = windowInsets.getInsets(WindowInsets.Type.systemBars());
                left = bars.left;
                top = bars.top;
                right = bars.right;
                bottom = bars.bottom;
            } else {
                left = windowInsets.getSystemWindowInsetLeft();
                top = windowInsets.getSystemWindowInsetTop();
                right = windowInsets.getSystemWindowInsetRight();
                bottom = windowInsets.getSystemWindowInsetBottom();
            }
            view.setPadding(left, top, right, bottom);
            return windowInsets;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                                | WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                                | WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS);
            }
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                            | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
    }

    private void configureNativeActions() {
        swipeRefresh.setColorSchemeColors(Color.rgb(0, 155, 103));
        swipeRefresh.setOnRefreshListener(this::refreshPage);
        webView.setOnScrollChangeListener((view, x, y, oldX, oldY) ->
                swipeRefresh.setEnabled(y == 0));

        findViewById(R.id.retry_button).setOnClickListener(view -> retryConnection());
        findViewById(R.id.refresh_button).setOnClickListener(view -> refreshPage());
        findViewById(R.id.share_button).setOnClickListener(view -> shareCurrentPage());
        findViewById(R.id.menu_button).setOnClickListener(this::showAppMenu);
    }

    private void requestAdConsent() {
        consentInformation = UserMessagingPlatform.getConsentInformation(this);
        ConsentRequestParameters parameters = new ConsentRequestParameters.Builder()
                .setTagForUnderAgeOfConsent(false)
                .build();

        consentInformation.requestConsentInfoUpdate(
                this,
                parameters,
                () -> UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                        this,
                        formError -> {
                            if (consentInformation.canRequestAds()) {
                                initializeMobileAds();
                            }
                        }),
                requestConsentError -> {
                    if (consentInformation.canRequestAds()) {
                        initializeMobileAds();
                    }
                });

        if (consentInformation.canRequestAds()) {
            initializeMobileAds();
        }
    }

    private synchronized void initializeMobileAds() {
        if (mobileAdsInitialized) return;
        mobileAdsInitialized = true;
        new Thread(() -> MobileAds.initialize(this, initializationStatus ->
                runOnUiThread(this::createBanner))).start();
    }

    private void createBanner() {
        if (isFinishing() || isDestroyed() || adView != null) return;
        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.banner_ad_unit_id));
        adView.setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, getAdWidth()));
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                updateBannerVisibility();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                adContainer.setVisibility(View.GONE);
            }
        });
        adContainer.removeAllViews();
        adContainer.addView(
                adView,
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
        adView.loadAd(new AdRequest.Builder().build());
    }

    private void configureWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccess(false);
        settings.setMediaPlaybackRequiresUserGesture(true);
        settings.setSupportMultipleWindows(false);
        settings.setUserAgentString(settings.getUserAgentString() + " JimatMYAndroid/1.2");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            settings.setSafeBrowsingEnabled(true);
        }

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);

        webView.setBackgroundColor(Color.rgb(247, 249, 246));
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return handleNavigation(request.getUrl());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return handleNavigation(Uri.parse(url));
            }

            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                pageLoadFailed = false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                swipeRefresh.setRefreshing(false);
                if (!pageLoadFailed) hideOffline();
                isAdminPage = isAdminUrl(url);
                updateBannerVisibility();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (request.isForMainFrame()) {
                    pageLoadFailed = true;
                    swipeRefresh.setRefreshing(false);
                    showOffline();
                }
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(
                    WebView view,
                    ValueCallback<Uri[]> newCallback,
                    FileChooserParams fileChooserParams) {
                if (fileChooserCallback != null) {
                    fileChooserCallback.onReceiveValue(null);
                }
                fileChooserCallback = newCallback;
                try {
                    startActivityForResult(fileChooserParams.createIntent(), FILE_CHOOSER_REQUEST_CODE);
                    return true;
                } catch (ActivityNotFoundException exception) {
                    fileChooserCallback = null;
                    Toast.makeText(LauncherActivity.this, R.string.no_file_picker, Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        });

        webView.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength) ->
                openExternal(Uri.parse(url)));
    }

    private void installAdminShortcut() {
        webView.setOnTouchListener((view, event) -> {
            if (event.getAction() != MotionEvent.ACTION_UP) return false;
            boolean tappedLogoArea = event.getX() <= view.getWidth() * 0.55f
                    && event.getY() <= dpToPixels(130);
            if (!tappedLogoArea) {
                adminTapCount = 0;
                return false;
            }

            long now = SystemClock.elapsedRealtime();
            if (now - adminTapWindowStartedAt > 3500L) {
                adminTapCount = 0;
                adminTapWindowStartedAt = now;
            }
            adminTapCount++;
            if (adminTapCount >= 5) {
                adminTapCount = 0;
                Toast.makeText(this, R.string.opening_admin, Toast.LENGTH_SHORT).show();
                loadUrl(ADMIN_URL);
            }
            return false;
        });
    }

    private boolean handleNavigation(Uri uri) {
        String scheme = uri.getScheme();
        String host = uri.getHost();
        if (("https".equalsIgnoreCase(scheme) || "http".equalsIgnoreCase(scheme))
                && INTERNAL_HOST.equalsIgnoreCase(host)) {
            return false;
        }
        openExternal(uri);
        return true;
    }

    private void loadUrl(String url) {
        isAdminPage = isAdminUrl(url);
        updateBannerVisibility();
        if (isConnected()) {
            hideOffline();
            webView.loadUrl(url);
        } else {
            showOffline();
        }
    }

    private void refreshPage() {
        swipeRefresh.setRefreshing(true);
        if (isConnected()) {
            hideOffline();
            webView.reload();
        } else {
            swipeRefresh.setRefreshing(false);
            showOffline();
        }
    }

    private void retryConnection() {
        if (isConnected()) {
            hideOffline();
            String current = webView.getUrl();
            webView.loadUrl(current == null ? HOME_URL : current);
        } else {
            Toast.makeText(this, R.string.offline_message, Toast.LENGTH_SHORT).show();
        }
    }

    private void showOffline() {
        offlineView.setVisibility(View.VISIBLE);
        adContainer.setVisibility(View.GONE);
        if (adView != null) adView.pause();
    }

    private void hideOffline() {
        offlineView.setVisibility(View.GONE);
    }

    private boolean isConnected() {
        ConnectivityManager manager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) return false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = manager.getActiveNetwork();
            if (network == null) return false;
            NetworkCapabilities capabilities = manager.getNetworkCapabilities(network);
            return capabilities != null
                    && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        }
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    private void shareCurrentPage() {
        String url = webView.getUrl();
        if (url == null || isAdminUrl(url)) url = HOME_URL;
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "JimatMY");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + " " + url);
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_with)));
    }

    private void showAppMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenu().add(0, MENU_PRIVACY_POLICY, 0, R.string.privacy_policy);
        if (consentInformation != null
                && consentInformation.getPrivacyOptionsRequirementStatus()
                == PrivacyOptionsRequirementStatus.REQUIRED) {
            popup.getMenu().add(0, MENU_AD_PRIVACY, 1, R.string.ad_privacy);
        }
        popup.getMenu().add(0, MENU_OPEN_BROWSER, 2, R.string.open_browser);
        popup.getMenu().add(0, MENU_ABOUT, 3, R.string.about_app);
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == MENU_PRIVACY_POLICY) {
                loadUrl(PRIVACY_URL);
                return true;
            }
            if (item.getItemId() == MENU_AD_PRIVACY) {
                showAdPrivacyOptions();
                return true;
            }
            if (item.getItemId() == MENU_OPEN_BROWSER) {
                String url = webView.getUrl();
                openExternal(Uri.parse(url == null || isAdminUrl(url) ? HOME_URL : url));
                return true;
            }
            if (item.getItemId() == MENU_ABOUT) {
                showAboutDialog();
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void showAdPrivacyOptions() {
        if (consentInformation == null
                || consentInformation.getPrivacyOptionsRequirementStatus()
                != PrivacyOptionsRequirementStatus.REQUIRED) {
            Toast.makeText(this, R.string.privacy_not_required, Toast.LENGTH_SHORT).show();
            return;
        }
        UserMessagingPlatform.showPrivacyOptionsForm(this, formError -> {
            if (consentInformation.canRequestAds()) initializeMobileAds();
        });
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.about_app)
                .setMessage(getString(R.string.about_message, getAppVersion()))
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private String getAppVersion() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException exception) {
            return "1.2.0";
        }
    }

    private void openExternal(Uri uri) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (ActivityNotFoundException exception) {
            Toast.makeText(this, R.string.no_app_for_link, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isAdminUrl(String url) {
        Uri uri = Uri.parse(url);
        String path = uri.getPath();
        return path != null && (path.equals("/admin") || path.startsWith("/admin/"));
    }

    private void updateBannerVisibility() {
        boolean offline = offlineView != null && offlineView.getVisibility() == View.VISIBLE;
        if (adView == null || isAdminPage || offline) {
            adContainer.setVisibility(View.GONE);
            if (adView != null) adView.pause();
        } else {
            adContainer.setVisibility(View.VISIBLE);
            adView.resume();
        }
    }

    private int getAdWidth() {
        return (int) (getResources().getDisplayMetrics().widthPixels
                / getResources().getDisplayMetrics().density);
    }

    private int dpToPixels(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_REQUEST_CODE && fileChooserCallback != null) {
            Uri[] result = WebChromeClient.FileChooserParams.parseResult(resultCode, data);
            fileChooserCallback.onReceiveValue(result);
            fileChooserCallback = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        webView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (offlineView.getVisibility() == View.VISIBLE) {
            hideOffline();
        } else if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        if (adView != null) adView.pause();
        if (webView != null) webView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) webView.onResume();
        if (adView != null && !isAdminPage) adView.resume();
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
            adView = null;
        }
        if (webView != null) {
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
