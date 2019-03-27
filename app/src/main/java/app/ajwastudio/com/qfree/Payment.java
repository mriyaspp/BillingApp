package app.ajwastudio.com.qfree;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Payment extends AppCompatActivity {
    private Button payment;
    private DatabaseReference headerDatabase;
    private DatabaseReference Cart;
    private DatabaseReference purchase;
    private List<Data> orderList;
    private FirebaseAuth mAuth;
    Double total=0.0;
    String uName,phno;
    TextView totalVal;
    Data order;
    long getChildrenCounts;
    String day="AM";
    String outTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        payment=(Button) findViewById(R.id.button);
        totalVal=(TextView) findViewById(R.id.total);
        mAuth= FirebaseAuth.getInstance();
        orderList = new ArrayList<>();

        final EditText acno=(EditText)findViewById(R.id.acountno);
        final EditText ifsc=(EditText)findViewById(R.id.ifsc);
        final EditText date=(EditText)findViewById(R.id.date);

        headerDatabase = FirebaseDatabase.getInstance().getReference().child("user");
        headerDatabase.child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                uName=dataSnapshot.child("name").getValue().toString();
                phno=dataSnapshot.child("phoninfo").getValue().toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Toast.makeText(this, ""+uName+","+phno, Toast.LENGTH_SHORT).show();

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(acno.getText())||TextUtils.isEmpty(ifsc.getText())||TextUtils.isEmpty(date.getText())){
                    Toast.makeText(Payment.this, "Enter Valid Details", Toast.LENGTH_SHORT).show();
                }else {


                    Cart = FirebaseDatabase.getInstance().getReference().child("Cart").child(mAuth.getUid());
                    purchase = FirebaseDatabase.getInstance().getReference().child("Purchase");


                    Cart.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            orderList.clear();

                            for (DataSnapshot orderChild : dataSnapshot.getChildren()) {


                                order = orderChild.getValue(Data.class);
                                orderList.add(order);
                                total = total + Double.parseDouble(order.getAmount());
                                getChildrenCounts = orderChild.getChildrenCount();


                            }
                            Date currentTime = Calendar.getInstance().getTime();
                            // final String outDate = currentTime.toString();
                            final String outDate = String.format(" %1$td-%1$tm-%1$tY", currentTime);
                            Time time = new Time();
                            time.setToNow();
                            // System.out.println("time: " + time.hour+":"+time.minute);
                            int hourofday = time.hour;
                            int minute = time.minute;
                            if (hourofday == 12) {
                                hourofday = 12;
                                day = "PM";
                            } else if (hourofday > 12 && hourofday < 24) {
                                hourofday = hourofday - 12;
                                day = "PM";
                            } else if (hourofday == 0) {
                                hourofday = 12;
                                day = "AM";
                            } else {
                                day = "AM";
                            }
                            outTime = "" + hourofday + ":" + minute + " " + day;

                            DatabaseReference db = purchase.push();
                            for (int i = 0; i < orderList.size(); i++) {
                                Data ol = orderList.get(i);

                                db.child("pd" + (i + 1)).setValue(ol.getName() + ",1," + ol.getTax() + "," + ol.getAmount() + "," + total + "," + uName + "," + phno + "," + "paid" + "," + outDate + "," + outTime);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });


                    startActivity(new Intent(Payment.this, DetailsActivity.class));
                }
            }
        });





    }
}
