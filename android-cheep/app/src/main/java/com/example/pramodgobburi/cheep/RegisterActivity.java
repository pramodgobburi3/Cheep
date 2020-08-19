package com.example.pramodgobburi.cheep;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.example.pramodgobburi.cheep.Models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import uk.me.hardill.volley.multipart.MultipartRequest;

public class RegisterActivity extends AppCompatActivity {

    private RelativeLayout registerLayout;
    private Button registerSubmit;
    private EditText username;
    private EditText password;
    private EditText email;
    private EditText firstname;
    private EditText lastname;
    private EditText confirmPassword;
    private TextView login;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        requestQueue = Volley.newRequestQueue(this);

        registerLayout = (RelativeLayout) findViewById(R.id.register_layout);

        registerSubmit = (Button) findViewById(R.id.register_submit);
        username = (EditText) findViewById(R.id.register_username);
        password = (EditText) findViewById(R.id.register_password);
        confirmPassword = (EditText) findViewById(R.id.register_confirm_password);
        email = (EditText) findViewById(R.id.register_email);
        firstname = (EditText) findViewById(R.id.register_firstname);
        lastname = (EditText) findViewById(R.id.register_lastname);
        login = (TextView) findViewById(R.id.register_login);

        registerSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(password.getText().toString().equals(confirmPassword.getText().toString())) {
                    if(username.getText().toString().equals("") || password.getText().toString().equals("") || confirmPassword.getText().toString().equals("")
                            || email.getText().toString().equals("") || firstname.getText().toString().equals("") || lastname.getText().toString().equals("")) {
                        Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        register(firstname.getText().toString(), lastname.getText().toString(), email.getText().toString(),
                                username.getText().toString(), password.getText().toString());
                    }
                }

            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toLoginActivity();
            }
        });

    }

    private void register(String firstname, String lastname, String email, String username, String password) {
        Log.e("REGISTER", "Reached register method");
        MultipartRequest registerRequest = new MultipartRequest(
                getResources().getString(R.string.register_url),
                null,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        JSONObject jsonObject;
                        String json = "";
                        try {
                            json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                            jsonObject = new JSONObject(json);

                            String status = jsonObject.getString("status");

                            if (status.equals(getResources().getString(R.string.status_success)))   {
                                String accessToken = jsonObject.getString("Access_Token");
                                initializeLink(accessToken);

                            }
                            else if (status.equals(getResources().getString(R.string.status_expired_token)))    {
                                Toast.makeText(RegisterActivity.this, status, Toast.LENGTH_SHORT).show();
                            }
                            else if(status.equals(getResources().getString(R.string.status_invalid_token)))   {
                                Toast.makeText(RegisterActivity.this, status, Toast.LENGTH_SHORT).show();
                            }
                            else if (status.equals(getResources().getString(R.string.status_not_provided))) {
                                Toast.makeText(RegisterActivity.this, status, Toast.LENGTH_SHORT).show();
                            }
                            else    {
                                Log.e("LOGINACTIVITY", "An Error Occurred");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        registerRequest.addPart(new MultipartRequest.FormPart("firstname", firstname));
        registerRequest.addPart(new MultipartRequest.FormPart("lastname", lastname));
        registerRequest.addPart(new MultipartRequest.FormPart("email", email));
        registerRequest.addPart(new MultipartRequest.FormPart("username", username));
        registerRequest.addPart(new MultipartRequest.FormPart("password", password));

        requestQueue.add(registerRequest);
    }

    private void toLoginActivity() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void initializeLink(final String accessToken) {
        HashMap<String, String> linkInitializeOptions = new HashMap<String,String>();
        linkInitializeOptions.put("key", getResources().getString(R.string.plaid_public_key));
        linkInitializeOptions.put("product", "auth");
        linkInitializeOptions.put("apiVersion", "v2"); // set this to "v1" if using the legacy Plaid API
        linkInitializeOptions.put("env", "sandbox");
        linkInitializeOptions.put("clientName", "Test App");
        linkInitializeOptions.put("selectAccount", "true");
        linkInitializeOptions.put("webhook", "http://requestb.in");
        linkInitializeOptions.put("baseUrl", "https://cdn.plaid.com/link/v2/stable/link.html");
        // If initializing Link in PATCH / update mode, also provide the public_token
        // linkInitializeOptions.put("token", "PUBLIC_TOKEN")

        // Generate the Link initialization URL based off of the configuration options.
        final Uri linkInitializationUrl = generateLinkInitializationUrl(linkInitializeOptions);

        // Modify Webview settings - all of these settings may not be applicable
        // or necessary for your integration.
        final WebView plaidLinkWebview = (WebView) findViewById(R.id.web_view);
        WebSettings webSettings = plaidLinkWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        WebView.setWebContentsDebuggingEnabled(true);
        registerLayout.setVisibility(View.GONE);
        plaidLinkWebview.setVisibility(View.VISIBLE);

        // Initialize Link by loading the Link initialization URL in the Webview
        plaidLinkWebview.loadUrl(linkInitializationUrl.toString());

        // Override the Webview's handler for redirects
        // Link communicates success and failure (analogous to the web's onSuccess and onExit
        // callbacks) via redirects.
        plaidLinkWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Parse the URL to determine if it's a special Plaid Link redirect or a request
                // for a standard URL (typically a forgotten password or account not setup link).
                // Handle Plaid Link redirects and open traditional pages directly in the  user's
                // preferred browser.
                Uri parsedUri = Uri.parse(url);
                if (parsedUri.getScheme().equals("plaidlink")) {
                    String action = parsedUri.getHost();
                    HashMap<String, String> linkData = parseLinkUriData(parsedUri);

                    if (action.equals("connected")) {
                        // User successfully linked
                        String publicToken = linkData.get("public_token");
                        String accountId = linkData.get("account_id");
                        String accountName = linkData.get("account_name");

                        // Reload Link in the Webview
                        // You will likely want to transition the view at this point.
                        addAccount(accessToken, publicToken, accountId, accountName);
                    } else if (action.equals("exit")) {
                        // User exited
                        // linkData may contain information about the user's status in the Link flow,
                        // the institution selected, information about any error encountered,
                        // and relevant API request IDs.
                        Log.d("User status in flow: ", linkData.get("status"));
                        // The request ID keys may or may not exist depending on when the user exited
                        // the Link flow.
                        Log.d("Link request ID: ", linkData.get("link_request_id"));
                        Log.d("API request ID: ", linkData.get("plaid_api_request_id"));

                        // Reload Link in the Webview
                        // You will likely want to transition the view at this point.
                        plaidLinkWebview.loadUrl(linkInitializationUrl.toString());
                    } else if (action.equals("event")) {
                        // The event action is fired as the user moves through the Link flow
                        Log.d("Event name: ", linkData.get("event_name"));
                    } else {
                        Log.d("Link action detected: ", action);
                    }
                    // Override URL loading
                    return true;
                } else if (parsedUri.getScheme().equals("https") ||
                        parsedUri.getScheme().equals("http")) {
                    // Open in browser - this is most  typically for 'account locked' or
                    // 'forgotten password' redirects
                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    // Override URL loading
                    return true;
                } else {
                    // Unknown case - do not override URL loading
                    return false;
                }
            }
        });
    }

    // Generate a Link initialization URL based on a set of configuration options
    public Uri generateLinkInitializationUrl(HashMap<String,String>linkOptions) {
        Uri.Builder builder = Uri.parse(linkOptions.get("baseUrl"))
                .buildUpon()
                .appendQueryParameter("isWebview", "true")
                .appendQueryParameter("isMobile", "true");
        for (String key : linkOptions.keySet()) {
            if (!key.equals("baseUrl")) {
                builder.appendQueryParameter(key, linkOptions.get(key));
            }
        }
        return builder.build();
    }

    // Parse a Link redirect URL querystring into a HashMap for easy manipulation and access
    public HashMap<String,String> parseLinkUriData(Uri linkUri) {
        HashMap<String,String> linkData = new HashMap<String,String>();
        for(String key : linkUri.getQueryParameterNames()) {
            linkData.put(key, linkUri.getQueryParameter(key));
        }
        return linkData;
    }

    private void addAccount(final String accessToken, String publicKey, String accountId, String accountName) {
        MultipartRequest accountRequest = new MultipartRequest(
                getResources().getString(R.string.user_link_account_url),
                null,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        JSONObject jsonObject;
                        String json = "";
                        try {
                            json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                            jsonObject = new JSONObject(json);

                            String status = jsonObject.getString("status");
                            if(status.equals(getResources().getString(R.string.status_success))) {
                                toLoginActivity();
                            }
                            else {
                                initializeLink(accessToken);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        accountRequest.addPart(new MultipartRequest.FormPart("access_token", accessToken));
        accountRequest.addPart(new MultipartRequest.FormPart("public_key", publicKey));
        accountRequest.addPart(new MultipartRequest.FormPart("account_id", accountId));
        accountRequest.addPart(new MultipartRequest.FormPart("account_name", accountName));

        requestQueue.add(accountRequest);
    }
}
