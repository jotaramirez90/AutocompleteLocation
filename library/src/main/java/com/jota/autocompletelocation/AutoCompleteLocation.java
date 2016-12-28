package com.jota.autocompletelocation;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

public class AutoCompleteLocation extends AutoCompleteTextView {

  private Drawable mCloseIcon;
  private GoogleApiClient mGoogleApiClient;
  private AutoCompleteAdapter mAutoCompleteAdapter;
  private AutoCompleteLocationListener mAutoCompleteLocationListener;

  public interface AutoCompleteLocationListener {
    void onTextClear();

    void onItemSelected(Place selectedPlace);
  }

  public AutoCompleteLocation(Context context, AttributeSet attrs) {
    super(context, attrs);
    Resources resources = context.getResources();
    TypedArray typedArray =
        context.obtainStyledAttributes(attrs, R.styleable.AutoCompleteLocation, 0, 0);
    Drawable background =
        typedArray.getDrawable(R.styleable.AutoCompleteLocation_background_layout);
    if (background == null) {
      background = resources.getDrawable(R.drawable.bg_rounded_white);
    }
    String hintText = typedArray.getString(R.styleable.AutoCompleteLocation_hint_text);
    if (hintText == null) {
      hintText = resources.getString(R.string.default_hint_text);
    }
    int hintTextColor = typedArray.getColor(R.styleable.AutoCompleteLocation_hint_text_color,
        resources.getColor(R.color.default_hint_text));
    int textColor = typedArray.getColor(R.styleable.AutoCompleteLocation_text_color,
        resources.getColor(R.color.default_text));
    int padding = resources.getDimensionPixelSize(R.dimen.default_padding);
    typedArray.recycle();

    setBackground(background);
    setHint(hintText);
    setHintTextColor(hintTextColor);
    setTextColor(textColor);
    setPadding(padding, padding, padding, padding);
    setMaxLines(resources.getInteger(R.integer.default_max_lines));

    mCloseIcon = context.getResources().getDrawable(R.drawable.ic_close);
    mGoogleApiClient = new GoogleApiClient.Builder(context).addApi(Places.GEO_DATA_API)
        .addApi(AppIndex.API)
        .build();
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    mGoogleApiClient.connect();
    this.addTextChangedListener(mAutoCompleteTextWatcher);
    this.setOnTouchListener(mOnTouchListener);
    this.setOnItemClickListener(mAutocompleteClickListener);
    this.setAdapter(mAutoCompleteAdapter);
    mAutoCompleteAdapter = new AutoCompleteAdapter(getContext(), mGoogleApiClient, null, null);
    this.setAdapter(mAutoCompleteAdapter);
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    if (mGoogleApiClient.isConnected()) {
      mGoogleApiClient.disconnect();
    }
  }

  @Override public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    if (!enabled) {
      this.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    } else {
      this.setCompoundDrawablesWithIntrinsicBounds(null, null,
          AutoCompleteLocation.this.getText().toString().equals("") ? null : mCloseIcon, null);
    }
  }

  public void setAutoCompleteTextListener(
      AutoCompleteLocationListener autoCompleteLocationListener) {
    mAutoCompleteLocationListener = autoCompleteLocationListener;
  }

  private TextWatcher mAutoCompleteTextWatcher = new TextWatcher() {
    @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      AutoCompleteLocation.this.setCompoundDrawablesWithIntrinsicBounds(null, null,
          AutoCompleteLocation.this.getText().toString().equals("") ? null : mCloseIcon, null);
      if (mAutoCompleteLocationListener != null) {
        mAutoCompleteLocationListener.onTextClear();
      }
    }

    @Override public void afterTextChanged(Editable editable) {
    }
  };

  private OnTouchListener mOnTouchListener = new View.OnTouchListener() {
    @Override public boolean onTouch(View view, MotionEvent motionEvent) {
      if (motionEvent.getX()
          > AutoCompleteLocation.this.getWidth()
          - AutoCompleteLocation.this.getPaddingRight()
          - mCloseIcon.getIntrinsicWidth()) {
        AutoCompleteLocation.this.setText("");
        AutoCompleteLocation.this.setCompoundDrawables(null, null, null, null);
      }
      return false;
    }
  };

  private AdapterView.OnItemClickListener mAutocompleteClickListener =
      new AdapterView.OnItemClickListener() {
        @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
          UIUtils.hideKeyboard(AutoCompleteLocation.this.getContext(), AutoCompleteLocation.this);
          final AutocompletePrediction item = mAutoCompleteAdapter.getItem(position);
          if (item != null) {
            final String placeId = item.getPlaceId();
            PendingResult<PlaceBuffer> placeResult =
                Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
          }
        }
      };

  private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback =
      new ResultCallback<PlaceBuffer>() {
        @Override public void onResult(@NonNull PlaceBuffer places) {
          if (!places.getStatus().isSuccess()) {
            places.release();
            return;
          }
          final Place place = places.get(0);
          if (mAutoCompleteLocationListener != null) {
            mAutoCompleteLocationListener.onItemSelected(place);
          }
          places.release();
        }
      };
}
