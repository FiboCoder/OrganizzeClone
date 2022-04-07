package Model;

import android.net.Uri;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

import Config.FirebaseConfig;
import Helper.Base64Custom;

public class User {

    private String userID;
    private String profileImageUrl = "";
    private String name;
    private String email;
    private String password;
    private Double totalRevenue = 0.00;
    private Double totalExpense = 0.00;

    private DatabaseReference reference = FirebaseConfig.getReference();

    public User() {
    }

    public void saveUser(){


        reference.child("Users")
                 .child(this.userID)
                 .setValue(this);
    }

    public void updateProfileImage(){

        DatabaseReference imageRef = reference.child("Users").child(getUserID());
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("profileImageUrl", getProfileImageUrl());

        imageRef.updateChildren(updateMap);

    }

    public void updateName(){

        DatabaseReference nameRef = reference.child("Users").child(getUserID());
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("name", getName());

        nameRef.updateChildren(updateMap);
    }

    public void updateEmail(){

        DatabaseReference userRef = reference.child("Users").child(getUserID());
        userRef.removeValue();

        String newId = Base64Custom.encode64Base(getEmail());

        DatabaseReference userRefUpdate = reference.child("Users").child(newId);
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("name", getName());
        updateMap.put("email", getEmail());
        updateMap.put("totalRevenue", getTotalRevenue());
        updateMap.put("totalExpense", getTotalExpense());

        userRefUpdate.setValue(updateMap);

    }

    @Exclude
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
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

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Double getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(Double totalExpense) {
        this.totalExpense = totalExpense;
    }
}
