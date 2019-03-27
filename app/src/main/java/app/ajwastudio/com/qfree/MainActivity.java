package app.ajwastudio.com.qfree;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private Button OrderButton;
    Double total = 0.0;
    TextView Total;
    ProgressBar progressBar;
    OrderListAdapter adapter;

    private DatabaseReference Cart;

    //shared pref

//    SharedPreferences appSharedPrefs;
//    SharedPreferences.Editor prefsEditor;
//    Gson gson;


    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        try {
            s_uid = mAuth.getUid();

        } catch (Exception e) {
            s_uid = null;
        }
        if (s_uid == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else {
            initial();
            //      progressBar.setVisibility(View.VISIBLE);
            addToCart();
            Toast.makeText(this, "" + key, Toast.LENGTH_SHORT).show();
        }

    }

    private void addToCart() {
        final DatabaseReference mBillRef = database.getReference().child("Product");
        orderList = new ArrayList<>();

        Cart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                progressBar.setVisibility(View.VISIBLE);

                try {

                    orderList.clear();
                    for (DataSnapshot orderChild : dataSnapshot.getChildren()) {
                        Data order = orderChild.getValue(Data.class);
                        order.setId(orderChild.getKey());
                        orderList.add(order);
                        total = total + Double.parseDouble(order.getAmount());
                        //Toast.makeText(MainActivity.this, ""+orderList.get(0), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {

                }


                Total.setText("" + total);

                adapter = new OrderListAdapter(orderList, getApplicationContext());
                homeRecyclerView.setAdapter(adapter);
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
                itemTouchHelper.attachToRecyclerView(homeRecyclerView);
                if (orderList.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Cart is empty", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        OrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Payment.class));

            }
        });


    }

    private void initial() {

        progressBar = (ProgressBar) findViewById(R.id.prograss);
        OrderButton = (Button) findViewById(R.id.order);
        headerDatabase = FirebaseDatabase.getInstance().getReference().child("user");
        Cart = FirebaseDatabase.getInstance().getReference().child("Cart").child(mAuth.getUid());
        FloatingActionButton fButton = (FloatingActionButton) findViewById(R.id.addNew);
        Total = (TextView) findViewById(R.id.cart_price);
        fButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ScanQR.class));
            }
        });


        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Product");
        homeRecyclerView = findViewById(R.id.my_order_recycler);
        homeRecyclerView.setHasFixedSize(true);
        homeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        key = getIntent().getStringExtra("key");


        orderList = new ArrayList<>();


    }

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            //Remove swiped item from list and notify the RecyclerView
            int position = viewHolder.getAdapterPosition();
            String key = adapter.orderList.get(position).getId();
            database.getReference().child("Cart").child(mAuth.getUid()).child(key).setValue(null);
            orderList.remove(position);
            adapter.notifyDataSetChanged();

        }
    };

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
//            Toast.makeText(mCtx, ""+order.getName(), Toast.LENGTH_SHORT).show();
//            Toast.makeText(mCtx, ""+i, Toast.LENGTH_SHORT).show();
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
                cardView = itemView.findViewById(R.id.card);
            }
        }
    }


}
