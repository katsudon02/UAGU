package com.example.petfeederthird;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class Fragment3 extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_3, container, false);

        // Get database reference from MainActivity
        Listener listener = (Listener) getActivity();
        DatabaseReference ref = listener.reference();

        RelativeLayout drop = view.findViewById(R.id.drop);
        drop.setOnClickListener(v -> {
            // Save on Firebase
            ref.child("dropNow").setValue(true);
        });

        // Dispenser event listener
        ref.child("lastActivated").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TextView fountain = view.findViewById(R.id.dispenser_date);
                String dispenserDate = snapshot.getValue(String.class);
                fountain.setText(dispenserDate.replace(".", " - "));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Erro ao atualizar status", Toast.LENGTH_SHORT).show();
            }
        });

        // Deposit event listener
        ref.child("depositLevel").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TextView deposit = view.findViewById(R.id.deposit_text);
                int value = snapshot.getValue(Integer.class);
                deposit.setText(value + "%");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Erro ao atualizar status", Toast.LENGTH_SHORT).show();
            }
        });

        // Fountain event listener
        ref.child("fountainLevel").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TextView fountain = view.findViewById(R.id.fountain_text);
                int fountainLevel = snapshot.getValue(Boolean.class) ? 0 : 1;
                fountain.setText(getResources().getStringArray(R.array.water_status)[fountainLevel]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Erro ao atualizar status", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}