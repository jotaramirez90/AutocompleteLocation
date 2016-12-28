package com.jota.autocompletelocation.sample;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

public class AndroidApplication extends Application {

  @Override protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    MultiDex.install(this);
  }
}
