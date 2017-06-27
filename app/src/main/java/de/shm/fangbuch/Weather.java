package de.shm.fangbuch;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Weather.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Weather#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Weather extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LONGTITUDE = "LONGTITUDE";
    private static final String LATITUDE = "LATITUDE";

    private final static double KELVIN = 273.14;
    private final static String WEATHER_DATA = "weather_data";

    public final static String WEATHER_FRAGMENT = "weather_fragment";

    private double mLon;
    private double mLat;
    private JSONObject mWeatherData;
    private int mTemperatur;
    private int mPressure;

    private OnFragmentInteractionListener mListener;

    public Weather() {
        // Required empty public constructor
    }


    public static Weather newInstance(double lon, double lat) {
        Weather fragment = new Weather();
        Bundle args = new Bundle();
        args.putDouble(LONGTITUDE, lon);
        args.putDouble(LATITUDE, lat);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLon = getArguments().getDouble(LONGTITUDE);
            mLat = getArguments().getDouble(LATITUDE);
        }
        SharedPreferences sharedPreferences = getActivity().getPreferences(getActivity().MODE_PRIVATE);
        try {
            mWeatherData = new JSONObject(sharedPreferences.getString(WEATHER_DATA,null));
        } catch (JSONException e) {
            e.printStackTrace();
            mWeatherData = null;
            getWeatherData(mLon,mLat);
        }catch (NullPointerException e){
            e.printStackTrace();
            mWeatherData = null;
            getWeatherData(mLon,mLat);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        Context context = getActivity();


        if(mWeatherData!=null) {
            setWeatherCard(view, mWeatherData);
        }

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }

    public void upDateLocation(double lon,double lat){
        mLat=lat;
        mLon=lon;
        getWeatherData(lon,lat);

    }

    private void getWeatherData(double lon, double lat){
        Log.d(WEATHER_FRAGMENT,"GPS data -> Lon: "+Double.toString(lon)+"Lat: " + Double.toString(lat));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String url = "http://api.openweathermap.org/data/2.5/weather?lat="+Double.toString(lat)+"&lon="+Double.toString(lon)+"&APPID=064d2048fffea5ad0495f3bee1be8fb2";
        Log.d(WEATHER_FRAGMENT,"WeatherURL ->" + url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject jsonObject) {

                mWeatherData=jsonObject;
                SharedPreferences sharedPreferences = getActivity().getPreferences(getActivity().MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(WEATHER_DATA,mWeatherData.toString());
                editor.commit();
                setWeatherCard(getView(),mWeatherData);

            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void setWeatherCard(View view, JSONObject jsonObject){
        final TextView textViewTemp = (TextView) view.findViewById(R.id.textViewTemp);
        final ImageView imageViewWeather = (ImageView) view.findViewById(R.id.weather_icon);
        final TextView textViewOrt = (TextView) view.findViewById(R.id.textViewOrt);
        final TextView textViewPressure = (TextView) view.findViewById(R.id.textViewPressure);
        final TextView textViewMaxTemp = (TextView) view.findViewById(R.id.textViewMaxTemp);
        final TextView textViewMinTemp = (TextView) view.findViewById(R.id.textViewMinTemp);

        try {
            JSONArray weatherArray = jsonObject.getJSONArray("weather");
            JSONObject jsonItem = weatherArray.getJSONObject(0);
            //imageViewWeather.setImageResource(R.drawable.w10d);
            imageViewWeather.setImageResource(getResources().getIdentifier("de.shm.fangbuch:drawable/w" + jsonItem.getString("icon"), null, null));
            jsonItem = jsonObject.getJSONObject("main");
            double temp = Double.parseDouble(jsonItem.getString("temp")) - KELVIN;
            int intTemp = (int) (temp + 0.5);
            textViewTemp.setText(String.format("%1$d°C", intTemp));
            this.mTemperatur = intTemp;
            textViewOrt.setText(jsonObject.getString("name"));
            textViewPressure.setText(String.format("%1d hPa", jsonObject.getJSONObject("main").getInt("pressure")));
            this.mPressure = jsonObject.getJSONObject("main").getInt("pressure");
            textViewMaxTemp.setText(String.format("%1$.0f°C", jsonObject.getJSONObject("main").getDouble("temp_max") - KELVIN));
            textViewMinTemp.setText(String.format("%1$.0f°C", jsonObject.getJSONObject("main").getDouble("temp_min") - KELVIN));
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public int getTemperatur() {
        return this.mTemperatur;
    }

    public int getPressure(){
        return this.mPressure;
    }
}
