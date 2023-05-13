package com.dabois.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.dabois.R;
import com.dabois.adapters.RecentConversationsAdapter;
import com.dabois.databinding.ActivityMainBinding;
import com.dabois.listeners.ConversionListner;
import com.dabois.models.ChatMessage;
import com.dabois.models.User;
import com.dabois.utilities.Constants;
import com.dabois.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ConversionListner {

    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;

    private List<ChatMessage> conversatinos;
    private RecentConversationsAdapter conversationsAdapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        init();
        loadUser();
        getToken();
        setListner();
        listenConversations();


    }

    private void init(){
        conversatinos=new ArrayList<>();
        conversationsAdapter=new RecentConversationsAdapter(conversatinos,this);
        binding.ConversationRv.setAdapter(conversationsAdapter);
        db=FirebaseFirestore.getInstance();
    }

    private void setListner() {
        binding.imglogout.setOnClickListener(view -> signOut());
        binding.NewChat.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(),UsersActivity.class));
        });
    }

    private void loadUser() {
        binding.textName.setText(PreferenceManager.getString(Constants.KEY_NAME));

    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    private void listenConversations(){
        db.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        db.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECV_ID,preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener =(value,error) ->{
        if (error != null) {
            return;
        }
        if(value !=null){
            for(DocumentChange documentChange :value.getDocumentChanges()){
                if(documentChange.getType() == DocumentChange.Type.ADDED){
                    String senderid=documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    String recvid=documentChange.getDocument().getString(Constants.KEY_RECV_ID);
                    ChatMessage chatMessage=new ChatMessage();
                    chatMessage.senderId = senderid;
                    chatMessage.recvId=recvid;
                    if(preferenceManager.getString(Constants.KEY_USER_ID).equals(senderid)){
                        chatMessage.conversionName=documentChange.getDocument().getString(Constants.KEY_RECV_NAME);
                        chatMessage.conversionId=documentChange.getDocument().getString(Constants.KEY_RECV_ID);
                    }else{
                        chatMessage.conversionName=documentChange.getDocument().getString(Constants.KEY_SENDER_Name);
                        chatMessage.conversionId=documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    }
                    chatMessage.msg= documentChange.getDocument().getString(Constants.KEY_LAST_MSG);
                    chatMessage.dateobject= documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    conversatinos.add(chatMessage);
                } else if (documentChange.getType()== DocumentChange.Type.MODIFIED) {
                    for (int i = 0; i < conversatinos.size(); i++) {
                        String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        String recvId = documentChange.getDocument().getString(Constants.KEY_RECV_ID);
                        if(conversatinos.get(i).senderId.equals(senderId) && conversatinos.get(i).recvId.equals(recvId)){
                            conversatinos.get(i).msg = documentChange.getDocument().getString(Constants.KEY_LAST_MSG);
                            conversatinos.get(i).dateobject=documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                            break;
                        }

                    }

                }
            }
            Collections.sort(conversatinos,(obj1,obj2) -> obj2.dateobject.compareTo(obj1.dateobject));
            conversationsAdapter.notifyDataSetChanged();
            binding.ConversationRv.smoothScrollToPosition(0);
            binding.ConversationRv.setVisibility(View.VISIBLE);
            binding.pgbar.setVisibility(View.GONE);
        }
    };
    private void updateToken(String token) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                db.collection(Constants.KEY_COLELCTION).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        documentReference.update(Constants.KEY_FCM_TOKEN, token)

                .addOnFailureListener(e -> showToast("Unable to Update token dzl bb"));

    }

    private void signOut() {
        showToast("E9leb mandhrek ♥");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        /*DocumentReference documentReference =
                db.collection(Constants.KEY_COLELCTION).document(
                        preferenceManager.getString(Constants.KEY_IS_SIGNED_IN)
                );
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    PreferenceManager.Clear();

                }).addOnFailureListener(e -> showToast("Maandk win mechi :*"));
    }*/
        startActivity(new Intent(getApplicationContext(), SignIn.class));
        finish();
    }
    public void onConversionClicked(User user){
        Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
        intent.putExtra(Constants.KEY_USER,user);
        startActivity(intent);
    }
}