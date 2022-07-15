package com.example.crp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.crp.ui.gallery.GalleryFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Contratos extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth fba;
    private ImageView check;
    private boolean selected, select;
    private AlertDialog dialog_explain_contracts, dialog_share, dialog_read;
    private ImageView img_insta, img_twit, img_whats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contratos);
        Toolbar toolbar = findViewById(R.id.contract_bar);
        setSupportActionBar(toolbar);



        fba = FirebaseAuth.getInstance();

        selected = false;
        Button read = findViewById(R.id.read);
        Button show_share = findViewById(R.id.show_share);
        check = findViewById(R.id.select);
        ImageButton contract = findViewById(R.id.contract);

        read.setOnClickListener(view -> {
            if (selected) {
                AlertDialog.Builder dialogBuilder_read = new AlertDialog.Builder(this);
                final View preview = getLayoutInflater().inflate(R.layout.popup_preview, null);

                Button close = preview.findViewById(R.id.close_btn_preview);

                dialogBuilder_read.setView(preview);
                dialog_read = dialogBuilder_read.create();
                dialog_read.show();

                close.setOnClickListener(view1 -> {
                    dialog_read.dismiss();
                });

            } else {
                Toast.makeText(Contratos.this, "Por favor escolha um contrato para visualizar", Toast.LENGTH_LONG).show();
            }
        });

        contract.setOnClickListener(view -> {
            selected = !selected;
            if (selected) {
                check.setVisibility(View.VISIBLE);
            } else {
                check.setVisibility(View.INVISIBLE);
            }
        });

        show_share.setOnClickListener(view -> {
            if (selected) {
                AlertDialog.Builder dialogBuilder_share = new AlertDialog.Builder(this);
                final View sharePopUp = getLayoutInflater().inflate(R.layout.popup_share, null);

                ImageButton insta, twit, whats;
                Button cancel, share;
                select = false;

                twit = sharePopUp.findViewById(R.id.twitter);
                insta = sharePopUp.findViewById(R.id.insta);
                whats = sharePopUp.findViewById(R.id.whats);

                img_insta = sharePopUp.findViewById(R.id.insta_check);
                img_twit = sharePopUp.findViewById(R.id.twitter_check);
                img_whats = sharePopUp.findViewById(R.id.whats_check);

                cancel = sharePopUp.findViewById(R.id.cancel);
                share = sharePopUp.findViewById(R.id.share);

                dialogBuilder_share.setView(sharePopUp);
                dialog_share = dialogBuilder_share.create();
                dialog_share.show();

                twit.setOnClickListener(this);
                insta.setOnClickListener(this);
                whats.setOnClickListener(this);
                cancel.setOnClickListener(this);
                share.setOnClickListener(this);
            } else {
                Toast.makeText(this, "Por favor escolha um contrato para partilhar", Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.contracts_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.About_contracts) {
            AlertDialog.Builder dialogBuilder_explain_contracts = new AlertDialog.Builder(this);
            final View explainPopup = getLayoutInflater().inflate(R.layout.popup_explain_contracts, null);

            Button popUpBtn_explain = explainPopup.findViewById(R.id.close_btn_contracts);

            dialogBuilder_explain_contracts.setView(explainPopup);
            dialog_explain_contracts = dialogBuilder_explain_contracts.create();
            dialog_explain_contracts.show();

            popUpBtn_explain.setOnClickListener(view -> dialog_explain_contracts.dismiss());
        } else if (id == R.id.btn_logout_contracts) {
            fba.signOut();

            Intent intent = new Intent(Contratos.this, LogIn.class);
            startActivity(intent);
        } else if (id == R.id.home) {
            Intent intent = new Intent(Contratos.this, MainScreen.class);
            startActivity(intent);
        }
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.insta:
                if (!select) {
                    img_insta.setVisibility(View.VISIBLE);
                } else {
                    if (img_twit.getVisibility() == View.VISIBLE) {
                        img_twit.setVisibility(View.INVISIBLE);
                        img_insta.setVisibility(View.VISIBLE);
                    } else if (img_whats.getVisibility() == View.VISIBLE) {
                        img_whats.setVisibility(View.INVISIBLE);
                        img_insta.setVisibility(View.VISIBLE);
                    }
                }
                break;

            case R.id.twitter:
                if (!select) {
                    img_twit.setVisibility(View.VISIBLE);
                    select = !select;
                } else {
                    if (img_insta.getVisibility() == View.VISIBLE) {
                        img_insta.setVisibility(View.INVISIBLE);
                        img_twit.setVisibility(View.VISIBLE);
                    } else if (img_whats.getVisibility() == View.VISIBLE) {
                        img_whats.setVisibility(View.INVISIBLE);
                        img_twit.setVisibility(View.VISIBLE);
                    }
                }
                break;

            case R.id.whats:
                if (!select) {
                    img_whats.setVisibility(View.VISIBLE);
                    select = !select;
                } else {
                    if (img_twit.getVisibility() == View.VISIBLE) {
                        img_twit.setVisibility(View.INVISIBLE);
                        img_whats.setVisibility(View.VISIBLE);
                    } else if (img_insta.getVisibility() == View.VISIBLE) {
                        img_insta.setVisibility(View.INVISIBLE);
                        img_whats.setVisibility(View.VISIBLE);
                    }
                }
                break;

            case R.id.share:
                if (!select) {
                    Toast.makeText(this, "Por favor escolha uma plataforma para partilhar o contrato", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Obrigado por participar do teste desta aplicação, esta função será ativada na versão final", Toast.LENGTH_LONG).show();
                }
                dialog_share.dismiss();
                break;

            case R.id.cancel:
                dialog_share.dismiss();
        }
    }
}