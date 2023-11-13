package com.iwc.iwctablet.utility;

import android.text.InputFilter;
import android.text.Spanned;

public class MinNumberFilter implements InputFilter {

    private int minNumber, maxNumber;

    public MinNumberFilter(int minValue, int maxValue) {
        this.minNumber = minValue;
        this.maxNumber = maxValue;
    }

    public MinNumberFilter(String minValue, String maxValue) {
        this.minNumber = Integer.parseInt(minValue);
        this.maxNumber = Integer.parseInt(maxValue);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            int input = Integer.parseInt(dest.toString() + source.toString());
            if (isInRange(minNumber, maxNumber, input))
                return null;
        } catch (NumberFormatException ignored) { }
        return "";
    }

    private boolean isInRange(int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}
