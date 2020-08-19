package com.example.pramodgobburi.cheep.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.pramodgobburi.cheep.Budget;
import com.example.pramodgobburi.cheep.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BudgetsRecyclerAdapter extends RecyclerView.Adapter<BudgetsRecyclerAdapter.ViewHolder> {
    private List<Budget> budgets;
    private Context context;
    private LayoutInflater layoutInflater;
    private int date;
    private int maxDaysOfMonth;

    public BudgetsRecyclerAdapter(Context context, ArrayList<Budget> budgets) {
        this.context = context;
        this.budgets = budgets;
        this.layoutInflater = LayoutInflater.from(this.context);
    }

    @NonNull
    @Override
    public BudgetsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = layoutInflater.inflate(R.layout.single_budget, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BudgetsRecyclerAdapter.ViewHolder viewHolder, int i) {
        String budgetName = budgets.get(i).getBudgetName();
        float totalAmount = budgets.get(i).getBudgetAmount();
        float currentCompleted = budgets.get(i).getBudgetCompleted();

        viewHolder.budgetName.setText(budgetName);
        viewHolder.budgetAmount.setText(String.format("$%.2f", currentCompleted));
        viewHolder.budgetSpent.setText(String.format("$%.2f Left", totalAmount-currentCompleted));
        float completed = currentCompleted/totalAmount * 100;
        viewHolder.bar.setProgress((int) completed);
        Calendar calendar = Calendar.getInstance();
        date = calendar.get(Calendar.DAY_OF_MONTH);
        maxDaysOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        try {
            viewHolder.budgetColor.setBackgroundColor(context.getResources().getColor(budgets.get(i).getColor()));
        }
        catch (Resources.NotFoundException e) {
            viewHolder.budgetColor.setBackgroundColor(budgets.get(i).getColor());
        }
        float percent = (float)date/maxDaysOfMonth * totalAmount;

        if(currentCompleted > percent) {
            viewHolder.bar.setProgressTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.red)));
        }
        else {
            viewHolder.bar.setProgressTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.blue)));
        }
        viewHolder.itemView.post(new Runnable()
        {
            @Override
            public void run()
            {
                int barRight = viewHolder.bar.getRight()-viewHolder.day.getWidth();
                float start = (float)(date-1)/maxDaysOfMonth * barRight;
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) viewHolder.day.getLayoutParams();
                params.setMargins((int)start,0, 0, 0);
                viewHolder.day.setLayoutParams(params);

            }
        });
    }

    @Override
    public int getItemCount() {
        return budgets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View budgetColor;
        TextView budgetName;
        TextView budgetSpent;
        TextView budgetAmount;
        ProgressBar bar;
        View day;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            budgetColor = (View) itemView.findViewById(R.id.single_budget_color);
            budgetName = (TextView) itemView.findViewById(R.id.single_budget_name);
            budgetSpent = (TextView) itemView.findViewById(R.id.single_budget_spent);
            budgetAmount = (TextView) itemView.findViewById(R.id.single_budget_amount);
            bar = (ProgressBar) itemView.findViewById(R.id.single_budget_bar);
            day = (View) itemView.findViewById(R.id.single_budget_day);

        }
    }

}
