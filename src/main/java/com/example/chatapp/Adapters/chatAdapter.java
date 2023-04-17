package com.example.chatapp.Adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.databinding.ItemContainerReceiveMessageBinding;
import com.example.chatapp.databinding.ItemContainerSentMessageBinding;
import com.example.chatapp.model.chatMessage;

import java.util.List;

public class chatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final List<chatMessage> messages;
    private final Bitmap receiverProfileImage;
    private final String sentId;

    public final static int VIEW_TYPE_SENT=1;
    public final static int VIEW_TYPE_RECEIVE=2;

    public chatAdapter(List<chatMessage> messages, Bitmap receiverProfileImage, String sentId) {
        this.messages = messages;
        this.receiverProfileImage = receiverProfileImage;
        this.sentId = sentId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==VIEW_TYPE_SENT){
            return new sentMessageViewHolder(ItemContainerSentMessageBinding.inflate(
                    LayoutInflater.from(parent.getContext()),
                    parent,
                    false
            ));
        }else {
            return new receiveMessageViewHolder(ItemContainerReceiveMessageBinding.inflate(
                    LayoutInflater.from(parent.getContext()),
                    parent,
                    false
            ));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position)==VIEW_TYPE_SENT){
            ((sentMessageViewHolder)holder).setData(messages.get(position));
        }else {
            ((receiveMessageViewHolder)holder).setData(messages.get(position),receiverProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).senderId.equals(sentId))
        {
            return VIEW_TYPE_SENT;
        }else {
            return VIEW_TYPE_RECEIVE;
        }
    }

    class sentMessageViewHolder extends RecyclerView.ViewHolder{
        private ItemContainerSentMessageBinding binding;

         sentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding) {
            super(itemContainerSentMessageBinding.getRoot());
            binding=itemContainerSentMessageBinding;
        }
        void setData(chatMessage chatMessage){
             binding.textMessage.setText(chatMessage.message);
             binding.textDateTime.setText(chatMessage.dateTime);
        }
    }
    class receiveMessageViewHolder extends RecyclerView.ViewHolder{
        private ItemContainerReceiveMessageBinding binding;

        public receiveMessageViewHolder(ItemContainerReceiveMessageBinding itemContainerReceiveMessageBinding) {
            super(itemContainerReceiveMessageBinding.getRoot());
            binding=itemContainerReceiveMessageBinding;
        }
        void setData(chatMessage chatMessage,Bitmap ProfileImage){
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
            binding.imageProfile.setImageBitmap(ProfileImage);
        }
    }
}
