package Helper;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

import Config.FirebaseConfig;

public class FirebaseUser {

    public static boolean updateProfileImage(Uri url){

        FirebaseAuth auth = FirebaseConfig.getFirebaseAuth();

        try{

            com.google.firebase.auth.FirebaseUser user = auth.getCurrentUser();
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(url)
                    .build();
            user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(!task.isSuccessful()){

                        Log.d("test", "tes");
                    }
                }
            });

            return true;
        }catch (Exception e){

            e.printStackTrace();
        }

        return false;
    }
}
