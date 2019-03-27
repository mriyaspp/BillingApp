package app.ajwastudio.com.qfree;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanQR extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    private ZXingScannerView mScannerView;
    private String TAG = "QR SCAN";
    private FirebaseDatabase database;
    private DatabaseReference Cart;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        mScannerView.setAutoFocus(true);







    }
    @Override
    protected void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }
    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }
    @Override
    public void handleResult(Result rawResult) {
//        Toast.makeText(this, rawResult.getText(), Toast.LENGTH_SHORT).show();

        final String key=rawResult.getText();


        database = FirebaseDatabase.getInstance();
        mAuth= FirebaseAuth.getInstance();
        final DatabaseReference mBillRef = database.getReference().child("Product");
        Cart= FirebaseDatabase.getInstance().getReference().child("Cart").child(mAuth.getUid());
        mBillRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(key + "")) {

                    final Data order = dataSnapshot.child(key+"").getValue(Data.class);
                    DatabaseReference db=Cart.push();
                    db.child("name").setValue(order.getName());
                    db.child("amount").setValue(order.getAmount());
                    db.child("tax").setValue(order.getTax());


                    //Toast.makeText(MainActivity.this, ""+orderList.get(0), Toast.LENGTH_SHORT).show();



//                        String json = gson.toJson(order[0]);
//                        prefsEditor.putString("MyObject", json);
//                        prefsEditor.commit();

                } else {
                    Toast.makeText(ScanQR.this, "Product is not availeble in the databse", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {


        Intent intent = new Intent(ScanQR.this, MainActivity.class);
        intent.putExtra("key", key);
        startActivity(intent);

            }
        }, 5000);

    }
}
