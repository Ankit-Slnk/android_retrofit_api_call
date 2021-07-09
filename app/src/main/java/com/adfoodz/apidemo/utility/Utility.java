package com.adfoodz.apidemo.utility;

import android.content.Context;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import com.adfoodz.apidemo.R;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

import okhttp3.ResponseBody;

public class Utility {

    public static RequestOptions getGlideRequestOptions() {
        return new RequestOptions()
                .centerCrop()
                .error(R.drawable.image_error)
                .priority(Priority.HIGH);
    }

    public static void showError(Context context, ResponseBody responseBody) {
        JSONObject jObjError = null;
        try {
            jObjError = new JSONObject(responseBody.string());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            JSONObject data = jObjError.getJSONObject("errors");
            Iterator<String> errorKeys = data.keys();

            while (errorKeys.hasNext()) {
                String key = errorKeys.next();
                try {
                    if (data.get(key) instanceof JSONArray) {
                        JSONArray array = data.getJSONArray(key);
                        int size = array.length();
                        if (size > 0) {
                            Toast.makeText(context, array.getString(0), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        System.out.println(key + ":" + data.getString(key));
                    }
                } catch (Throwable e) {
                    try {
                        System.out.println(key + ":" + data.getString(key));
                    } catch (Exception ee) {
                    }
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
