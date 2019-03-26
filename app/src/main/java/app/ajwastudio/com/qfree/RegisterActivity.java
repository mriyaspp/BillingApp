package app.ajwastudio.com.qfree;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {
    private static final int GALLERY_REQUEST = 1;
    private ImageView mImageView;
    private EditText name;
    private Button mButton;
    private DatabaseReference mDatabase;
//    private StorageReference mStorage;
//    private int SELECT_PICTURE = 1;
    private FirebaseAuth mAuth;
    private Uri mImageUri = null;
    private String phoneno;
    private TextView confText;
    private ProgressBar confBar;
    String msgToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();

    }
    private void init() {
        Intent intent=getIntent();
        phoneno = intent.getStringExtra("phonevery");
        name = (EditText) findViewById(R.id.register_name);
        mButton = (Button) findViewById(R.id.register_btn);
        confText=(TextView)findViewById(R.id.conf_text);
        confBar=(ProgressBar)findViewById(R.id.conf_bar);
        mAuth = FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("user").child(mAuth.getUid());

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

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(name.getText().toString())) {
                    confText.setVisibility(View.VISIBLE);
                    confBar.setVisibility(View.VISIBLE);
                    uploadImage();
                }
                else {
                    confText.setText("Enter Your Name");
                    confText.setTextColor(Color.RED);
                    confText.setVisibility(View.VISIBLE);
                    Toast.makeText(RegisterActivity.this, "Fill the Details", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void uploadImage() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mDatabase.child("name").setValue(name.getText().toString());
                mDatabase.child("phoninfo").setValue(phoneno);
                Toast.makeText(getApplicationContext(),"Successfully Uploaded...",Toast.LENGTH_LONG).show();
                Intent i=new Intent(RegisterActivity.this,MainActivity.class);
                confBar.setVisibility(View.GONE);
                confText.setText("Registered");
                confText.setTextColor(Color.GREEN);
                startActivity(i);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==GALLERY_REQUEST&&resultCode==RESULT_OK){
            mImageUri=data.getData();
            mImageView.setImageURI(mImageUri);
        }
    }
}
