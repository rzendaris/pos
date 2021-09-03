package id.kopipintar.pos.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import id.kopipintar.pos.ActivityLogin;
import id.kopipintar.pos.R;
import id.kopipintar.pos.model.User;

import static android.content.Context.MODE_PRIVATE;

public class CommonUtils {

    private static SharedPreferences sharedPreferences;

    public static User getUserLoginData(Context context){
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name),MODE_PRIVATE);
        String json = sharedPreferences.getString(ActivityLogin.DATAUSER, null);

        Gson gson = new Gson();
        Type type = new TypeToken<User>(){}.getType();

        return gson.fromJson(json,type);
    }

    public static void clearUserLoginData(Context context){
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name),MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

    public static void logout(Activity activity){
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle("Warning!");
        alertDialog.setMessage("Anda yakin ingin logout?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ya",
                (dialog, i) -> {
                    dialog.dismiss();
                    clearUserLoginData(activity);
                    activity.startActivity(new Intent(activity, ActivityLogin.class));
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Tidak",
                (dialog, i) -> dialog.dismiss());
        alertDialog.show();
    }


}
