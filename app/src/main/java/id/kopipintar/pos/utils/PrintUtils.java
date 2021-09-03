package id.kopipintar.pos.utils;

import android.app.Application;

import com.mazenrashed.printooth.Printooth;

public class PrintUtils extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Printooth.INSTANCE.init(this);
    }
}
