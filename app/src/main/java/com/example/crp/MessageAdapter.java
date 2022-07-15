package com.example.crp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private Context mContext;
    private List<Chat> mChat;
    private FirebaseFirestore fb;
    private FirebaseUser fuser;

    public MessageAdapter(@NonNull Context mContext, List<Chat> mChat) {
        this.mContext = mContext;
        this.mChat = mChat;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment, parent, false);
        return new MessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Chat chat = mChat.get(position);
        holder.comment.setText(chat.getText());
        holder.commenter.setText(chat.getSender());
        holder.relevance.setText(String.valueOf(chat.getRele()));
    }

    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView comment, commenter, relevance;
        public ImageView  rel_btn, irel_btn;
        public String name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            comment = itemView.findViewById(R.id.comment);
            commenter = itemView.findViewById(R.id.comment_user);
            relevance = itemView.findViewById(R.id.relevance);
            rel_btn = itemView.findViewById(R.id.rel);
            irel_btn = itemView.findViewById(R.id.irel);

            fuser = FirebaseAuth.getInstance().getCurrentUser();
            String uid = fuser.getUid();
            fb = FirebaseFirestore.getInstance();
            fb.collection("users").document(uid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        HashMap data = (HashMap) document.getData();
                        name = data.get("username").toString();
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed: " + task.getException());
                }
            });

            rel_btn.setOnClickListener(view -> {
                String id = mChat.get(getPosition()).getIdentifier();
                List<String> rel = mChat.get(getPosition()).getRel();
                List<String> irel = mChat.get(getPosition()).getIrel();
                Boolean not = true;
                Log.d("TAG",
                        "onSuccess: user profile was created for " + name);
                Log.d("TAG",
                        "on " + rel.isEmpty());
                Log.d("TAG",
                        "Success: " + irel.isEmpty());
                if (!rel.isEmpty() || !irel.isEmpty()) {
                    if (irel.isEmpty()) {
                        Log.d("TAG",
                                "onSuccess: " + not);
                        for (int i = 0; i < rel.size(); i++) {
                            if (name.equals(rel.get(i).toString())) {
                                not = false;
                                Log.d("TAG",
                                        "onSuccess: user " + not);
                                break;
                            }
                        }
                    } else {
                        for (int i = 0; i < irel.size(); i++) {
                            if (name.equals(irel.get(i))) {
                                irel.remove(i);
                                HashMap<String, Object> new_rel = new HashMap<>();
                                new_rel.put("irrelevance", irel);
                                fb.collection("comments").document(id).update(new_rel).addOnSuccessListener(aVoid -> Log.d("TAG",
                                        "onSuccess: for " + irel))
                                        .addOnFailureListener(e -> Log.d("TAG", "onFailure: " + e));

                                mChat.get(getPosition()).setIrel(irel);
                                break;
                            }
                        }
                    }
                }
                if (not) {
                    rel.add(name);
                    mChat.get(getPosition()).setRel(rel);
                    Log.d("TAG",
                            "onSuccess: profile " + rel);
                    HashMap<String, Object> new_rel = new HashMap<>();
                    new_rel.put("relevance", rel);

                    Log.d("TAG",
                            "onSuccess: created" + new_rel);
                    fb.collection("comments").document(id).update(new_rel).addOnSuccessListener(aVoid -> Log.d("TAG",
                            "onSuccess: was " + rel))
                            .addOnFailureListener(e -> Log.d("TAG", "onFailure: " + e));
                }
                Log.d("TAG",
                        "onSuccess: user profile was created for " + mChat.get(getPosition()).getRele());
                relevance.setText(String.valueOf(mChat.get(getPosition()).getRele()));
            });

            irel_btn.setOnClickListener(view -> {
                String id = mChat.get(getPosition()).getIdentifier();
                List<String> rel = mChat.get(getPosition()).getRel();
                List<String> irel = mChat.get(getPosition()).getIrel();
                Boolean not = true;
                if (!irel.isEmpty() || !rel.isEmpty()) {
                    if (rel.isEmpty()) {
                        for (int i = 0; i < irel.size(); i++) {
                            if (name.equals(irel.get(i))) {
                                not = false;
                                break;
                            }
                        }
                    } else {
                        for (int i = 0; i < rel.size(); i++) {
                            if (name.equals(rel.get(i).toString())) {
                                rel.remove(i);
                                HashMap<String, Object> new_rel = new HashMap<>();
                                new_rel.put("relevance", rel);
                                fb.collection("comments").document(id).update(new_rel).addOnSuccessListener(aVoid -> Log.d("TAG",
                                        "onSuccess: user profile was created for " + rel))
                                        .addOnFailureListener(e -> Log.d("TAG", "onFailure: " + e));
                                mChat.get(getPosition()).setRel(rel);
                                break;
                            }
                        }
                    }
                }

                if (not) {
                    irel.add(name);
                    Log.d("TAG",
                            "onSuccess: user profile was created for " + irel);
                    HashMap<String, Object> new_irel = new HashMap<>();
                    new_irel.put("irrelevance", irel);
                    Log.d("TAG",
                            "onSuccess: user profile was created for " + new_irel);
                    mChat.get(getPosition()).setIrel(irel);
                    fb.collection("comments").document(id).update(new_irel).addOnSuccessListener(aVoid -> Log.d("TAG",
                            "onSuccess: user profile was created for " + irel))
                            .addOnFailureListener(e -> Log.d("TAG", "onFailure: " + e));
                }
                Log.d("TAG",
                        "onSuccess: user profile was created for " + mChat.get(getPosition()).getRele());
                relevance.setText(String.valueOf(mChat.get(getPosition()).getRele()));
            });
        }
    }
}
