package com.example.petfeederthird;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class Fragment1 extends Fragment {

    ArrayList<ScheduleModel> items = new ArrayList<>();
    ScheduleAdapter adapter;
    RecyclerView schedule;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_1, container, false);

        Button button = view.findViewById(R.id.addTime);
        button.setOnClickListener(v -> {
            View timeView = inflater.inflate(R.layout.add_time, null);
            PopupWindow popupWindow = new PopupWindow(
                    timeView,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    true);
            popupWindow.showAtLocation(timeView, Gravity.CENTER, 0, 0);

            Button select = timeView.findViewById(R.id.ok_time);
            select.setOnClickListener(v1 -> {
                TimePicker timePicker = timeView.findViewById(R.id.timePicker);
                timePicker.setIs24HourView(true);
                String time = "";
                if (timePicker.getHour() < 10) {
                    time += "0" + timePicker.getHour() + ":";
                } else {
                    time += timePicker.getHour() + ":";
                }
                if (timePicker.getMinute() < 10) {
                    time += "0" + timePicker.getMinute();
                } else {
                    time += timePicker.getMinute();
                }
                items.add(items.size(), new ScheduleModel(time));
                adapter.notifyItemInserted(items.size());
                popupWindow.dismiss();
            });
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new ScheduleAdapter(getActivity(), items);

        schedule = view.findViewById(R.id.schedule);
        schedule.setAdapter(adapter);
        schedule.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public void setItems(String json) {
        try {
            JSONObject schedule = new JSONObject(json);
            Iterator<String> keys = schedule.keys();
            while (keys.hasNext()) {
                items.add(new ScheduleModel(schedule.getString(keys.next())));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void saveSchedule() {
        try {
            // Save locally
            FileOutputStream fos = getActivity().openFileOutput("schedule.json", Context.MODE_PRIVATE);
            fos.write(toJson(items).getBytes());
            fos.close();

            // Save on Firebase
            Listener listener = (Listener) getActivity();
            DatabaseReference reference = listener.reference();
            reference.child("schedule").setValue(items);

            Toast.makeText(getContext(), "Dados salvos", Toast.LENGTH_SHORT).show();

        } catch (IOException ignored) {
            Toast.makeText(getContext(), ignored.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private String toJson(ArrayList<ScheduleModel> array) {
        String converted = "{";
        for (int i = 0; i < array.size()-1; i++) {
            converted += String.format("\"%s\":\"%s\",", i, array.get(i).getTime());
        }
        int i = array.size()-1;
        converted += String.format("\"%s\":\"%s\"", i, array.get(i).getTime());
        converted += "}";
        return converted;
    }
}