package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class HomeFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    private EditText editDescription;
    private TextView textString;

    public HomeFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.home_fragment, container, false);

        editDescription = view.findViewById(R.id.editDescription);
        textString = view.findViewById(R.id.textString);
        Button buttonSend = view.findViewById(R.id.buttonInputStr);
        Button buttonImport = view.findViewById(R.id.buttonImportStr);
        Button buttonLogout = view.findViewById(R.id.buttonBackToLogin);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Not logged in", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).navigate(R.id.action_blankFragment3_to_blankFragment);
        }

        String uid = currentUser.getUid();

        buttonSend.setOnClickListener(v -> {
            String inputText = editDescription.getText().toString().trim();
            if (inputText.isEmpty()) {
                Toast.makeText(getContext(), "Please enter text", Toast.LENGTH_SHORT).show();
                return;
            }

            usersRef.child(uid).child("description").setValue(inputText)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(getContext(), "Description saved", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Failed to save", Toast.LENGTH_SHORT).show());
        });

        buttonImport.setOnClickListener(v -> {
            usersRef.child(uid).child("description").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String desc = snapshot.getValue(String.class);
                            if (desc != null) {
                                textString.setText(desc);
                            } else {
                                textString.setText("No description found.");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getContext(), "Failed to load", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        buttonLogout.setOnClickListener(v -> {
            usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = snapshot.child("name").getValue(String.class);
                    if (name == null) name = "User";

                    Toast.makeText(getContext(), name + " is logout...", Toast.LENGTH_SHORT).show();

                    mAuth.signOut();
                    Navigation.findNavController(v).navigate(R.id.action_blankFragment3_to_blankFragment);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Error accessing user data", Toast.LENGTH_SHORT).show();
                }
            });
        });


        return view;
    }
}
