package com.dabois.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dabois.databinding.ItemContainerRecentConvBinding;
import com.dabois.listeners.ConversionListner;
import com.dabois.models.ChatMessage;
import com.dabois.models.User;

import java.util.List;

public class RecentConversationsAdapter extends RecyclerView.Adapter<RecentConversationsAdapter.ConversionViewHolder>{

    private final List<ChatMessage> chatMessages;
    private final ConversionListner conversionListner;

    public RecentConversationsAdapter(List<ChatMessage> chatMessages , ConversionListner conversionListner) {
        this.chatMessages = chatMessages;
        this.conversionListner=conversionListner;
    }


    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversionViewHolder(
                ItemContainerRecentConvBinding.inflate(
                        LayoutInflater.from(parent.getContext()),parent,false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ConversionViewHolder holder, int position) {
holder.setData((chatMessages.get(position)));

    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ConversionViewHolder extends RecyclerView.ViewHolder{

        ItemContainerRecentConvBinding binding;

        ConversionViewHolder(ItemContainerRecentConvBinding itemContainerRecentConvBinding){
            super(itemContainerRecentConvBinding.getRoot());
            binding=itemContainerRecentConvBinding;
        }

        void setData(ChatMessage chatMessage){
            binding.textName.setText((chatMessage.conversionName));
            binding.RecentMsg.setText(chatMessage.msg);
            binding.getRoot().setOnClickListener(view -> {
                User user = new User();
                user.id=chatMessage.conversionId;
                user.name=chatMessage.conversionName;
                conversionListner.onConversionClicked(user);

            });
        }
    }

}
