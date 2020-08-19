package com.example.pramodgobburi.cheep;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.skydoves.colorpickerpreference.ColorEnvelope;
import com.skydoves.colorpickerpreference.ColorListener;
import com.skydoves.colorpickerpreference.ColorPickerDialog;

public class NewBudgetActivity extends AppCompatActivity {

    private EditText budgetName;
    private EditText budgetAmount;
    private Button submit;
    private View budgetColor;
    private ColorPickerDialog.Builder builder;
    private int color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_budget);

        budgetName = (EditText) findViewById(R.id.new_budget_name);
        budgetAmount = (EditText) findViewById(R.id.new_budget_amount);
        budgetColor = (View) findViewById(R.id.new_budget_color);
        submit = (Button) findViewById(R.id.new_budget_save);

        budgetColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initColorPicker();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!budgetName.getText().toString().equals("") &&
                        !budgetAmount.getText().toString().equals("")) {
                    float amount = Float.parseFloat(budgetAmount.getText().toString());
                    Budget budget = new Budget(budgetName.getText().toString(), amount, 0f, color);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", budget);
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }
            }
        });


    }

    private void initColorPicker() {
        builder = new ColorPickerDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        builder.setTitle("ColorPicker Dialog");
        builder.setPreferenceName("MyColorPickerDialog");
        builder.setPositiveButton(
                "Ok",
                new ColorListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onColorSelected(ColorEnvelope colorEnvelope) {
                        color = colorEnvelope.getColor();
                        budgetColor.setBackgroundColor(color);
                    }
                });
        builder.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.show();
    }
}
