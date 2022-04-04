package Config;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseConfig {

    private static FirebaseAuth auth;
    private static DatabaseReference reference;

    public static DatabaseReference getReference(){

        if(reference == null){
            reference = FirebaseDatabase.getInstance().getReference();
        }
        return reference;
    }
    public static FirebaseAuth getFirebaseAuth(){

        if(auth == null){
            auth = FirebaseAuth.getInstance();
        }
        return auth;

    }
}
