package com.example.crp;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;


public class tab1 extends Fragment {

    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<DocumentSnapshot> mComments;
    private List<Chat> mChat;
    private FirebaseFirestore fb;
    private EditText popUpTxt;
    private Button popUpBtn_comment, popUpBtn_cancel;
    private AlertDialog dialog_comment;
    private AlertDialog.Builder dialogBuilder_comment;
    private String name;

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fb = FirebaseFirestore.getInstance();
        View view = inflater.inflate(R.layout.fragment_tab1, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager LM = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(LM);

        mComments = new ArrayList<>();
        mChat = new ArrayList<>();

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(view1 -> {
            dialogBuilder_comment = new AlertDialog.Builder(getContext());
            final View commentPopup = getLayoutInflater().inflate(R.layout.popup_window, null);

            popUpBtn_cancel = (Button) commentPopup.findViewById(R.id.popUpBtnCancel);
            popUpBtn_comment = (Button) commentPopup.findViewById(R.id.popUpBtnPublish);
            popUpTxt = (EditText) commentPopup.findViewById(R.id.comentary);

            String uuid = UUID.randomUUID().toString();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String currentTime = dtf.format(now);

            dialogBuilder_comment.setView(commentPopup);
            dialog_comment = dialogBuilder_comment.create();
            dialog_comment.show();

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser user = firebaseAuth.getCurrentUser();

            assert user != null;
            String userId = user.getUid();
            fb.collection("users").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        name = document.getData().get("username").toString();
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed: " + task.getException());
                }
            });

            popUpBtn_comment.setOnClickListener(view2 -> {
                String getComment = popUpTxt.getText().toString();
                List<String> rel = new ArrayList<>(), irel = new ArrayList<>();
                if (TextUtils.isEmpty(getComment)) {
                    Toast.makeText(getContext(), "Por favor intruduza o seu comentário", Toast.LENGTH_LONG).show();
                } else {
                    DocumentReference reference = fb.collection("comments").document(uuid);
                    Map<String, Object> comment = new HashMap<>();
                    comment.put("text", getComment);
                    comment.put("identifier", uuid);
                    comment.put("relevance", rel);
                    comment.put("irrelevance", irel);
                    comment.put("commenter", name);
                    comment.put("time", currentTime);

                    reference.set(comment).addOnSuccessListener(aVoid -> Log.d("TAG",
                            "onSuccess: user profile was created for " + comment))
                            .addOnFailureListener(e -> Log.d("TAG", "onFailure: " + e));
                    dialog_comment.dismiss();

                    Chat chat = new Chat(name, getComment, currentTime, uuid, rel, irel);

                    mChat.add(0, chat);

                    messageAdapter = new MessageAdapter(getContext(), mChat);
                    recyclerView.setAdapter(messageAdapter);
                }
            });

            popUpBtn_cancel.setOnClickListener(view2 -> {
                    dialog_comment.dismiss();
            });
        });

        readComments();

        return view;
    }

    private void readComments() {
        fb.collection("comments").orderBy("time", Query.Direction.DESCENDING).get().addOnCompleteListener(task -> {
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
                rel = (List<String>)map.get("relevance");
                irel = (List<String>) map.get("irrelevance");
                Chat chat = new Chat(commenter, text, time, identifier, rel, irel);
                mChat.add(chat);
            }

            messageAdapter = new MessageAdapter(getContext(), mChat);
            recyclerView.setAdapter(messageAdapter);
        });
    }
}