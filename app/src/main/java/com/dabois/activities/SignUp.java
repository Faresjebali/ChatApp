package com.dabois.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.dabois.databinding.ActivitySignInBinding;
import com.dabois.databinding.ActivitySignUpBinding;
import com.dabois.utilities.Constants;
import com.dabois.utilities.PreferenceManager;
import com.google.common.cache.LoadingCache;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignUp extends AppCompatActivity {
private ActivitySignUpBinding binding;
private PreferenceManager preferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
       

    }

    private void setListeners() {
        binding.textAlready.setOnClickListener(v -> onBackPressed());
        binding.ButtonSignup.setOnClickListener(v ->{
            if(isValid()){
                signUp();
            }
        });
    }
    private void showToast(String msg){
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
    }

    private void signUp(){
        loading(true);
        FirebaseFirestore data = FirebaseFirestore.getInstance();
        HashMap<String,Object> user =new HashMap<>();
        user.put(Constants.KEY_NAME,binding.inputName.getText().toString());
        user.put(Constants.KEY_EMAIL,binding.inputEmail.getText().toString());
        user.put(Constants.KEY_PASS,binding.inputPass.getText().toString());
        data.collection(Constants.KEY_COLELCTION)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                    preferenceManager.putString(Constants.KEY_USER_ID,documentReference.getId());
                    preferenceManager.putString(Constants.KEY_NAME,binding.inputName.getText().toString());
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                })
                .addOnFailureListener(exception ->{
                    loading(false);
                    showToast(exception.getMessage());


                });

    }

    private boolean isValid(){
        if(binding.inputName.getText().toString().trim().isEmpty()){
            showToast("Enter Your name ");
            return false;
        } else if (binding.inputEmail.getText().toString().trim().isEmpty()) {
            showToast("Enter your Email");
            return false;

        } else if (binding.inputPass.getText().toString().trim().isEmpty()) {
            showToast("Enter your Password");
            return false;

        }else if(binding.ConfirmPass.getText().toString().trim().isEmpty()){
            showToast("Confirm Your Password ");
            return false;
        }else if(!binding.inputPass.getText().toString().equals(binding.ConfirmPass.getText().toString())){
            showToast("Verify your PassWord stp Rak Karztni");
            return false;
        }else
            return true;

    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.ButtonSignup.setVisibility(View.INVISIBLE);
            binding.progBar.setVisibility(View.VISIBLE);
        }else{
            binding.progBar.setVisibility(View.INVISIBLE);
            binding.ButtonSignup.setVisibility(View.VISIBLE);
        }
    }

}