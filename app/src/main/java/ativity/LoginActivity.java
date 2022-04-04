package ativity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organizze.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import Config.FirebaseConfig;
import Model.User;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmailL, etPasswordL;
    private Button btLogin;

    private FirebaseAuth auth;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmailL = findViewById(R.id.etEmailL);
        etPasswordL = findViewById(R.id.etPasswordL);
        btLogin = findViewById(R.id.btLogin);

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = etEmailL.getText().toString();
                String password = etPasswordL.getText().toString();

                if(!email.isEmpty()){

                    if(!password.isEmpty()){
                        user = new User();
                        user.setEmail(email);
                        user.setPassword(password);
                        loginUser();

                    }else{

                        Toast.makeText(LoginActivity.this, "Preencha a Senha", Toast.LENGTH_SHORT).show();
                    }

                }else{

                    Toast.makeText(LoginActivity.this, "Preencha o E-mail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void loginUser(){

        auth = FirebaseConfig.getFirebaseAuth();
        auth.signInWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    openMainScreen();
                }else{

                    Toast.makeText(LoginActivity.this, "Erro ao fazer login.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void openMainScreen(){

        startActivity(new Intent(this, MainScreenActivity.class));
        finish();
    }
}