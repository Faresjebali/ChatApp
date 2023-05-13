package com.dabois.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dabois.R;
import com.dabois.adapters.UserAdapter;
import com.dabois.databinding.ActivityUsersBinding;
import com.dabois.listeners.UserListener;
import com.dabois.models.User;
import com.dabois.utilities.Constants;
import com.dabois.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity  implements UserListener {
    private ActivityUsersBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager((getApplicationContext()));
        getUser();
        setListner();
    }

    private void setListner(){
        binding.imgBack.setOnClickListener(v-> onBackPressed());
    }
    private void getUser(){
        loading(true);
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLELCTION)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                   if(task.isSuccessful() && task.getResult() !=null){
                       List<User> users = new ArrayList<>();
                       for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                           if(currentUserId.equals(queryDocumentSnapshot.getId())){
                               continue;
                           }
                           User user = new User();
                           user.name=queryDocumentSnapshot.getString(Constants.KEY_NAME);
                           user.email=queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                           user.token=queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                           user.id=queryDocumentSnapshot.getId();
                           users.add(user);
                       }
                       if(users.size()>0){
                           UserAdapter usersAdapter = new UserAdapter(users,this);
                           binding.usersRv.setAdapter(usersAdapter);
                           binding.usersRv.setVisibility(View.VISIBLE);
                       }else{
                           ShowErrMsg();
                       }
                    }
                   else{
                        ShowErrMsg();
                    }}
                );
    }

    private void ShowErrMsg(){
        binding.errormsg.setText(String.format("%s","No User Available"));
        binding.errormsg.setVisibility(View.VISIBLE);

    }
    private void loading(Boolean isLoading){
        if(isLoading){
            binding.pgbar.setVisibility(View.VISIBLE);
        }else{
            binding.pgbar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void OnUserClicked(User user) {
        Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
        intent.putExtra(Constants.KEY_USER,user);
        startActivity(intent);
        finish();
    }
}
