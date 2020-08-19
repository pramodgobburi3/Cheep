package com.example.pramodgobburi.cheep;

import android.os.Parcel;
import android.os.Parcelable;

public class Budget implements Parcelable {
    private String budgetName;
    private float budgetAmount;
    private float budgetCompleted;
    private int color;

    public Budget(String budgetName, float budgetAmount, float budgetCompleted, int color) {
        this.budgetName = budgetName;
        this.budgetAmount = budgetAmount;
        this.budgetCompleted = budgetCompleted;
        this.color = color;
    }

    protected Budget(Parcel in) {
        budgetName = in.readString();
        budgetAmount = in.readFloat();
        budgetCompleted = in.readFloat();
        color = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(budgetName);
        dest.writeFloat(budgetAmount);
        dest.writeFloat(budgetCompleted);
        dest.writeInt(color);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Budget> CREATOR = new Creator<Budget>() {
        @Override
        public Budget createFromParcel(Parcel in) {
            return new Budget(in);
        }

        @Override
        public Budget[] newArray(int size) {
            return new Budget[size];
        }
    };

    public String getBudgetName() {
        return budgetName;
    }

    public void setBudgetName(String budgetName) {
        this.budgetName = budgetName;
    }

    public float getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(float budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    public float getBudgetCompleted() {
        return budgetCompleted;
    }

    public void setBudgetCompleted(float budgetCompleted) {
        this.budgetCompleted = budgetCompleted;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
