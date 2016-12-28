package com.jota.autocompletelocation;

import android.content.Context;
import android.graphics.Typeface;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class AutoCompleteAdapter extends ArrayAdapter<AutocompletePrediction>
    implements Filterable {

  private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);
  private ArrayList<AutocompletePrediction> mResultList;
  private GoogleApiClient mGoogleApiClient;
  private LatLngBounds mBounds;
  private AutocompleteFilter mPlaceFilter;

  public AutoCompleteAdapter(Context context, GoogleApiClient googleApiClient, LatLngBounds bounds,
      AutocompleteFilter filter) {
    super(context, android.R.layout.simple_expandable_list_item_2, android.R.id.text1);
    mGoogleApiClient = googleApiClient;
    mBounds = bounds;
    mPlaceFilter = filter;
  }

  public void setBounds(LatLngBounds bounds) {
    mBounds = bounds;
  }

  @Override public int getCount() {
    return mResultList.size();
  }

  @Override public AutocompletePrediction getItem(int position) {
    return mResultList.get(position);
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    View row = super.getView(position, convertView, parent);
    AutocompletePrediction item = getItem(position);
    TextView textView1 = (TextView) row.findViewById(android.R.id.text1);
    TextView textView2 = (TextView) row.findViewById(android.R.id.text2);
    textView1.setText(item.getPrimaryText(STYLE_BOLD));
    textView2.setText(item.getSecondaryText(STYLE_BOLD));

    return row;
  }

  @Override public Filter getFilter() {
    return new Filter() {
      @Override protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        ArrayList<AutocompletePrediction> filterData = new ArrayList<>();

        if (constraint != null) {
          filterData = getAutocomplete(constraint);
        }

        results.values = filterData;
        if (filterData != null) {
          results.count = filterData.size();
        } else {
          results.count = 0;
        }

        return results;
      }

      @Override protected void publishResults(CharSequence constraint, FilterResults results) {

        if (results != null && results.count > 0) {
          mResultList = (ArrayList<AutocompletePrediction>) results.values;
          notifyDataSetChanged();
        } else {
          notifyDataSetInvalidated();
        }
      }

      @Override public CharSequence convertResultToString(Object resultValue) {
        if (resultValue instanceof AutocompletePrediction) {
          return ((AutocompletePrediction) resultValue).getFullText(null);
        } else {
          return super.convertResultToString(resultValue);
        }
      }
    };
  }

  private ArrayList<AutocompletePrediction> getAutocomplete(CharSequence constraint) {
    if (mGoogleApiClient.isConnected()) {
      PendingResult<AutocompletePredictionBuffer> results =
          Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, constraint.toString(),
              mBounds, mPlaceFilter);

      AutocompletePredictionBuffer autocompletePredictions = results.await(60, TimeUnit.SECONDS);

      final Status status = autocompletePredictions.getStatus();
      if (!status.isSuccess()) {
        Toast.makeText(getContext(), "Error contacting API: " + status.toString(),
            Toast.LENGTH_SHORT).show();
        autocompletePredictions.release();
        return null;
      }

      return DataBufferUtils.freezeAndClose(autocompletePredictions);
    }
    return null;
  }
}
