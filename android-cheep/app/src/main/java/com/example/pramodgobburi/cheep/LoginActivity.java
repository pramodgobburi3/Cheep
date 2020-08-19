package com.example.pramodgobburi.cheep;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import uk.me.hardill.volley.multipart.MultipartRequest;

public class LoginActivity extends AppCompatActivity {

    private Button loginSubmit;
    private EditText username;
    private EditText password;
    private RequestQueue requestQueue;
    private TextView register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        requestQueue = Volley.newRequestQueue(this);

        loginSubmit = (Button) findViewById(R.id.login_submit);
        username = (EditText) findViewById(R.id.login_username);
        password = (EditText) findViewById(R.id.login_password);
        register = (TextView) findViewById(R.id.login_register);

        getAccessandRefresh();

        loginSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.d("LOGINACTIVITY", "Sign in Clicked");
                if(username.getText().toString().equals("") || password.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, "Must specify a username and password", Toast.LENGTH_SHORT).show();
                }
                else {
                    login(username.getText().toString(), password.getText().toString());
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toRegisterActivity();
            }
        });
    }

    private void getAccessandRefresh() {
        SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
        String accessToken = sp.getString("cheep_accessToken", null);
        String refreshToken = sp.getString("cheep_refreshToken", null);
        String username = sp.getString("cheep_username", null);
        String password = sp.getString("cheep_password", null);

        if(accessToken != null && refreshToken != null) {
            byte[] bytAccess = Base64.decode(accessToken, Base64.DEFAULT);
            byte[] bytRefresh = Base64.decode(refreshToken, Base64.DEFAULT);
            try {
                accessToken = new String(bytAccess, "UTF-8");
                refreshToken = new String(bytRefresh, "UTF-8");

                Log.e("ACCESS_TOKEN", accessToken);
                Log.e("REFRESH_TOKEN", refreshToken);

                getUserInfo(accessToken, refreshToken);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private void login(final String username, final String password) {
        MultipartRequest loginRequest = new MultipartRequest(
                this.getResources().getString(R.string.login_url),
                null,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {

                        JSONObject jsonObject;
                        String json = "";
                        try {
                            json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                            jsonObject = new JSONObject(json);

                            String accessToken = jsonObject.getString("access_token");
                            String refreshToken = jsonObject.getString("refresh_token");

                            SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
                            SharedPreferences.Editor Ed = sp.edit();
                            byte[] accessTokenData = accessToken.getBytes("UTF-8");
                            byte[] refreshTokenData = refreshToken.getBytes("UTF-8");
                            Ed.putString("cheep_accessToken", Base64.encodeToString(accessTokenData, Base64.DEFAULT));
                            Ed.putString("cheep_refreshToken", Base64.encodeToString(refreshTokenData, Base64.DEFAULT));
                            Ed.putString("cheep_username", Base64.encodeToString(username.getBytes(), Base64.DEFAULT));
                            Ed.putString("cheep_password", Base64.encodeToString(password.getBytes(), Base64.DEFAULT));
                            Ed.commit();

                            getUserInfo(accessToken, refreshToken);

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
                        Log.e("LOGINACTIVITY", error.toString());
                    }
                }

        );

        loginRequest.addPart(new MultipartRequest.FormPart("username", username));
        loginRequest.addPart(new MultipartRequest.FormPart("password", password));
        loginRequest.addPart(new MultipartRequest.FormPart("client_id", getResources().getString(R.string.client_id)));
        loginRequest.addPart(new MultipartRequest.FormPart("client_secret", getResources().getString(R.string.client_secret)));
        loginRequest.addPart(new MultipartRequest.FormPart("grant_type", "password"));

        requestQueue.add(loginRequest);

    }

    private void getUserInfo(final String accessToken, final String refreshToken) {
        MultipartRequest userRequest = new MultipartRequest(
                getResources().getString(R.string.get_user_info_url),
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

                                JSONObject userObject = jsonObject.getJSONObject("user");

                                String firstName = userObject.getString("firstname");
                                String lastName = userObject.getString("lastname");
                                String email = userObject.getString("email");

                                User.getInstance().setUserData(username.getText().toString(), firstName, lastName, email, true);
                                User.getInstance().setAccessToken(accessToken);
                                User.getInstance().setRefreshToken(refreshToken);

//                                Toast.makeText(LoginActivity.this, User.getFirstname(), Toast.LENGTH_SHORT).show();
                                toMainActivity();
                            }
                            else if (status.equals(getResources().getString(R.string.status_expired_token)))    {
                                Toast.makeText(LoginActivity.this, status, Toast.LENGTH_SHORT).show();
                            }
                            else if(status.equals(getResources().getString(R.string.status_invalid_token)))   {
                                Toast.makeText(LoginActivity.this, status, Toast.LENGTH_SHORT).show();
                            }
                            else if (status.equals(getResources().getString(R.string.status_not_provided))) {
                                Toast.makeText(LoginActivity.this, status, Toast.LENGTH_SHORT).show();
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
                        Log.e("LOGINACTIVITY", error.toString());
                    }
                }
        );

        userRequest.addPart(new MultipartRequest.FormPart("access_token", accessToken));
        requestQueue.add(userRequest);
    }

    private void toMainActivity() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void toRegisterActivity()   {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
        finish();
    }

}
