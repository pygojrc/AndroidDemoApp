package io.bloxorz.mobile;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import fi.iki.elonen.NanoHTTPD;

public final class MainActivity extends Activity {
    private static final String TARGET_ORIGIN = "https://bloxorz.io";
    private static final String LOCAL_ORIGIN = "http://127.0.0.1:8765";

    private WebView webView;
    private ProxyServer proxy;

    private final Map<Integer, Boolean> keyDownSent = new HashMap<>();
    private final Map<Integer, Boolean> keyUpPending = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        hideSystemUi();

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.BLACK);
        setContentView(root);

        webView = new WebView(this);
        webView.setBackgroundColor(Color.BLACK);
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        root.addView(webView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        configureWebView();
        addTouchControls(root);

        proxy = new ProxyServer();
        try {
            proxy.start(5000, false);
            webView.loadUrl(LOCAL_ORIGIN + "/");
        } catch (IOException e) {
            webView.loadData(
                    "<h2>Proxy failed to start</h2><pre>" + escapeHtml(e.toString()) + "</pre>",
                    "text/html",
                    "utf-8");
        }
    }

    private void configureWebView() {
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setAllowContentAccess(true);
        s.setAllowFileAccess(false);
        s.setLoadsImagesAutomatically(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setUseWideViewPort(true);
        s.setLoadWithOverviewMode(true);
        s.setBuiltInZoomControls(false);
        s.setDisplayZoomControls(false);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);

        CookieManager cookies = CookieManager.getInstance();
        cookies.setAcceptCookie(true);
        cookies.setAcceptThirdPartyCookies(webView, true);

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (!request.isForMainFrame()) return false;

                String url = request.getUrl().toString();
                if (url.startsWith(LOCAL_ORIGIN)) return false;

                if (url.startsWith(TARGET_ORIGIN + "/")) {
                    view.loadUrl(LOCAL_ORIGIN + url.substring(TARGET_ORIGIN.length()));
                    return true;
                }

                // This wrapper intentionally stays on Bloxorz.
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                view.requestFocus();
            }
        });
    }

    private void addTouchControls(FrameLayout root) {
        int size = dp(58);
        int gap = dp(8);
        int pad = dp(14);

        FrameLayout dpad = new FrameLayout(this);
        FrameLayout.LayoutParams dpadLp =
                new FrameLayout.LayoutParams(size * 3 + gap * 2, size * 3 + gap * 2);
        dpadLp.gravity = Gravity.BOTTOM | Gravity.START;
        dpadLp.leftMargin = pad;
        dpadLp.bottomMargin = pad;
        root.addView(dpad, dpadLp);

        addKeyButton(dpad, "▲", KeyEvent.KEYCODE_DPAD_UP,
                size + gap, 0, size, size);
        addKeyButton(dpad, "◀", KeyEvent.KEYCODE_DPAD_LEFT,
                0, size + gap, size, size);
        addKeyButton(dpad, "▼", KeyEvent.KEYCODE_DPAD_DOWN,
                size + gap, size + gap, size, size);
        addKeyButton(dpad, "▶", KeyEvent.KEYCODE_DPAD_RIGHT,
                (size + gap) * 2, size + gap, size, size);

        TextView space = makeButton("SPACE");
        FrameLayout.LayoutParams sp = new FrameLayout.LayoutParams(dp(118), size);
        sp.gravity = Gravity.BOTTOM | Gravity.END;
        sp.rightMargin = pad;
        sp.bottomMargin = pad;
        root.addView(space, sp);
        bindKey(space, KeyEvent.KEYCODE_SPACE);
    }

    private void addKeyButton(
            FrameLayout parent,
            String label,
            int keyCode,
            int left,
            int top,
            int width,
            int height) {
        TextView button = makeButton(label);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, height);
        lp.leftMargin = left;
        lp.topMargin = top;
        parent.addView(button, lp);
        bindKey(button, keyCode);
    }

    private TextView makeButton(String label) {
        TextView v = new TextView(this);
        v.setText(label);
        v.setTextColor(Color.WHITE);
        v.setTextSize(18);
        v.setGravity(Gravity.CENTER);
        v.setClickable(true);
        v.setFocusable(false);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0x99000000);
        bg.setStroke(dp(1), 0xCCFFFFFF);
        bg.setCornerRadius(dp(12));
        v.setBackground(bg);
        return v;
    }

    private void bindKey(View button, int keyCode) {
        button.setOnTouchListener((v, event) -> {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    v.setAlpha(0.60f);
                    keyDownSent.put(keyCode, false);
                    keyUpPending.put(keyCode, false);
                    focusLargestIframeThenSendDown(keyCode);
                    return true;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.setAlpha(1.0f);
                    if (Boolean.TRUE.equals(keyDownSent.get(keyCode))) {
                        sendKey(KeyEvent.ACTION_UP, keyCode);
                        keyDownSent.put(keyCode, false);
                    } else {
                        keyUpPending.put(keyCode, true);
                    }
                    return true;

                default:
                    return true;
            }
        });
    }

    private void focusLargestIframeThenSendDown(int keyCode) {
        webView.evaluateJavascript(
                "(function(){"
                        + "var a=[].slice.call(document.querySelectorAll('iframe'));"
                        + "var best=null,area=0;"
                        + "a.forEach(function(f){"
                        + "var r=f.getBoundingClientRect();"
                        + "var x=Math.max(0,r.width)*Math.max(0,r.height);"
                        + "if(x>area){area=x;best=f;}"
                        + "});"
                        + "if(best){try{best.focus();}catch(e){}}"
                        + "return !!best;"
                        + "})();",
                value -> {
                    webView.requestFocus();
                    sendKey(KeyEvent.ACTION_DOWN, keyCode);
                    keyDownSent.put(keyCode, true);

                    if (Boolean.TRUE.equals(keyUpPending.get(keyCode))) {
                        sendKey(KeyEvent.ACTION_UP, keyCode);
                        keyDownSent.put(keyCode, false);
                        keyUpPending.put(keyCode, false);
                    }
                });
    }

    private void sendKey(int action, int keyCode) {
        long now = SystemClock.uptimeMillis();
        KeyEvent event = new KeyEvent(
                now,
                now,
                action,
                keyCode,
                0,
                0,
                0,
                0,
                KeyEvent.FLAG_VIRTUAL_HARD_KEY,
                android.view.InputDevice.SOURCE_KEYBOARD);
        webView.dispatchKeyEvent(event);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUi();
        if (webView != null) webView.onResume();
    }

    @Override
    protected void onPause() {
        if (webView != null) webView.onPause();
        CookieManager.getInstance().flush();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (proxy != null) proxy.stop();

        if (webView != null) {
            webView.loadUrl("about:blank");
            webView.stopLoading();
            webView.destroy();
        }

        super.onDestroy();
    }

    private void hideSystemUi() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private static String escapeHtml(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static final class ProxyServer extends NanoHTTPD {
        private static final Set<String> HOP_BY_HOP = new HashSet<>();

        static {
            String[] names = {
                    "connection",
                    "keep-alive",
                    "proxy-authenticate",
                    "proxy-authorization",
                    "te",
                    "trailers",
                    "transfer-encoding",
                    "upgrade",
                    "host",
                    "content-length"
            };
            for (String name : names) HOP_BY_HOP.add(name);
        }

        ProxyServer() {
            super("127.0.0.1", 8765);
        }

        @Override
        public Response serve(IHTTPSession session) {
            try {
                String query = session.getQueryParameterString();
                String upstream =
                        TARGET_ORIGIN
                                + session.getUri()
                                + (query == null || query.isEmpty() ? "" : "?" + query);
                return proxy(session, upstream);
            } catch (Throwable t) {
                return newFixedLengthResponse(
                        Response.Status.INTERNAL_ERROR,
                        "text/plain; charset=utf-8",
                        "Bloxorz proxy error\n\n"
                                + t.getClass().getSimpleName()
                                + ": "
                                + String.valueOf(t.getMessage()));
            }
        }

        private Response proxy(IHTTPSession session, String upstreamUrl) throws Exception {
            HttpURLConnection conn =
                    (HttpURLConnection) new URL(upstreamUrl).openConnection();

            conn.setInstanceFollowRedirects(false);
            conn.setConnectTimeout(15_000);
            conn.setReadTimeout(30_000);
            conn.setRequestMethod(session.getMethod().name());
            conn.setRequestProperty("Accept-Encoding", "identity");

            for (Map.Entry<String, String> h : session.getHeaders().entrySet()) {
                String name = h.getKey();
                if (name == null
                        || HOP_BY_HOP.contains(name.toLowerCase(Locale.ROOT))) {
                    continue;
                }
                conn.setRequestProperty(name, h.getValue());
            }

            if (allowsRequestBody(session.getMethod())) {
                Map<String, String> files = new HashMap<>();
                session.parseBody(files);
                String postData = files.get("postData");

                if (postData != null) {
                    byte[] body = postData.getBytes(StandardCharsets.UTF_8);
                    conn.setDoOutput(true);
                    conn.setFixedLengthStreamingMode(body.length);
                    conn.getOutputStream().write(body);
                }
            }

            int code = conn.getResponseCode();
            String contentType = conn.getContentType();
            if (contentType == null) contentType = "application/octet-stream";

            InputStream raw;
            try {
                raw = code >= 400 ? conn.getErrorStream() : conn.getInputStream();
            } catch (IOException e) {
                raw = conn.getErrorStream();
            }

            byte[] bytes = raw == null ? new byte[0] : readAll(raw);

            if (isText(contentType)) {
                String text = new String(bytes, detectCharset(contentType));

                if (contentType.toLowerCase(Locale.ROOT).contains("text/html")) {
                    text = rewriteHtml(text);
                } else if (contentType.toLowerCase(Locale.ROOT).contains("text/css")) {
                    text = rewriteCss(text);
                }

                bytes = text.getBytes(StandardCharsets.UTF_8);
                contentType = normalizeUtf8(contentType);
            }

            Response response = newFixedLengthResponse(
                    statusFor(code),
                    contentType,
                    new ByteArrayInputStream(bytes),
                    bytes.length);

            Map<String, List<String>> headers = conn.getHeaderFields();
            if (headers != null) {
                for (Map.Entry<String, List<String>> e : headers.entrySet()) {
                    String name = e.getKey();
                    if (name == null) continue;

                    String lower = name.toLowerCase(Locale.ROOT);

                    if (HOP_BY_HOP.contains(lower)
                            || lower.equals("content-encoding")
                            || lower.equals("content-length")) {
                        continue;
                    }

                    if (lower.equals("location")) {
                        String value = first(e.getValue());
                        if (value != null) {
                            response.addHeader(
                                    "Location",
                                    rewriteLocation(upstreamUrl, value));
                        }
                        continue;
                    }

                    if (lower.equals("set-cookie")) {
                        if (e.getValue() != null) {
                            for (String cookie : e.getValue()) {
                                persistCookieToLocal(cookie);
                            }
                        }
                        continue;
                    }

                    if (lower.equals("content-security-policy")
                            || lower.equals("content-security-policy-report-only")
                            || lower.equals("x-frame-options")) {
                        continue;
                    }

                    String value = first(e.getValue());
                    if (value != null) response.addHeader(name, value);
                }
            }

            response.addHeader("Cache-Control", "no-transform");
            return response;
        }

        private static boolean allowsRequestBody(Method method) {
            String name = method.name();
            return "POST".equals(name)
                    || "PUT".equals(name)
                    || "PATCH".equals(name);
        }

        private static String rewriteHtml(String html) {
            String out = html
                    .replace("https://bloxorz.io/", LOCAL_ORIGIN + "/")
                    .replace(
                            "https:\\/\\/bloxorz.io\\/",
                            "http:\\/\\/127.0.0.1:8765\\/");

            String lower = out.toLowerCase(Locale.ROOT);
            if (!lower.contains("name=\"viewport\"")
                    && !lower.contains("name='viewport'")) {
                String meta =
                        "<meta name=\"viewport\" "
                                + "content=\"width=device-width,initial-scale=1,"
                                + "maximum-scale=1,user-scalable=no\">";

                int head = lower.indexOf("<head>");
                if (head >= 0) {
                    out = out.substring(0, head + 6)
                            + meta
                            + out.substring(head + 6);
                }
            }

            return out;
        }

        private static String rewriteCss(String css) {
            return css.replace("https://bloxorz.io/", LOCAL_ORIGIN + "/");
        }

        private static String rewriteLocation(String baseUrl, String value) {
            try {
                URI absolute = URI.create(baseUrl).resolve(value);

                if ("bloxorz.io".equalsIgnoreCase(absolute.getHost())) {
                    String path = absolute.getRawPath();
                    if (path == null || path.isEmpty()) path = "/";

                    String query = absolute.getRawQuery();
                    return LOCAL_ORIGIN
                            + path
                            + (query == null ? "" : "?" + query);
                }
            } catch (Throwable ignored) {
            }

            return value;
        }

        private static void persistCookieToLocal(String cookie) {
            if (cookie == null || cookie.isEmpty()) return;

            String rewritten = cookie
                    .replaceAll("(?i);\\s*Domain=[^;]+", "")
                    .replaceAll("(?i);\\s*Secure", "");

            CookieManager cookies = CookieManager.getInstance();
            cookies.setCookie(LOCAL_ORIGIN, rewritten);
            cookies.flush();
        }

        private static boolean isText(String type) {
            String t = type.toLowerCase(Locale.ROOT);
            return t.startsWith("text/")
                    || t.contains("javascript")
                    || t.contains("json")
                    || t.contains("xml")
                    || t.contains("svg");
        }

        private static Charset detectCharset(String type) {
            try {
                for (String part : type.split(";")) {
                    String p = part.trim();
                    if (p.toLowerCase(Locale.ROOT).startsWith("charset=")) {
                        return Charset.forName(p.substring(8).trim());
                    }
                }
            } catch (Throwable ignored) {
            }
            return StandardCharsets.UTF_8;
        }

        private static String normalizeUtf8(String type) {
            return type.split(";")[0].trim() + "; charset=utf-8";
        }

        private static byte[] readAll(InputStream input) throws IOException {
            try (InputStream in = input;
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[16 * 1024];
                int n;

                while ((n = in.read(buffer)) >= 0) {
                    out.write(buffer, 0, n);
                }

                return out.toByteArray();
            }
        }

        private static String first(List<String> values) {
            return values == null || values.isEmpty() ? null : values.get(0);
        }

        private static Response.IStatus statusFor(final int code) {
            for (Response.Status status : Response.Status.values()) {
                if (status.getRequestStatus() == code) return status;
            }

            return new Response.IStatus() {
                @Override
                public String getDescription() {
                    return Integer.toString(code);
                }

                @Override
                public int getRequestStatus() {
                    return code;
                }
            };
        }
    }
}
