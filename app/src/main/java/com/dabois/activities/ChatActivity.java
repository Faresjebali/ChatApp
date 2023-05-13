package com.dabois.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;


import com.dabois.databinding.ActivityChatBinding;
import com.dabois.models.ChatAdapter;
import com.dabois.models.ChatMessage;
import com.dabois.models.User;
import com.dabois.utilities.Constants;
import com.dabois.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.C;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private User reciviedUser;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore db;
    private String conversionId=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SetListner();
        LoadRec();
        init();
        listenmsg();
    }

    private void init(){
        preferenceManager = new PreferenceManager((getApplicationContext()));
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(
                chatMessages,PreferenceManager.getString((Constants.KEY_USER_ID))
        );
        binding.chatRv.setAdapter(chatAdapter);
        db=FirebaseFirestore.getInstance();
    }

    public void sendMsg(){
        HashMap<String ,Object> msg = new HashMap<>();
        msg.put(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID));
        msg.put(Constants.KEY_RECV_ID,reciviedUser.id);
        msg.put(Constants.KEY_MSG,binding.inputmsg.getText().toString());
        msg.put(Constants.KEY_TIMESTAMP,new Date());
        db.collection(Constants.KEY_CHAT).add(msg);
        if(conversionId !=null){
            updateConversion(binding.inputmsg.getText().toString());
        }else{
            HashMap<String,Object> conversion = new HashMap<>();
            conversion.put(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID));
            conversion.put(Constants.KEY_SENDER_Name,preferenceManager.getString(Constants.KEY_NAME));
            conversion.put(Constants.KEY_RECV_ID,reciviedUser.id);
            conversion.put(Constants.KEY_RECV_NAME,reciviedUser.name);
            conversion.put(Constants.KEY_LAST_MSG,binding.inputmsg.getText().toString());
            conversion.put(Constants.KEY_TIMESTAMP,new Date());
            addConversion(conversion);
        }
        binding.inputmsg.setText(null);
    }

    private void listenmsg(){
        db.collection(Constants.KEY_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_RECV_ID,reciviedUser.id)
                .addSnapshotListener(eventListener);
        db.collection(Constants.KEY_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID,reciviedUser.id)
                .whereEqualTo(Constants.KEY_RECV_ID,preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);


    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) ->{
        if(error !=null){
            return;
        }
        if(value !=null){
            int count= chatMessages.size();
            for(DocumentChange documentChange : value.getDocumentChanges()){
                if(documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    chatMessage.recvId = documentChange.getDocument().getString(Constants.KEY_RECV_ID);
                    chatMessage.msg = documentChange.getDocument().getString(Constants.KEY_MSG);
                    chatMessage.dateTime = getDatee(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    chatMessage.dateobject = documentChange.getDocument().getDate((Constants.KEY_TIMESTAMP));
                    chatMessages.add(chatMessage);
                }

            }
            Collections.sort(chatMessages,(obj1,obj2) -> obj1.dateobject.compareTo(obj2.dateobject));
            if(count ==0){
                chatAdapter.notifyDataSetChanged();

            }else{
                chatAdapter.notifyItemRangeInserted(chatMessages.size(),chatMessages.size());
                binding.chatRv.smoothScrollToPosition(chatMessages.size() -1);
            }
            binding.chatRv.setVisibility(View.VISIBLE);
        }
        binding.pgBaar.setVisibility(View.GONE);
        if(conversionId==null){
            checkForConversion();
        }
    };

    private void LoadRec(){
        reciviedUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textName.setText((reciviedUser.name));
    }

    private void SetListner(){
        binding.imgBack.setOnClickListener(view -> onBackPressed());
        binding.laySend.setOnClickListener(v-> sendMsg());
    }
    private String getDatee(Date date){
        return new SimpleDateFormat("dd,yyyy-hh:mm a", Locale.getDefault()).format(date);

    }

    private void addConversion(HashMap<String,Object> conversion){
        db.collection((Constants.KEY_COLLECTION_CONVERSATIONS))
                .add(conversion)
                .addOnSuccessListener(documentReference -> conversionId=documentReference.getId());
    }

    private void updateConversion(String msg){
        DocumentReference documentReference=
                db.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(conversionId);
        documentReference.update(
                Constants.KEY_LAST_MSG,msg,
                Constants.KEY_TIMESTAMP,new Date()
                );
    }

    private void checkForConversion(){
        if(chatMessages.size() !=0){
            checkForConversionRemotely(
                    preferenceManager.getString(Constants.KEY_USER_ID),
                    reciviedUser.id
            );
            checkForConversionRemotely(
                    reciviedUser.id,
                    preferenceManager.getString(Constants.KEY_USER_ID)
            );
        }
    }

    private void checkForConversionRemotely(String senderId,String receiverId){
        db.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID,senderId)
                .whereEqualTo(Constants.KEY_RECV_ID,receiverId)
                .get()
                .addOnCompleteListener(conversionOnCompleteListner);
    }

    private final OnCompleteListener<QuerySnapshot> conversionOnCompleteListner = task -> {
        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversionId = documentSnapshot.getId();
        }

    };

}