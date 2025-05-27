package com.example.myapplication;

import static com.example.myapplication.R.*;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    public RegisterFragment() {}

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

        View view = inflater.inflate(R.layout.register_fragment, container, false);

        EditText nameInput = view.findViewById(R.id.editTextName);
        EditText emailInput = view.findViewById(R.id.editTextEmail);
        EditText passwordInput = view.findViewById(R.id.editTextPassword);
        EditText phoneInput = view.findViewById(id.editTextPhone);
        Button registerBtn = view.findViewById(R.id.buttonRegister);
        Button backBtn = view.findViewById(R.id.buttonBackToLogin);

        registerBtn.setOnClickListener(v -> {
            String name = nameInput.getText().toString();
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            String phone = phoneInput.getText().toString();

            if (email.isEmpty() || password.isEmpty() || name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isValidPhoneNumber = HelpMethods.isValidPhoneNumber(phone);

            if (!isValidPhoneNumber){
                Toast.makeText(getContext(), "The phone number must start with 05 and be 10 digits long.", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String uid = mAuth.getCurrentUser().getUid();
                                Person person = new Person(name, email, uid, phone);

                                usersRef.child(uid).setValue(person);

                                Toast.makeText(getContext(), "Registration successful", Toast.LENGTH_SHORT).show();
                                Navigation.findNavController(v).navigate(R.id.action_blankFragment2_to_blankFragment);
                            } else {
                                Toast.makeText(getContext(), "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        });

        backBtn.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_blankFragment2_to_blankFragment)
        );

        return view;
    }
}
