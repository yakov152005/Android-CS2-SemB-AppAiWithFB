package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    public LoginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.login_fragment, container, false);

        EditText emailInput = view.findViewById(R.id.editTextEmail);
        EditText passwordInput = view.findViewById(R.id.editTextPassword);
        Button loginBtn = view.findViewById(R.id.buttonLogin);
        Button registerBtn = view.findViewById(R.id.buttonRegister);

        registerBtn.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_blankFragment_to_blankFragment2)
        );

        loginBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();

            if (email.isEmpty() && password.isEmpty()) {
                Toast.makeText(getContext(), "Please enter email and password", Toast.LENGTH_SHORT).show();
            } else if (email.isEmpty()) {
                Toast.makeText(getContext(), "Please enter email", Toast.LENGTH_SHORT).show();
            } else if (password.isEmpty()) {
                Toast.makeText(getContext(), "Please enter password", Toast.LENGTH_SHORT).show();
            } else {

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

                                    usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Person person = snapshot.getValue(Person.class);
                                            if (person != null) {
                                                Toast.makeText(getContext(), "Welcome " + person.getName(), Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getContext(), "User data not found", Toast.LENGTH_SHORT).show();
                                            }

                                            Navigation.findNavController(v).navigate(R.id.action_blankFragment_to_blankFragment3);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(getContext(), "Database error", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                } else {
                                    Toast.makeText(getContext(), "Login failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        return view;
    }
}
