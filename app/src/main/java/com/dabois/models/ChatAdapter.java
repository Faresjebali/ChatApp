package com.dabois.models;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dabois.databinding.ItemContainerReciviedMsgBinding;
import com.dabois.databinding.ItemContainerSentMessageBinding;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


    private List<ChatMessage> chatMessageList;
    private final String senderId;

    private static final int VIEW_TYPE_SENT =1;
    private static final int VIEW_TYPE_REC =2;

    public ChatAdapter(List<ChatMessage> chatMessageList, String senderId) {
        this.chatMessageList = chatMessageList;

        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==VIEW_TYPE_SENT){
return new SentMessageViewHolder((
        ItemContainerSentMessageBinding.inflate(
                LayoutInflater.from(parent.getContext()),parent,false
        )
        ));
        }else{
            return new ReciviedMessageViewHolder(
                    ItemContainerReciviedMsgBinding.inflate(
                            LayoutInflater.from(parent.getContext()),parent,false
                    )
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    if(getItemViewType(position) == VIEW_TYPE_SENT){
        ((SentMessageViewHolder) holder).setData(chatMessageList.get(position));
    }else{
        ((ReciviedMessageViewHolder) holder).setData(chatMessageList.get(position));

    }
    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(chatMessageList.get(position).senderId.equals(senderId)){
            return VIEW_TYPE_SENT;

        }else{
            return VIEW_TYPE_REC;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder{
        private final ItemContainerSentMessageBinding binding;


        SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding){
        super(itemContainerSentMessageBinding.getRoot());
        binding= itemContainerSentMessageBinding;
    }

    void setData(ChatMessage chatMessage){
        binding.Textmsg.setText(chatMessage.msg);
        binding.DateTime.setText(chatMessage.dateTime);

    }
    }

    static class ReciviedMessageViewHolder extends RecyclerView.ViewHolder{

        private final ItemContainerReciviedMsgBinding binding;

        ReciviedMessageViewHolder(ItemContainerReciviedMsgBinding itemContainerReciviedMsgBinding){
            super(itemContainerReciviedMsgBinding.getRoot());
            binding = itemContainerReciviedMsgBinding;
        }

        void setData(ChatMessage chatMessage){
            binding.textmsg.setText(chatMessage.msg);
            binding.DateTime.setText(chatMessage.dateTime);

        }


    }
}
