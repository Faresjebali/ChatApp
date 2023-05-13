package com.dabois.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.dabois.databinding.ActivitySignInBinding;
import com.dabois.utilities.Constants;
import com.dabois.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignIn extends AppCompatActivity {

   private ActivitySignInBinding binding;
   private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());

        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListners();

    }
    private void setListners(){
        binding.textCreate.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(),SignUp.class)));
        binding.ButtonSignIn.setOnClickListener(view -> {
            if(isValid()){
                signIn();
            }
        });

    }

    private void signIn(){
        loading(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection((Constants.KEY_COLELCTION))
                .whereEqualTo(Constants.KEY_EMAIL,binding.inputEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASS,binding.inputPass.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() !=null
                            && task.getResult().getDocuments().size()>0){
                        DocumentSnapshot  documentSnapshot=task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_NAME,documentSnapshot.getString(Constants.KEY_NAME));
                        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }else{
                        loading(false);
                        showToast("Unable to Login dzl hh");
                    }
                });

    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.ButtonSignIn.setVisibility(View.INVISIBLE);
            binding.progBar.setVisibility(View.VISIBLE);
        }else{
            binding.progBar.setVisibility(View.INVISIBLE);
            binding.ButtonSignIn.setVisibility(View.VISIBLE);
        }
    }
    private void showToast(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }



    private Boolean isValid(){
        if(binding.inputEmail.getText().toString().trim().isEmpty()){
            showToast("Enter Email yezi ble bhema");
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()){
            showToast("Valid Email bjeh l rab");
            return false;
        }else if(binding.inputPass.getText().toString().trim().isEmpty()){
            showToast("Password alooo???");
            return false;
        }else return true;
    }

}