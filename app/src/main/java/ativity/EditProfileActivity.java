package ativity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.example.organizze.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    //Components
    private CircleImageView civProfile;
    private AppCompatTextView tvUsernameMain, tvUsername, tvEmail, tvPass;
    private LinearLayoutCompat llUsername, llEmail, llPass;
    private AppCompatButton btnChangeProfileImage, btnSaveChanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initAndConfigComponents();
    }

    private void initAndConfigComponents(){

        Toolbar toolbar = findViewById(R.id.tbEditProfile);
        toolbar.setTitle("Editar Perfil");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
    }

    private void configAlertDialog(String type){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(type.equals("name")){

            builder.setView(R.layout.dialog_name);

            builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                }
            });

        }else if(type.equals("email")){

            builder.setView(R.layout.dialog_email);

            builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                }
            });

        }else if(type.equals("pass")) {

            builder.setView(R.layout.dialog_pass);

            builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                }
            });

        }

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}