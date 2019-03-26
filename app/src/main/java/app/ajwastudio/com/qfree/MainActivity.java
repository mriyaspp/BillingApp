package app.ajwastudio.com.qfree;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference headerDatabase;
    private FirebaseAuth mAuth;
    private String s_uid = null;
    private FirebaseUser user;
    private RecyclerView homeRecyclerView;
    private List<Data> orderList;
    private FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initial();
    }

    private void initial() {
        headerDatabase = FirebaseDatabase.getInstance().getReference().child("user");
        mAuth= FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Product");
        homeRecyclerView = findViewById(R.id.my_order_recycler);
        homeRecyclerView.setHasFixedSize(true);
        homeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        try {
            s_uid = mAuth.getUid();

        }catch (Exception e){
            s_uid = null;
        }
        if(s_uid==null){
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            finish();
        }
        else {

        }

        orderList = new ArrayList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderList.clear();
                for (DataSnapshot orderChild : dataSnapshot.getChildren()) {
                    Data order = orderChild.getValue(Data.class);
                    orderList.add(order);
                    //Toast.makeText(MainActivity.this, ""+orderList.get(0), Toast.LENGTH_SHORT).show();
                }
//                final Data orderIt = orderList.get(2);
//                Toast.makeText(MainActivity.this, ""+orderIt.getAmount(), Toast.LENGTH_SHORT).show();

                OrderListAdapter adapter = new OrderListAdapter(orderList, getApplicationContext());
                homeRecyclerView.setAdapter(adapter);
    //            progressBar.setVisibility(View.GONE);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }


    public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderViewHolder> {
        private List<Data> orderList;
        private Context mCtx;
        private LayoutInflater inflater;

        OrderListAdapter(List<Data> orderList, Context mCtx) {
            this.mCtx = mCtx;
            this.orderList = orderList;

        }

        @NonNull
        @Override
        public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            inflater = LayoutInflater.from(mCtx);
            View view = inflater.inflate(R.layout.home_child, viewGroup, false);
            return new OrderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderViewHolder orderViewHolder, int i) {
            final Data order = orderList.get(i);
            Toast.makeText(mCtx, ""+order.getName(), Toast.LENGTH_SHORT).show();
            Toast.makeText(mCtx, ""+i, Toast.LENGTH_SHORT).show();
            orderViewHolder.name.setText(order.getName());
            orderViewHolder.tax.setText(order.getTax());
            orderViewHolder.price.setText(order.getAmount());
//            orderViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent a=new Intent(MyOrderActivity.this,QrGenerator.class);
//                    a.putExtra("product",order.getProductId());
//                    startActivity(a);
//                }
//            });

        }

        @Override
        public int getItemCount() {
            return orderList.size();
        }

        class OrderViewHolder extends RecyclerView.ViewHolder {

            private TextView tax;
            private TextView name;
            private TextView price;
            private CardView cardView;

            OrderViewHolder(@NonNull View itemView) {
                super(itemView);

                tax = itemView.findViewById(R.id.item_tax);
                name = itemView.findViewById(R.id.item_tiitle);
                price = itemView.findViewById(R.id.item_price);
                cardView=itemView.findViewById(R.id.card);
            }
        }
    }


}
