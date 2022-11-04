package com.example.petfeederthird;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;

import javax.net.ssl.HttpsURLConnection;

public class Fragment2 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_2, container, false);

        RelativeLayout save = view.findViewById(R.id.save);
        save.setOnClickListener(v -> {
            int portions = Integer.parseInt( ( (EditText) getActivity().findViewById(R.id.portions) ).getText().toString() );
            int use_schedule = ((RadioGroup) getActivity().findViewById(R.id.schedule_radio)).getCheckedRadioButtonId();
            int use_rfid = ((RadioGroup) getActivity().findViewById(R.id.rfid_radio)).getCheckedRadioButtonId();
            saveDiet(portions, use_schedule, use_rfid);
            Listener listener = (Listener) getActivity();
            listener.onButtonPressed();
        });

        // Cats or Dogs
        /*
        animals.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String item = parent.getSelectedItem().toString();
                int array;
                if (item.equals("Cão")) {
                    array = R.array.dbreeds;
                } else {
                    array = R.array.cbreeds;
                }
                Spinner breeds = getView().findViewById(R.id.breedSpinner);
                ArrayAdapter<CharSequence> breedAdapter = ArrayAdapter.createFromResource(getActivity(), array, R.layout.spinner_item);
                breeds.setAdapter(breedAdapter);
                if (MainActivity.isFilePresent(getActivity(), "diet.json")) {
                    try {
                        JSONObject json = new JSONObject(MainActivity.read(getActivity(), "diet.json"));
                        breeds.setSelection(json.getInt("breed"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        */

        /*
        ArrayAdapter<CharSequence> animalAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.animals, R.layout.spinner_item);
        animalAdapter.setDropDownViewResource(R.layout.spinner_item);
        animals.setAdapter(animalAdapter);
        */

        //Toast.makeText(getActivity(), "View created", Toast.LENGTH_SHORT).show();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (MainActivity.isFilePresent(getContext(), "diet.json")) {
            String string = read(getActivity(), "diet.json");
            setSpinners(string);
        }
    }


    public void setSpinners(String json) {
        try {
            JSONObject diet = new JSONObject(json);
            EditText portions = getActivity().findViewById(R.id.portions);
            portions.setText(String.valueOf(diet.getInt("portions")));
            RadioGroup radioGroup = getActivity().findViewById(R.id.schedule_radio);
            RadioGroup rfidRadioGroup = getActivity().findViewById(R.id.rfid_radio);
            radioGroup.check(diet.getInt("use_schedule"));
            rfidRadioGroup.check(diet.getInt("use_rfid"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void saveDiet(int portions, int use_schedule, int use_rfid) {
        //Toast.makeText(getContext(), "Sheeeit", Toast.LENGTH_SHORT).show();

        JSONObject json = new JSONObject();
        try {
            json.put("portions", portions);
            json.put("use_schedule", use_schedule);
            json.put("use_rfid", use_rfid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            // Save locally
            FileOutputStream fos = getActivity().openFileOutput("diet.json", Context.MODE_PRIVATE);
            fos.write(json.toString().getBytes());
            fos.close();

            // Save on Firebase
            Listener listener = (Listener) getActivity();
            DatabaseReference reference = listener.reference();
            reference.child("portions").setValue(portions);
            reference.child("useSchedule").setValue(use_schedule != R.id.no);
            reference.child("useRFID").setValue(use_rfid != R.id.rfid_no);

        } catch (IOException ignored) {
            Toast.makeText(getContext(), ignored.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private String read(Context context, String fileName) {
        try {
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException fileNotFound) {
            return null;
        }
    }
}