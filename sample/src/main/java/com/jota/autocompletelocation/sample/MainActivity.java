package com.jota.autocompletelocation.sample;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jota.autocompletelocation.AutoCompleteLocation;

public class MainActivity extends FragmentActivity
    implements OnMapReadyCallback, AutoCompleteLocation.AutoCompleteLocationListener {

  private GoogleMap mMap;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    SupportMapFragment mapFragment =
        (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);

    AutoCompleteLocation autoCompleteLocation =
        (AutoCompleteLocation) findViewById(R.id.autocomplete_location);
    autoCompleteLocation.setAutoCompleteTextListener(this);
  }

  @Override public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;

    LatLng madrid = new LatLng(40.4167754, -3.7037902);
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(madrid, 16));
  }

  @Override public void onTextClear() {
    mMap.clear();
  }

  @Override public void onItemSelected(Place selectedPlace) {
    addMapMarker(selectedPlace.getLatLng());
  }

  private void addMapMarker(LatLng latLng) {
    mMap.clear();
    mMap.addMarker(new MarkerOptions().position(latLng));
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
  }
}
