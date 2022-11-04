package com.example.petfeederthird;

import com.google.firebase.database.DatabaseReference;

public interface Listener {
    void onButtonPressed();
    DatabaseReference reference();
}
