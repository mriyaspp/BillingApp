package app.ajwastudio.com.qfree;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    private DatabaseReference Cart;
    private FirebaseAuth mAuth;
    private RecyclerView homeRecyclerView;
    private List<Data> orderList;
    Double total=0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Initial();
    }

    private void Initial() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/opensans.ttf");
        TextView head=(TextView)findViewById(R.id.head);
        homeRecyclerView = findViewById(R.id.billList);
        homeRecyclerView.setHasFixedSize(true);
        homeRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        head.setTypeface(typeface);

        orderList = new ArrayList<>();
        mAuth= FirebaseAuth.getInstance();
        Cart= FirebaseDatabase.getInstance().getReference().child("Cart").child(mAuth.getUid());


        Cart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                orderList.clear();
                for (DataSnapshot orderChild : dataSnapshot.getChildren()) {
                    Data order = orderChild.getValue(Data.class);
                    orderList.add(order);

                    total=total+Double.parseDouble(order.getAmount());
                    Toast.makeText(DetailsActivity.this, ""+order.getName(), Toast.LENGTH_SHORT).show();
                }



                OrderListAdapter adapter = new OrderListAdapter(orderList, getApplicationContext());
                homeRecyclerView.setAdapter(adapter);
                if(orderList.isEmpty()){
                    Toast.makeText(DetailsActivity.this, "Cart is empty", Toast.LENGTH_SHORT).show();
                }
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
            View view = inflater.inflate(R.layout.billlist, viewGroup, false);
            return new OrderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderViewHolder orderViewHolder, int i) {
            try {
                final Data order = orderList.get(i);
            Toast.makeText(mCtx, ""+order.getName(), Toast.LENGTH_SHORT).show();
            Toast.makeText(mCtx, ""+i, Toast.LENGTH_SHORT).show();
                orderViewHolder.name.setText(order.getName());
                orderViewHolder.tax.setText(order.getTax());
                orderViewHolder.price.setText(order.getAmount());
                orderViewHolder.sno.setText(""+(i+1));
                orderViewHolder.qty.setText("" + 1);
                Double subTot = (1 * Double.parseDouble(order.getAmount())) + Double.parseDouble(order.getTax());
                orderViewHolder.total.setText("" + subTot);
            }catch (Exception e){

            }
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
            private TextView sno;
            private TextView qty;
            private TextView total;
            private CardView cardView;

            OrderViewHolder(@NonNull View itemView) {
                super(itemView);

                tax = itemView.findViewById(R.id.tax);
                name = itemView.findViewById(R.id.name);
                price = itemView.findViewById(R.id.rate);
                sno = itemView.findViewById(R.id.sno);
                qty = itemView.findViewById(R.id.qty);
                total = itemView.findViewById(R.id.total);
                cardView=itemView.findViewById(R.id.card);
            }
        }
    }



}
