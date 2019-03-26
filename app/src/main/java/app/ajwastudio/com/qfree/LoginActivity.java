package app.ajwastudio.com.qfree;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private EditText loginPhoneNoText, loginOtpText;
    private TextView optText;
    private Button login,otp;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verficationCallback;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private FirebaseAuth mAuth;
    private String phoneVerificationId = null;
    Activity activity =LoginActivity.this;
    String wantPermission = android.Manifest.permission.READ_PHONE_STATE;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private String phn = null;
    private DatabaseReference mDatabase;
    private String masg="+91";
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(this);


        init();
//        if (!checkPermission(wantPermission)) {
//            requestPermission(wantPermission);
//        } else {
//
//            loginPhoneNoText.setText(getPhone());
//        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pd.setTitle("Loading");
                if(!TextUtils.isEmpty(loginPhoneNoText.getText())) {
                    loginOtpText.setVisibility(View.VISIBLE);
                    // loginPrgbar.setVisibility(View.VISIBLE);
                    masg=loginPhoneNoText.getText().toString();
                    //pd.show();
                    optText.setText("Please Wait...Otp has been send to "+masg);
                    optText.setVisibility(View.VISIBLE);
                    login.setText("Please Wait For Code");

                    sendVerificationCode(v);
                    //login.setText("Submit OTP");
                }
                else {
                    Toast.makeText(activity, "Enter a valid phone number", Toast.LENGTH_SHORT).show();
                }
            }
        });
        otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerifySignInCode();
            }
        });



    }
    private void VerifySignInCode() {
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(phoneVerificationId,loginOtpText.getText().toString());
        signInWithPhoneAuthCredential(credential);
    }

    private void init() {
        // pd = new ProgressDialog(LoginActivity.this);
        loginPhoneNoText = (EditText) findViewById(R.id.login_phone);
        loginOtpText = (EditText) findViewById(R.id.login_otp);
        login = (Button) findViewById(R.id.login);
        otp = (Button) findViewById(R.id.otp);
        optText=(TextView)findViewById(R.id.otpText);
        mAuth = FirebaseAuth.getInstance();
        loginPhoneNoText.setText("+91");
        //loginPrgbar = (ProgressBar) findViewById(R.id.login_prgbar);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("user");
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.BLACK);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            //setStatusBarTranslucent(true);
        }



    }
//    private String getPhone() {
//        TelephonyManager phoneMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        if (ActivityCompat.checkSelfPermission(activity, wantPermission) != PackageManager.PERMISSION_GRANTED) {
//            return "";
//        }
//        return phoneMgr.getLine1Number();
//    }
//    private void requestPermission(String permission){
//        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)){
//            Toast.makeText(activity, "Phone state permission allows us to get phone number. Please allow it for additional functionality.", Toast.LENGTH_LONG).show();
//        }
//        ActivityCompat.requestPermissions(activity, new String[]{permission},PERMISSION_REQUEST_CODE);
//    }

    //    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case PERMISSION_REQUEST_CODE:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    loginPhoneNoText.setText(getPhone());
//                } else {
//                    Toast.makeText(activity,"Permission Denied. We can't get phone number.", Toast.LENGTH_LONG).show();
//                }
//                break;
//        }
//
//    }
    public void sendVerificationCode(View view){
        String phoneNumber=loginPhoneNoText.getText().toString();
        setUpVerificationCallbacks();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                verficationCallback
        );
    }

    private void setUpVerificationCallbacks() {
        verficationCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
//            signInWithPhoneAuthCredential(phoneAuthCredential);
                try {
                    optText.setText("Otp has been set to " + masg);
                    optText.setVisibility(View.VISIBLE);
                    login.setVisibility(View.GONE);
                    otp.setVisibility(View.VISIBLE);
                    //loginOtpText.setText(phoneAuthCredential.getSmsCode());
                    //   verifyPhoneNumberWithCode(phoneVerificationId, phoneAuthCredential.getSmsCode());
                }catch (Exception e){

                    login.setVisibility(View.VISIBLE);
                    login.setText("Resent Otp");
                    Toast.makeText(activity, ""+e, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onVerificationFailed(FirebaseException e) {
                optText.setText("Otp sent error ");
                //loginPrgbar.setVisibility(View.GONE);
                optText.setTextColor(Color.RED);
                loginOtpText.setVisibility(View.GONE);
                otp.setVisibility(View.GONE);
                optText.setVisibility(View.VISIBLE);
                login.setText("Resent Otp");
                login.setVisibility(View.VISIBLE);
                Toast.makeText(activity, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String phoneVerificationid, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                phoneVerificationId = phoneVerificationid;
                resendToken = forceResendingToken;
            }
        };
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    mDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(mAuth.getUid()+"")){
                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                                intent.putExtra("phonevery",masg);
                                //  Toast.makeText(activity, ""+masg, Toast.LENGTH_SHORT).show();
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    Toast.makeText(LoginActivity.this, "user signed in success", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
}
