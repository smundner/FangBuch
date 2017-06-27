package de.shm.fangbuch;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddFish.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddFish#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFish extends Fragment {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 2;

    public final static String ADD_FISH = "AddFish";

    public final static String LON = "LON";
    public final static String LAT = "LAT";
    public final static String ART = "ART";
    public final static String GROESSE = "groesse";
    public final static String GEWICHT = "gewicht";
    public final static String LOCATION = "ort";
    public final static String KOEDER = "Koeder";
    public final static String TEMPERATUR = "Temperatur";
    public final static String LUFTDRUCK = "Luftdruck";
    public final static String WETTER = "Wetter";

    private double latitude;
    private double longtitude;
    private Weather mWeather;

    Spinner spinnerChooseFish;
    EditText editTextSize;
    EditText editTextGewicht;
    EditText editTextLocation;
    AutoCompleteTextView autoCompleteTextViewBait;

    private String mCurrentPhotoPath;

    public AddFish() {
        // Required empty public constructor
    }

    public static AddFish newInstance(double lon, double lat) {
        AddFish fragment = new AddFish();
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
        FloatingActionButton floatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        floatingActionButton.hide();
        View view = inflater.inflate(R.layout.fragment_addfish, container, false);
        ImageButton imageButtonAddPicture = (ImageButton) view.findViewById(R.id.imageButtonNewFishAddPicture);
        imageButtonAddPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        spinnerChooseFish = (Spinner) view.findViewById(R.id.spinnerChooseFish);
        String[] fishes = getActivity().getResources().getStringArray(R.array.fishes);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(),android.R.layout.simple_spinner_dropdown_item,fishes);
        spinnerChooseFish.setAdapter(adapter);


        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        mWeather = Weather.newInstance(longtitude,latitude);

        transaction.replace(R.id.addFishFragment,mWeather,Weather.WEATHER_FRAGMENT);
        transaction.commit();

        final Button buttonFishDeclair = (Button) view.findViewById(R.id.buttonNewFishDeclair);
        buttonFishDeclair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               FragmentManager fm = getActivity().getSupportFragmentManager();
                FishDeclair fd = FishDeclair.newInstance(spinnerChooseFish.getSelectedItem().toString());
                fd.show(fm,FishDeclair.FISH_DECLAIR);
            }
        });

        final Button buttonSaveFish = (Button) view.findViewById(R.id.buttonNewFishSave);
        buttonSaveFish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFisch();

            }
        });

        editTextSize = (EditText) view.findViewById(R.id.editTextNewFishSize) ;
        editTextGewicht = (EditText) view.findViewById(R.id.editTextNewFishWeight);
        editTextLocation = (EditText) view.findViewById(R.id.editTextNewFishLocation);
        autoCompleteTextViewBait = (AutoCompleteTextView) view.findViewById(R.id.autoComTextViewNewFishBait);

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
        FloatingActionButton floatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        floatingActionButton.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            final ImageView imageViewPicture = (ImageView) getView().findViewById(R.id.imageViewNewFishPicture);
            imageViewPicture.setImageBitmap(imageBitmap);
        }
        if(requestCode == REQUEST_TAKE_PHOTO && resultCode == getActivity().RESULT_OK){
            setPic();
        }
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getActivity().getPackageManager())!=null){
            File photoFile = null;
            try {
                photoFile = creatImageFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            if(photoFile!=null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),"de.shm.fangbuch",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File creatImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,".jpg",storageDir);
        mCurrentPhotoPath="file:"+image.getAbsolutePath();
        return image;
    }

    private void setPic(){
        ImageView imageViewNewFishPic = (ImageView) getView().findViewById(R.id.imageViewNewFishPicture);
        int targetW = imageViewNewFishPic.getWidth();
        int targetH = imageViewNewFishPic.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath.replaceFirst("file:",""),bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW/targetW,photoH/targetH);

        bmOptions.inJustDecodeBounds=false;
        bmOptions.inSampleSize=scaleFactor;
        bmOptions.inPurgeable=true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath.replaceFirst("file:",""),bmOptions);
        imageViewNewFishPic.setImageBitmap(bitmap);
    }

    private void saveFisch(){
        JSONObject store = new JSONObject();
        try {
            store.put(LAT,latitude);
            store.put(LON,longtitude);
            String fischArt = spinnerChooseFish.getSelectedItem().toString();
            store.put(ART,fischArt);
            store.put(GROESSE,Long.parseLong(editTextSize.getText().toString()));
            store.put(GEWICHT,Long.parseLong(editTextGewicht.getText().toString()));
            store.put(LOCATION,editTextLocation.getText().toString());
            store.put(KOEDER,autoCompleteTextViewBait.getText().toString());
            store.put(TEMPERATUR,mWeather.getTemperatur());
            store.put(LUFTDRUCK,mWeather.getPressure());
            Log.d(ADD_FISH,store.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
    }

}
