package Model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import Config.FirebaseConfig;

public class User {

    private String userID;
    private String name;
    private String email;
    private String password;
    private Double receitaTotal = 0.00;
    private Double despesaTotal = 0.00;

    public User() {
    }

    public void saveUser(){

        DatabaseReference reference = FirebaseConfig.getReference();
        reference.child("Users")
                 .child(this.userID)
                 .setValue(this);
    }

    @Exclude
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Double getReceitaTotal() {
        return receitaTotal;
    }

    public void setReceitaTotal(Double receitaTotal) {
        this.receitaTotal = receitaTotal;
    }

    public Double getDespesaTotal() {
        return despesaTotal;
    }

    public void setDespesaTotal(Double despesaTotal) {
        this.despesaTotal = despesaTotal;
    }
}
