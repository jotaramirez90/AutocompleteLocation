package com.jota.autocompletelocation;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class UIUtils {

  public static void hideKeyboard(Context context, View view) {
    if (view != null) {
      InputMethodManager inputMethodManager =
          (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
      inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
  }
}
