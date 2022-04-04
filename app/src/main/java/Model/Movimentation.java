package Model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import Config.FirebaseConfig;
import Helper.Base64Custom;
import Helper.DateUtil;

public class Movimentation {

    private String date;
    private String category;
    private String description;
    private String type;
    private double value;
    private String key;

    public Movimentation() {
    }

    public void save(String date){

        FirebaseAuth auth = FirebaseConfig.getFirebaseAuth();
        String userID = Base64Custom.encode64Base(auth.getCurrentUser().getEmail());
        String monthYear = DateUtil.myDate(date);
        DatabaseReference reference = FirebaseConfig.getReference();
        reference.child("Movimentation")
                .child(userID)
                .child(monthYear)
                .push()
                .setValue(this);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }


}
