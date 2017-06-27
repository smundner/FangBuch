package de.shm.fangbuch;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Start.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Start#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Start extends Fragment {


    private OnFragmentInteractionListener mListener;
    private final static String LON = "LON";
    private final static String LAT = "LAT";

    //private final static double KELVIN = 273.14;

    private final static String START_FRAGMENT = "start_fragment";
    //private final static String WEATHER_DATA = "weather_data";
    //private final static String GEO_DATA = "geo_data";

    private double latitude;
    private double longtitude;
    //private JSONObject mWeatherData;

    public Start() {
        // Required empty public constructor
    }




    public static Start newInstance(double lon, double lat) {
        Start fragment = new Start();
        Bundle args = new Bundle();
        args.putDouble(LON,lon);
        args.putDouble(LAT,lat);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments()!=null){
            longtitude = getArguments().getDouble(LON);
            latitude = getArguments().getDouble(LAT);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_start, container, false);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Weather weather = Weather.newInstance(longtitude,latitude);
        transaction.replace(R.id.startFragmentContainer,weather,Weather.WEATHER_FRAGMENT);
        transaction.commit();

        /*
        SharedPreferences sharedPreferences = getActivity().getPreferences(getActivity().MODE_PRIVATE);
        try {
            mWeatherData = new JSONObject(sharedPreferences.getString(WEATHER_DATA,null));
        } catch (JSONException e) {
            e.printStackTrace();
            mWeatherData = null;
            getWeatherData(longtitude,latitude);
        }catch (NullPointerException e){
            e.printStackTrace();
            mWeatherData = null;
            getWeatherData(longtitude,latitude);
        }
        if(mWeatherData!=null) {
            setWeatherCard(view, mWeatherData);
        }*/
        return view;
    }

    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
        latitude=lat;
        longtitude=lon;
        //getWeatherData(lon,lat);

    }
    /*
    private void getWeatherData(double lon, double lat){
        Log.d(START_FRAGMENT,"GPS data -> Lon: "+Double.toString(lon)+"Lat: " + Double.toString(lat));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String url = "http://api.openweathermap.org/data/2.5/weather?lat="+Double.toString(lat)+"&lon="+Double.toString(lon)+"&APPID=064d2048fffea5ad0495f3bee1be8fb2";
        Log.d(START_FRAGMENT,"WeatherURL ->" + url);

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
    }*/
    /*
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
            textViewOrt.setText(jsonObject.getString("name"));
            textViewPressure.setText(String.format("%1d hPa", jsonObject.getJSONObject("main").getInt("pressure")));
            textViewMaxTemp.setText(String.format("%1$.0f°C", jsonObject.getJSONObject("main").getDouble("temp_max") - KELVIN));
            textViewMinTemp.setText(String.format("%1$.0f°C", jsonObject.getJSONObject("main").getDouble("temp_min") - KELVIN));
        }catch (JSONException e){
            e.printStackTrace();
        }


    }*/
}
