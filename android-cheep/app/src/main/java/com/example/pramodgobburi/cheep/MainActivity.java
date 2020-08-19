package com.example.pramodgobburi.cheep;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.chaos.view.PinView;
import com.example.pramodgobburi.cheep.Adapters.BudgetsRecyclerAdapter;
import com.example.pramodgobburi.cheep.Models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;
import uk.me.hardill.volley.multipart.MultipartRequest;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mainBudgets;
    private View mainContent;
    private BudgetsRecyclerAdapter adapter;
    private ArrayList<Budget> budgets;
    private PieChartView pieChartView;
    List<SliceValue> pieData;
    private AlertDialog authDialog;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mainContent= findViewById(R.id.main_layout);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Budgets");

        requestQueue = Volley.newRequestQueue(this);
        mainBudgets = (RecyclerView) mainContent.findViewById(R.id.main_budgets);
        pieChartView = mainContent.findViewById(R.id.chart);
//        mockfetchBudgets();
        fetchBudgets();

        FloatingActionButton fab = (FloatingActionButton) mainContent.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newBudget = new Intent(MainActivity.this, NewBudgetActivity.class);
                startActivityForResult(newBudget, 1);
            }
        });
    }

    private void fetchBudgets() {
        Log.e("ACCESS_TOKEN", User.getInstance().getAccessToken());
        MultipartRequest budgetsRequest = new MultipartRequest(
                getResources().getString(R.string.fetch_budgets_url),
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
                                JSONArray jsonBudgets = jsonObject.getJSONArray("budgets");

                                budgets = new ArrayList<>();
                                for(int i=0; i < jsonBudgets.length(); i++) {
                                    JSONObject budget = jsonBudgets.getJSONObject(i);

                                    String budgetName = budget.getString("name");
                                    String budgetColor = budget.getString("color");
                                    float budgetAmount = Float.parseFloat(budget.getString("limit"));
                                    float budgetCompleted = Float.parseFloat(budget.getString("spent"));

                                    budgets.add(new Budget(budgetName, budgetAmount, budgetCompleted, Color.parseColor(budgetColor)));
                                }

                                initializeRecyclerView();
                                initChart();
                                // Create asynctask to recompute budgets
                            }
                            else if (status.equals(getResources().getString(R.string.status_expired_token)))    {
                                Toast.makeText(MainActivity.this, status, Toast.LENGTH_SHORT).show();
                            }
                            else if(status.equals(getResources().getString(R.string.status_invalid_token)))   {
                                Toast.makeText(MainActivity.this, status, Toast.LENGTH_SHORT).show();
                            }
                            else if (status.equals(getResources().getString(R.string.status_not_provided))) {
                                Toast.makeText(MainActivity.this, status, Toast.LENGTH_SHORT).show();
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
        budgetsRequest.addPart(new MultipartRequest.FormPart("access_token", User.getInstance().getAccessToken()));
        requestQueue.add(budgetsRequest);

    }

    private void mockfetchBudgets() {
        //Mock up data
        budgets = new ArrayList<>();
        budgets.add(new Budget("Gas", 100f, 40f, R.color.red));
        budgets.add(new Budget("Food", 200f, 80f, R.color.blue));
        budgets.add(new Budget("Entertainment", 150f, 90f, R.color.orange));

//        initializeRecyclerView();
    }

    private void initializeRecyclerView() {
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        adapter = new BudgetsRecyclerAdapter(this, budgets);
        final SwipeController swipeController = new SwipeController(this, new SwipeControllerActions() {
            @Override
            public void onLeftClicked(int position) {
                super.onLeftClicked(position);
            }

            @Override
            public void onRightClicked(int position) {
                super.onRightClicked(position);
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
        mainBudgets.setAdapter(adapter);
        mainBudgets.setLayoutManager(llm);
        mainBudgets.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });
        itemTouchHelper.attachToRecyclerView(mainBudgets);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                Budget budget = (Budget) data.getParcelableExtra("result");
                addBudgetToDB(budget);

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    private void addBudgetToDB(final Budget budget) {
        MultipartRequest addBudgetRequest = new MultipartRequest(
                getResources().getString(R.string.add_budgets_url),
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
                                budgets.add(budget);
                                adapter.notifyDataSetChanged();


                                initChart();
                            }
                            else if (status.equals(getResources().getString(R.string.status_expired_token)))    {
                                Toast.makeText(MainActivity.this, status, Toast.LENGTH_SHORT).show();
                            }
                            else if(status.equals(getResources().getString(R.string.status_invalid_token)))   {
                                Toast.makeText(MainActivity.this, status, Toast.LENGTH_SHORT).show();
                            }
                            else if (status.equals(getResources().getString(R.string.status_not_provided))) {
                                Toast.makeText(MainActivity.this, status, Toast.LENGTH_SHORT).show();
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

        addBudgetRequest.addPart(new MultipartRequest.FormPart("access_token", User.getInstance().getAccessToken()));
        addBudgetRequest.addPart(new MultipartRequest.FormPart("budget_name", budget.getBudgetName()));
        addBudgetRequest.addPart(new MultipartRequest.FormPart("budget_limit", String.valueOf(budget.getBudgetAmount())));
        addBudgetRequest.addPart(new MultipartRequest.FormPart("budget_spent", String.valueOf(budget.getBudgetCompleted())));
        String hexColor = String.format("#%06X", (0xFFFFFF & budget.getColor()));
        addBudgetRequest.addPart(new MultipartRequest.FormPart("budget_color", hexColor));

        requestQueue.add(addBudgetRequest);
    }

    private void initChart() {
        pieData = new ArrayList<>();
        float total = 0;
        float spent = 0;
        for(Budget b : budgets) {
            total += b.getBudgetAmount();
            spent += b.getBudgetCompleted();
        }
        for(Budget b : budgets) {
            try {
                pieData.add(new SliceValue(b.getBudgetAmount(), getResources().getColor(b.getColor())).setLabel(String.format("$%.2f", b.getBudgetAmount())));
            }
            catch (Resources.NotFoundException e) {
                pieData.add(new SliceValue(b.getBudgetAmount(), b.getColor()).setLabel(String.format("$%.2f", b.getBudgetAmount())));
            }
        }
        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true);
        pieChartData.setHasCenterCircle(true).setCenterText1(String.format("$%.0f", total)).setCenterText2(String.format("$%.0f", spent)).setCenterText1FontSize(18).setCenterText1Color(Color.parseColor("#ffffff"));
        if(spent > total) {
            pieChartData.setCenterText2Color(getResources().getColor(R.color.red));
        }
        else {
            pieChartData.setCenterText2Color(getResources().getColor(R.color.green));
        }
        pieChartView.setPieChartData(pieChartData);


    }

    @Override
    protected void onStop() {
        super.onStop();
        if(User.currentUser != null) {
            User.currentUser.setIsAuthenticated(false);
        }
        if (authDialog != null){
            authDialog.dismiss();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(User.currentUser == null) {
            authenticateUser();
        }
        else {
            if(!User.currentUser.isIsAuthenticated()) {
                authenticateUser();
            }
        }
    }

    private void authenticateUser() {
        mainContent.setVisibility(View.GONE);
        LayoutInflater inflater = getLayoutInflater();
        View authLayout = inflater.inflate(R.layout.authorize_layout, null);
        final PinView pinView = authLayout.findViewById(R.id.pin);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(authLayout);
        builder.setCancelable(false);
        authDialog = builder.show();

        pinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Toast.makeText(MainActivity.this, "Length: " + pinView.getText().length(), Toast.LENGTH_SHORT).show();
                if(pinView.getText().length() == 4){
                    if(pinView.getText().toString().equals("1998")) {
                        authDialog.dismiss();
                        mainContent.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

    }

}
