package ativity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.organizze.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import Config.FirebaseConfig;
import Helper.Base64Custom;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    //Components
    private CircleImageView civProfile;
    private AppCompatTextView tvUsernameMain, tvUsername, tvEmail, tvPass;
    private LinearLayoutCompat llUsername, llEmail, llPass;
    private AppCompatButton btnChangeProfileImage, btnSaveChanges;
    private String userId;

    //Dialog Components
    private AppCompatEditText etName, etEmail, etCurrentPass, etNewPass, etConfirmPass;

    //Firebase
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;

    //Utils
    private User user;
    private Double totalRevenue, totalExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initAndConfigComponents();
        recoverUserData();
    }

    private void initAndConfigComponents(){

        Toolbar toolbar = findViewById(R.id.tbEditProfile);
        toolbar.setTitle("Editar Perfil");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reference = FirebaseConfig.getReference();
        auth = FirebaseConfig.getFirebaseAuth();
        firebaseUser = auth.getCurrentUser();

        civProfile = findViewById(R.id.civEP);
        tvUsernameMain = findViewById(R.id.tvUserNameMainEP);
        tvUsername = findViewById(R.id.tvUserNameEP);
        tvEmail = findViewById(R.id.tvEmailEP);
        tvPass = findViewById(R.id.tvPassEP);

        llUsername = findViewById(R.id.llUsernameEP);
        llUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               configAlertDialog("name");
            }
        });

        llEmail = findViewById(R.id.llEmailEP);
        llEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               configAlertDialog("email");
            }
        });

        llPass = findViewById(R.id.llPassEP);
        llPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                configAlertDialog("pass");
            }
        });

        btnChangeProfileImage = findViewById(R.id.btnChangeProfileImageEP);
        btnSaveChanges = findViewById(R.id.btnSaveChangesEP);
        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveChanges();
            }
        });
    }

    private void recoverUserData(){

        userId = Base64Custom.encode64Base(auth.getCurrentUser().getEmail());

        DatabaseReference userRef = reference.child("Users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                user = new User();
                user = snapshot.getValue(User.class);

                if(user.getName() != null){

                    tvUsernameMain.setText(user.getName());
                    tvUsername.setText(user.getName());
                }
                tvEmail.setText(user.getEmail());
                tvPass.setText("Senha aqui");
                totalRevenue = user.getTotalRevenue();
                totalExpense = user.getTotalExpense();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configAlertDialog(String type){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(type.equals("name")){

            LayoutInflater inflater = this.getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_name, null);
            builder.setView(view);

            builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    etName = view.findViewById(R.id.etNameDialogEP);
                    tvUsername.setText(etName.getText());

                }
            });

        }else if(type.equals("email")){

            LayoutInflater inflater = this.getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_email, null);
            builder.setView(view);


            builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    etEmail = view.findViewById(R.id.etEmailDialogEP);
                    tvEmail.setText(etEmail.getText());
                }
            });

        }else if(type.equals("pass")) {


            LayoutInflater inflater = this.getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_pass, null);
            builder.setView(view);

            builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    etCurrentPass = view.findViewById(R.id.etCurrentPassDialogEP);
                    etNewPass = view.findViewById(R.id.etNewPassDialogEP);
                    etConfirmPass = view.findViewById(R.id.etConfirmPassDialogEP);

                }
            });

        }

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void saveChanges(){

        if(etName != null){

            user = new User();
            user.setUserID(userId);
            user.setName(etName.getText().toString());
            user.updateName();

        }

        if(etEmail != null && etCurrentPass != null && etNewPass != null && etConfirmPass != null){

            firebaseUser.updateEmail(etEmail.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                    user = new User();
                    user.setUserID(userId);
                    user.setName(tvUsername.getText().toString());
                    user.setEmail(etEmail.getText().toString());
                    user.setTotalRevenue(totalRevenue);
                    user.setTotalExpense(totalExpense);
                    user.updateEmail();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    showMessage("Erro ao alterar E-mail, tente novamente!" + e.getMessage());
                }
            });

            String newPass = etNewPass.getText().toString();
            String confirmPass = etNewPass.getText().toString();

            if(newPass.equals(confirmPass)){

                firebaseUser.updatePassword(newPass).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        auth.signOut();
                        finish();
                        startActivity(new Intent(EditProfileActivity.this, LoginActivity.class));

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        showMessage("Erro ao alterar senha, tente novamente!");
                    }
                });
            }else{

                showMessage("As senhas não conferem, digite a nova senha, confirme!");
            }

        }else if(etEmail != null && etCurrentPass == null && etNewPass == null && etConfirmPass == null){

            firebaseUser.updateEmail(etEmail.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                    user = new User();
                    user.setUserID(userId);
                    user.setName(tvUsername.getText().toString());
                    user.setEmail(etEmail.getText().toString());
                    user.setTotalRevenue(totalRevenue);
                    user.setTotalExpense(totalExpense);
                    user.updateEmail();
                    auth.signOut();
                    finish();
                    startActivity(new Intent(EditProfileActivity.this, LoginActivity.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    showMessage("Erro ao alterar E-mail, tente novamente!" + e.getMessage());
                }
            });


        }else if(etEmail == null && etCurrentPass != null && etNewPass != null && etConfirmPass != null){

            String newPass = etNewPass.getText().toString();
            String confirmPass = etNewPass.getText().toString();

            if(newPass.equals(confirmPass)){

                firebaseUser.updatePassword(newPass).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        auth.signOut();
                        finish();
                        startActivity(new Intent(EditProfileActivity.this, LoginActivity.class));

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        showMessage("Erro ao alterar senha, tente novamente!");
                    }
                });
            }else{

                showMessage("As senhas não conferem, digite a nova senha e confirme!");
            }
        }
    }

    private void showMessage(String message){

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}