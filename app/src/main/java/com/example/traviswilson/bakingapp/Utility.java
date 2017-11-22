package com.example.traviswilson.bakingapp;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.util.Log;

import okhttp3.internal.Util;

/**
 * Created by traviswilson on 11/1/17.
 */

public class Utility {
    private static final int LONGEST_LINE_PERMITTED_PORTRAIT = 40;
    private static final int LONGEST_LINE_PERMITTED_LANDSCAPE = -1;

    private static final String LOG_TAG = Utility.class.toString();

    public static String getMeasurementFromData(String quantity, String measurement){
        return quantity + " " +measurement;
    }
    public static String convertComputerScienceStepToHumanStep(String stepNumber){
        return stepNumber.equals("0")? "": stepNumber;
    }
    public static String formatString(Context context, String s){
        int orientationCode = context.getResources().getConfiguration().orientation;
        if (orientationCode == Configuration.ORIENTATION_LANDSCAPE) {
            if (s.length() > LONGEST_LINE_PERMITTED_LANDSCAPE) {
                int splitLocation = getSplitLocation(s, LONGEST_LINE_PERMITTED_LANDSCAPE);
                return s.substring(0, splitLocation)
                        +"\n" +
                        s.substring(splitLocation);
            } else {
                return s;
            }
        } else if (orientationCode == Configuration.ORIENTATION_PORTRAIT){
            if (s.length() > LONGEST_LINE_PERMITTED_PORTRAIT){
                int splitLocation = getSplitLocation(s, LONGEST_LINE_PERMITTED_PORTRAIT);
                return s.substring(0, splitLocation) + "\n" +
                        s.substring(splitLocation);
            } else {
                return s;
            }
        } else {
            return s; //TODO: fix me for tablets.
        }
    }
    private static int getSplitLocation(String s, int place){
        for (int i = place ; i> 0 ; i--){
            if (s.charAt(i) == ' '){
                return i;
            }
        }
        return place;
    }
}
