package de.shm.fangbuch;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FishDeclair.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FishDeclair#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FishDeclair extends DialogFragment {

    private static final String FISH = "fish";

    public static final String FISH_DECLAIR="FISH_DECLAIR";

    // TODO: Rename and change types of parameters
    private String mFish;


    private OnFragmentInteractionListener mListener;

    public FishDeclair() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static FishDeclair newInstance(String fish) {
        FishDeclair fragment = new FishDeclair();
        Bundle args = new Bundle();
        args.putString(FISH, fish);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFish = getArguments().getString(FISH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view = inflater.inflate(R.layout.fragment_fish_declair, container, false);
        TextView textViewTitle = (TextView) view.findViewById(R.id.textViewFishDeclairTitle);
        textViewTitle.setText(mFish);

        Spinner spinnerBundesland = (Spinner) view.findViewById(R.id.spinnerChooseCountry);
        String[] bundesLaender = getActivity().getResources().getStringArray(R.array.bundeslaender);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(),android.R.layout.simple_spinner_dropdown_item,bundesLaender);
        spinnerBundesland.setAdapter(adapter);

        ImageButton imageButtonClose = (ImageButton) view.findViewById(R.id.imageViewFishDeclairClose);
        imageButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FishDeclair fd = (FishDeclair) getActivity().getSupportFragmentManager().findFragmentByTag(FishDeclair.FISH_DECLAIR);
                fd.dismiss();
            }
        });
        return view;
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
