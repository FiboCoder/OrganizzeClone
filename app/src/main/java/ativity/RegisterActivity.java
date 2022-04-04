package ativity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organizze.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import Config.FirebaseConfig;
import Helper.Base64Custom;
import Model.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText etNameR, etEmailR, etPasswordR;
    private Button btRegisterR;

    private FirebaseAuth auth;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        FirebaseApp.initializeApp(RegisterActivity.this);

        etNameR = this.findViewById(R.id.etNameR);
        etEmailR = this.findViewById(R.id.etEmailR);
        etPasswordR = this.findViewById(R.id.etPasswordR);
        btRegisterR = this.findViewById(R.id.btRegisterR);

        btRegisterR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = etNameR.getText().toString();
                String email = etEmailR.getText().toString();
                String password = etPasswordR.getText().toString();

                if(!name.isEmpty()){

                    if(!email.isEmpty()){

                        if(!password.isEmpty()){
                            user = new User();
                            user.setName(name);
                            user.setEmail(email);
                            user.setPassword(password);
                            registerUser();

                        }else{

                            Toast.makeText(RegisterActivity.this, "Preencha a Senha", Toast.LENGTH_SHORT).show();
                        }

                    }else{

                        Toast.makeText(RegisterActivity.this, "Preencha o E-mail", Toast.LENGTH_SHORT).show();
                    }

                }else{

                    Toast.makeText(RegisterActivity.this, "Preencha o Nome", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void registerUser(){

        auth = FirebaseConfig.getFirebaseAuth();
        auth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    String userID = Base64Custom.encode64Base(user.getEmail());
                    user.setUserID(userID);
                    user.saveUser();
                    saveUserData();
                    finish();

                }else{
                    String exception = "";
                    try{
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        exception = "Digite uma senha mais forte!";
                    }catch (FirebaseAuthInvalidCredentialsException e) {
                        exception = "Digite um e-mail válido!";
                    }catch (FirebaseAuthUserCollisionException e) {
                        exception = "Essa conta já foi cadastrada!";
                    }catch (Exception e) {
                        exception = "Erro ao cadastrar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(RegisterActivity.this, exception, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void saveUserData(){


    }
}