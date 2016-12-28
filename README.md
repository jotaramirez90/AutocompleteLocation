# AutocompleteLocation
Cute library to implement PlaceAutocomplete in Android

<img src="https://github.com/jotaramirez90/AutocompleteLocation/blob/master/art/init.png" width="200px" height="356px" />
<img src="https://github.com/jotaramirez90/AutocompleteLocation/blob/master/art/autocomplete.png" width="200px" height="356px" />
<img src="https://github.com/jotaramirez90/AutocompleteLocation/blob/master/art/place.png" width="200px" height="356px" />

#Native version
Maybe it would be useful to take a look into the new official approach
https://developers.google.com/places/android-api/autocomplete

# Usage
**Add the dependencies to your gradle file:**
```javascript
	dependencies {
    	compile ''
	}
```
**Get a Google Maps API Key and enabled the Google Places API for Android** *(Add your API Key in AndroidManifest)*:

```xml
    <meta-data
        android:name="com.google.android.geo.API_KEY"
        android:value="YOUR_API_KEY"
        />
```
**Add the AutocompleteLocation into the layout:**
```xml
  <com.jota.autocompletelocation.AutoCompleteLocation
      android:id="@+id/autocomplete_location"
      android:layout_width="match_parent"
      android:layout_height="wrap_content"
      />
```
**Set the listeners:**
```java
public class MainActivity extends FragmentActivity
    implements AutoCompleteLocation.AutoCompleteLocationListener {

  @Override protected void onCreate(Bundle savedInstanceState) {
    ...
    AutoCompleteLocation autoCompleteLocation =
        (AutoCompleteLocation) findViewById(R.id.autocomplete_location);
    autoCompleteLocation.setAutoCompleteTextListener(this);
  }
}
```
**Get the place:**
```java
  @Override public void onItemSelected(Place selectedPlace) {
    mMap.addMarker(new MarkerOptions().position(latLng));
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
  }
```

# Style it!
Attributes for custom AutoCompleteLocation
* __background_layout__
* __hint_text__ 
* __hint_text_color__
* __text_color__

```xml
  <com.jota.autocompletelocation.AutoCompleteLocation
      android:id="@+id/autocomplete_location"
      android:layout_width="match_parent"
      android:layout_height="wrap_content"
      app:background_layout="@drawable/bg_rounded_accent"
      app:hint_text="Custom text"
      app:hint_text_color="#F0F0F0"
      app:text_color="#FFF000"
      />
```

# Help me
Pull requests are more than welcome, help me and others improve this awesome library.

The code is based in the Place Autocomplete original concept.

# License
	Copyright 2016 Jota Ramirez

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
