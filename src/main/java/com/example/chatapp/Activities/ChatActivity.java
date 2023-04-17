package com.example.chatapp.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Base64;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.Adapters.chatAdapter;
import com.example.chatapp.databinding.ActivityChatBinding;
import com.example.chatapp.model.User;
import com.example.chatapp.model.chatMessage;
import com.example.chatapp.utilities.Constants;
import com.example.chatapp.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private User receiver;
    private List<chatMessage> messages;
    private chatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadReceiverDetails();
        setListeners();
        init();
        listenMessages();
    }

    private void listenMessages(){
        firebaseFirestore.collection(Constants.KEY_COLLECTION_CHAT).
                whereEqualTo(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_RECEIVER_ID,receiver.id)
                .addSnapshotListener(eventListener);
        firebaseFirestore.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID,receiver.id)
                .whereEqualTo(Constants.KEY_RECEIVER_ID,preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);

    }

    private void init() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        messages = new ArrayList<>();
        chatAdapter = new chatAdapter(messages, getBitmapFromEncodeString(receiver.image), preferenceManager.getString(Constants.KEY_USER_ID));
        binding.chatRecyclerView.setAdapter(chatAdapter);
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error!=null){
            return;
        }
        if (value!=null)
        {
            int count = messages.size();
            for (DocumentChange documentChange : value.getDocumentChanges()){
                if (documentChange.getType()==DocumentChange.Type.ADDED){
                    chatMessage message = new chatMessage();
                    message.senderId=documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    message.receiverId=documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    message.message=documentChange.getDocument().getString(Constants.KEY_MESSAGE);
                    message.dateTime=getReadableDateTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    message.dateObject=documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    messages.add(message);
                }
            }
            Collections.sort(messages,(obj1,obj2)->obj1.dateObject.compareTo(obj2.dateObject));
            if (count==0){
                chatAdapter.notifyDataSetChanged();
            }else {

                chatAdapter.notifyItemRangeInserted(messages.size(),messages.size());
                binding.chatRecyclerView.smoothScrollToPosition(messages.size()-1);
            }
            binding.chatRecyclerView.setVisibility(View.VISIBLE);
        }
        binding.progressBar.setVisibility(View.GONE);
    };

    private void sendMessage() {
        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVER_ID, receiver.id);
        message.put(Constants.KEY_MESSAGE, binding.InputMessage.getText().toString());
        message.put(Constants.KEY_TIMESTAMP, new Date());
        firebaseFirestore.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        binding.InputMessage.setText(null);
    }

    private void loadReceiverDetails() {
        receiver = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textName.setText(receiver.name);
    }

    private Bitmap getBitmapFromEncodeString(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(view -> onBackPressed());
        binding.imageSendMessage.setOnClickListener(view -> sendMessage());
    }

    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("MMMM dd,yyyy - hh:mm a", Locale.getDefault()).format(date);
    }
}