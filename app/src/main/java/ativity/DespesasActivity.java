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

public class DespesasActivity extends AppCompatActivity {

    private TextInputEditText date, category, description;
    private EditText  value;
    private Double totalExpense;

    private Movimentation movimentation;

    private DatabaseReference reference = FirebaseConfig.getReference();
    private FirebaseAuth auth = FirebaseConfig.getFirebaseAuth();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesas);

        date = findViewById(R.id.etDate);
        category = findViewById(R.id.etCategory);
        description = findViewById(R.id.etCategory);
        value = findViewById(R.id.etValue);

        date.setText(DateUtil.currentDate());
        recoverTotalExpense();
    }

    public void saveExpenditure(View view){

        if(validateExpenseFields()){

            movimentation = new Movimentation();
            String selectedDate = date.getText().toString();
            Double recoveredValue = Double.parseDouble(value.getText().toString());
            movimentation.setValue(recoveredValue);
            movimentation.setCategory(category.getText().toString());
            movimentation.setDescription(description.getText().toString());
            movimentation.setDate(selectedDate);
            movimentation.setType("E");

            Double updatedExpense = totalExpense + recoveredValue;
            updateExpense(updatedExpense);

            movimentation.save(selectedDate);
            finish();
        }
    }

    public Boolean validateExpenseFields(){

        String vefValue = value.getText().toString();
        String vefDate = date.getText().toString();
        String vefCategory = category.getText().toString();
        String vefDescription = description.getText().toString();

        if(!vefValue.isEmpty()){

            if(!vefDate.isEmpty()){

                if(!vefCategory.isEmpty()){

                    if(!vefDescription.isEmpty()){

                        return true;
                    }else{

                        Toast.makeText(DespesasActivity.this, "Descrição não foi preenchida!", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }else{

                    Toast.makeText(DespesasActivity.this, "Categoria não foi preenchida!", Toast.LENGTH_SHORT).show();
                    return false;
                }

            }else{

                Toast.makeText(DespesasActivity.this, "Data não foi preenchida!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{

            Toast.makeText(DespesasActivity.this, "Valor não foi preenchido!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void recoverTotalExpense(){

        String userEmail = auth.getCurrentUser().getEmail();
        String userID = Base64Custom.encode64Base(userEmail);
        DatabaseReference userRef = reference.child("Users").child(userID);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);
                totalExpense = user.getTotalExpense();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updateExpense(Double expense){

        String userEmail = auth.getCurrentUser().getEmail();
        String userID = Base64Custom.encode64Base(userEmail);
        DatabaseReference userRef = reference.child("Users").child(userID);

        userRef.child("despesaTotal").setValue(expense);
    }
}