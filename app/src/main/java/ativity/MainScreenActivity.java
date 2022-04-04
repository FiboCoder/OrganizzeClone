package ativity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.organizze.MainActivity;
import com.example.organizze.databinding.ActivityMainScreenBinding;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.organizze.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import Adapter.MovimentationAdapter;
import Config.FirebaseConfig;
import Helper.Base64Custom;
import Model.Movimentation;
import Model.User;

public class MainScreenActivity extends AppCompatActivity {

    private TextView welcome, balance;
    private MaterialCalendarView cvMain;
    private RecyclerView moves;
    private MovimentationAdapter adapter;
    private List<Movimentation> movimentationList = new ArrayList<>();
    private Movimentation movimentation;
    private DatabaseReference movesRef;
    private String mySelected;

    private FirebaseAuth auth = FirebaseConfig.getFirebaseAuth();
    private DatabaseReference reference = FirebaseConfig.getReference();
    private DatabaseReference userRef;
    private ValueEventListener valueEventListener;
    private ValueEventListener valueEventListenerMoves;

    private Double totalExpense;
    private Double totalRevenue;
    private Double valueResume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Organizze");
        setSupportActionBar(toolbar);

        welcome = findViewById(R.id.tvWelcome);
        balance = findViewById(R.id.tvBalance);
        cvMain = findViewById(R.id.cvMain);
        moves = findViewById(R.id.rvMoves);

        calendarViewConfigurations();

        adapter = new MovimentationAdapter(movimentationList, this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        moves.setLayoutManager(layoutManager);
        moves.setHasFixedSize(true);
        moves.setAdapter(adapter);
        swipe();

    }

    public void swipe(){

        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeFlags = ItemTouchHelper.START| ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                moveDelete(viewHolder);
            }
        };

        new ItemTouchHelper(itemTouch).attachToRecyclerView(moves);
    }

    public void moveDelete(RecyclerView.ViewHolder viewHolder){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("Exluir movimentação da Conta");
        alertDialog.setMessage("Você tem certeza que deseja realmente excluir essa movimentação da sua conta?");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int position = viewHolder.getAdapterPosition();
                movimentation = movimentationList.get(position);

                String userEmail = auth.getCurrentUser().getEmail();
                String userID = Base64Custom.encode64Base(userEmail);

                movesRef = reference.child("Movimentation")
                        .child(userID)
                        .child(mySelected);

                movesRef.child(movimentation.getKey()).removeValue();
                adapter.notifyItemRemoved(position);
                updateBalance();

            }
        });
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(MainScreenActivity.this, "Cancelado", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    public void updateBalance(){

        String userEmail = auth.getCurrentUser().getEmail();
        String userID = Base64Custom.encode64Base(userEmail);
        userRef = reference.child("Users").child(userID);

        if(movimentation.getType().equals("R")){

            totalRevenue = totalRevenue - movimentation.getValue();
            userRef.child("receitaTotal").setValue(totalRevenue);

        }

        if(movimentation.getType().equals("E")){

            totalExpense = totalExpense - movimentation.getValue();
            userRef.child("despesaTotal").setValue(totalExpense);

        }
    }

    public void recoverMovimentation(){

        String userEmail = auth.getCurrentUser().getEmail();
        String userID = Base64Custom.encode64Base(userEmail);

        movesRef = reference.child("Movimentation")
                .child(userID)
                .child(mySelected);


        valueEventListenerMoves = movesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                movimentationList.clear();

                for(DataSnapshot data: snapshot.getChildren()){

                    Movimentation movimentation = data.getValue(Movimentation.class);
                    movimentation.setKey(data.getKey());
                    movimentationList.add(movimentation);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void recoverResume(){

        String userEmail = auth.getCurrentUser().getEmail();
        String userID = Base64Custom.encode64Base(userEmail);
        userRef = reference.child("Users").child(userID);


        valueEventListener = userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                totalExpense = user.getDespesaTotal();
                totalRevenue = user.getReceitaTotal();
                valueResume = totalRevenue - totalExpense;

                DecimalFormat decimalFormat = new DecimalFormat("0.##");
                String formatedResult = decimalFormat.format(valueResume);

                welcome.setText("Olá, " + user.getName());
                balance.setText("R$" + formatedResult);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.logout:
                auth.signOut();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addDespesas(View view){

        startActivity(new Intent(this, DespesasActivity.class));
    }

    public void addReceitas(View view){

        startActivity(new Intent(this, ReceitasActivity.class));
    }

    public void calendarViewConfigurations(){

        CharSequence months[] = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        cvMain.setTitleMonths(months);

        CalendarDay currentDate = cvMain.getCurrentDate();
        String selectedMonth = String.format("%02d", currentDate.getMonth());
        mySelected = String.valueOf( selectedMonth + "" + currentDate.getYear());

        cvMain.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

                String selectedMonth = String.format("%02d", date.getMonth());
                mySelected = String.valueOf(selectedMonth + "" + date.getYear());

                movesRef.removeEventListener(valueEventListenerMoves);
                recoverMovimentation();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        recoverResume();
        recoverMovimentation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        userRef.removeEventListener(valueEventListener);
        movesRef.removeEventListener(valueEventListenerMoves);
    }
}