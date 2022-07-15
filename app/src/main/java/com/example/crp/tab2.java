package com.example.crp;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.renderscript.Element;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;


public class tab2 extends Fragment {

    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<DocumentSnapshot> mComments;
    private List<Chat> mChat;
    private FirebaseFirestore fb;

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fb = FirebaseFirestore.getInstance();
        View view = inflater.inflate(R.layout.fragment_tab2, container, false);
        recyclerView = view.findViewById(R.id.recycler_view2);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager LM = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(LM);

        mComments = new ArrayList<>();
        mChat = new ArrayList<>();

        readComments();

        return view;
    }

    private void readComments() {
        fb.collection("comments").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot collection = task.getResult();
                assert collection != null;
                if (!collection.isEmpty()) {
                    Log.d("TAG", "Collection data: " + collection.getDocuments());
                    mComments = collection.getDocuments();
                } else {
                    Log.d("TAG", "No such document");
                }
            } else {
                Log.d("TAG", "get failed: " + task.getException());
            }
            for (int i = 0; i < mComments.size(); i++) {
                DocumentSnapshot querySnapshot = mComments.get(i);
                HashMap<String, Object> map = (HashMap<String, Object>) querySnapshot.getData();
                String identifier, time, text, commenter;
                List<String> rel, irel;

                identifier = map.get("identifier").toString();
                time = map.get("time").toString();
                text = map.get("text").toString();
                commenter = map.get("commenter").toString();
                rel = (List<String>) map.get("relevance");
                irel = (List<String>) map.get("irrelevance");
                Chat chat = new Chat(commenter, text, time, identifier, rel, irel);
                mChat.add(chat);
            }

            boolean sorted = false;
            Chat temp;
            while (!sorted) {
                sorted = true;
                for (int i = 0; i < mChat.size()-1; i++) {
                    if (mChat.get(i).getRele() < mChat.get(i + 1).getRele()) {
                        temp = mChat.get(i);
                        mChat.set(i, mChat.get(i + 1));
                        mChat.set(i + 1, temp);
                        sorted = false;
                    }
                }
            }
            messageAdapter = new MessageAdapter(getContext(), mChat);
            recyclerView.setAdapter(messageAdapter);
        });
    }
}