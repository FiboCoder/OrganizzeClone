package ativity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.organizze.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import Config.FirebaseConfig;
import Helper.Base64Custom;
import Helper.Permissions;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    //Components
    private CircleImageView civProfile;
    private AppCompatTextView tvUsernameMain, tvUsername, tvEmail, tvPass;
    private AppCompatButton btnChangeProfileImage;
    private AppCompatImageButton btnChangeName, btnChangeEmail, btnChangePass;
    private String userId;

    //Dialog Components
    private AppCompatEditText etName, etEmail, etCurrentPass, etNewPass, etConfirmPass;

    //Firebase
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;

    //Utils
    private String[] permissions = new String[]{

            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private static final int GALLERY_SELECTION = 100;
    private User user;
    private Double totalRevenue, totalExpense;
    private Uri url;
    private androidx.appcompat.app.AlertDialog dialogLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initAndConfigComponents();
        recoverUserData();
    }

    private void initAndConfigComponents() {

        Toolbar toolbar = findViewById(R.id.tbEditProfile);
        toolbar.setTitle("Editar Perfil");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Permissions.validatePermissions(permissions, this, 1);

        reference = FirebaseConfig.getReference();
        auth = FirebaseConfig.getFirebaseAuth();
        storageReference = FirebaseConfig.getStorage();
        firebaseUser = auth.getCurrentUser();

        civProfile = findViewById(R.id.civEP);

        tvUsernameMain = findViewById(R.id.tvUserNameMainEP);
        tvUsername = findViewById(R.id.tvUserNameEP);
        tvEmail = findViewById(R.id.tvEmailEP);
        tvPass = findViewById(R.id.tvPassEP);

        btnChangeProfileImage = findViewById(R.id.btnChangeProfileImageEP);
        btnChangeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if (intent.resolveActivity(getPackageManager()) != null) {

                    startActivityForResult(intent, GALLERY_SELECTION);
                }
            }
        });

        btnChangeName = findViewById(R.id.btnChangeNameEP);
        btnChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                configAlertDialog("name");
            }
        });

        btnChangeEmail = findViewById(R.id.btnChangeEmailEP);
        btnChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                configAlertDialog("email");
            }
        });

        btnChangePass = findViewById(R.id.btnChangePassEP);
        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                configAlertDialog("pass");
            }
        });

    }

    private void recoverUserData(){

        configLoadingDialog("Carregando dados");

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

                Picasso.get().load(user.getProfileImageUrl()).into(civProfile);

                dialogLoading.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){

            Bitmap image = null;

            try {

                switch (requestCode){

                    case GALLERY_SELECTION:
                        Uri localImage = data.getData();
                        image = MediaStore.Images.Media.getBitmap(getContentResolver(), localImage);
                        break;
                }

            }catch (Exception e){

                e.printStackTrace();
            }

            if(image != null){

                civProfile.setImageBitmap(image);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                byte[] imageData = baos.toByteArray();

                configLoadingDialog("Alterando Imagem de Perfil");

                StorageReference imageRef = storageReference
                        .child("Profile")
                        .child(userId + ".jpeg");

                UploadTask uploadTask = imageRef.putBytes(imageData);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {

                                url = task.getResult();
                                updateProfileImage(url);
                                dialogLoading.dismiss();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        }
    }

    private void configLoadingDialog(String title){

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_loading, null);
        AppCompatTextView tvMessage = view.findViewById(R.id.tvDialogLoading);
        tvMessage.setText(title);
        builder.setView(view);

        dialogLoading = builder.create();
        dialogLoading.show();
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

                    configLoadingDialog("Alterando Nome");

                    etName = view.findViewById(R.id.etNameDialogEP);
                    updateName();
                    tvUsername.setText(etName.getText());
                    dialogLoading.dismiss();
                    dialog.dismiss();

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

                    configLoadingDialog("Alterando Email");

                    etEmail = view.findViewById(R.id.etEmailDialogEP);
                    updateEmail();
                    tvEmail.setText(etEmail.getText());
                    dialogLoading.dismiss();
                    dialog.dismiss();
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

                    configLoadingDialog("Alterando Senha");

                    etCurrentPass = view.findViewById(R.id.etCurrentPassDialogEP);
                    etNewPass = view.findViewById(R.id.etNewPassDialogEP);
                    etConfirmPass = view.findViewById(R.id.etConfirmPassDialogEP);

                    if(etNewPass.getText().equals(etConfirmPass.getText())){

                        updatePass();
                        dialogLoading.dismiss();
                        dialog.dismiss();

                    }else{

                        showMessage("As senhas não correspondem, digite a nova senha e confirme a mesma!");
                        dialogLoading.dismiss();
                        dialog.dismiss();
                    }
                }
            });

        }

        AlertDialog dialog = builder.create();
        dialog.show();
    }



    private void updateProfileImage(Uri url){

        boolean _return = Helper.FirebaseUser.updateProfileImage(url);

        if(_return){

            user = new User();
            user.setUserID(userId);
            user.setProfileImageUrl(String.valueOf(url));
            user.updateProfileImage();
        }
    }

    private void updateName(){

        user = new User();
        user.setUserID(userId);
        user.setName(etName.getText().toString());
        user.updateName();
        showMessage("Nome alterado com sucesso!");
    }

    private void updateEmail(){

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
                showMessage("Email alterado com sucesso!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                showMessage("Erro ao alterar E-mail, tente novamente!" + e.getMessage());
            }
        });
    }


    private void updatePass(){

        String newPass = etNewPass.getText().toString();
        String confirmPass = etNewPass.getText().toString();

        if(newPass.equals(confirmPass)){

            firebaseUser.updatePassword(newPass).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                    showMessage("Senha alterada com sucesso!");
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
    }

    private void showMessage(String message){

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}