package ativity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organizze.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import Config.FirebaseConfig;
import Helper.Base64Custom;
import Helper.DateUtil;
import Model.Movimentation;
import Model.User;

public class ReceitasActivity extends AppCompatActivity {

    private TextInputEditText date, category, description;
    private EditText value;
    private Double totalRevenue;

    private Movimentation movimentation;

    private DatabaseReference reference = FirebaseConfig.getReference();
    private FirebaseAuth auth = FirebaseConfig.getFirebaseAuth();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receitas);

        date = findViewById(R.id.etDateR);
        category = findViewById(R.id.etCategoryR);
        description = findViewById(R.id.etDescriptionR);
        value = findViewById(R.id.etValueR);

        date.setText(DateUtil.currentDate());
        recoverTotalRevenue();
    }

    public void saveRevenue(View view){

        if(validateRevenueFields()){

            movimentation = new Movimentation();
            String selectedDate = date.getText().toString();
            Double recoveredValue = Double.parseDouble(value.getText().toString());
            movimentation.setValue(recoveredValue);
            movimentation.setCategory(category.getText().toString());
            movimentation.setDescription(description.getText().toString());
            movimentation.setDate(selectedDate);
            movimentation.setType("R");

            Double updatedExpense = totalRevenue + recoveredValue;
            updateRevenue(updatedExpense);

            movimentation.save(selectedDate);
            finish();
        }
    }

    public Boolean validateRevenueFields(){

        String vrfValue = value.getText().toString();
        String vrfDate = date.getText().toString();
        String vrfCategory = category.getText().toString();
        String vrfDescription = description.getText().toString();

        if(!vrfValue.isEmpty()){

            if(!vrfDate.isEmpty()){

                if(!vrfCategory.isEmpty()){

                    if(!vrfDescription.isEmpty()){

                        return true;
                    }else{

                        Toast.makeText(ReceitasActivity.this, "Descrição não foi preenchida!", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }else{

                    Toast.makeText(ReceitasActivity.this, "Categoria não foi preenchida!", Toast.LENGTH_SHORT).show();
                    return false;
                }

            }else{

                Toast.makeText(ReceitasActivity.this, "Data não foi preenchida!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{

            Toast.makeText(ReceitasActivity.this, "Valor não foi preenchida!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void recoverTotalRevenue(){

        String userEmail = auth.getCurrentUser().getEmail();
        String userID = Base64Custom.encode64Base(userEmail);
        DatabaseReference userRef = reference.child("Users").child(userID);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);
                totalRevenue = user.getTotalRevenue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updateRevenue(Double revenue){

        String userEmail = auth.getCurrentUser().getEmail();
        String userID = Base64Custom.encode64Base(userEmail);
        DatabaseReference userRef = reference.child("Users").child(userID);

        userRef.child("receitaTotal").setValue(revenue);
    }
}