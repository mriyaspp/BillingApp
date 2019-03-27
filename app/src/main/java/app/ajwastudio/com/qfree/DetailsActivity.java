package app.ajwastudio.com.qfree;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
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
import com.google.zxing.WriterException;

import java.util.ArrayList;
import java.util.List;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

public class DetailsActivity extends AppCompatActivity {

    private DatabaseReference Cart;
    private FirebaseAuth mAuth;
    private RecyclerView homeRecyclerView;
    private List<Bill> orderList;
    Double total=0.0;
    private String dat;
    private ImageView productQR;
    private Button download;


    private String key;
    private Bitmap bitmap;
    private QRGEncoder qrgEncoder;
    private String savePath = Environment.getExternalStorageDirectory().getPath() + "/QFree/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Initial();
        genQRCode();
    }

    private void genQRCode() {

        if (key.length() > 0) {
            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;
            int smallerDimension = width < height ? width : height;
            smallerDimension = smallerDimension * 3 / 4;

            qrgEncoder = new QRGEncoder(
                    key, null,
                    QRGContents.Type.TEXT,
                    smallerDimension);
            try {
                bitmap = qrgEncoder.encodeAsBitmap();
                productQR.setImageBitmap(bitmap);
            } catch (WriterException e) {
                Log.v("QR ERROR", e.toString());
            }
        } else {
            Toast.makeText(this, "QR Code Error", Toast.LENGTH_SHORT).show();
        }


        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED
                    ) {
                        ActivityCompat.requestPermissions(DetailsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                }
                boolean save;
                String result;
                try {
                    save = QRGSaver.save(savePath, key, bitmap, QRGContents.ImageType.IMAGE_JPEG);
                    result = save ? "Image Saved" : "Image Not Saved";
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
    private void Initial() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/opensans.ttf");
        TextView head=(TextView)findViewById(R.id.head);
        homeRecyclerView = findViewById(R.id.billList);
        homeRecyclerView.setHasFixedSize(true);
        homeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productQR = findViewById(R.id.ivQRCode);
        download = findViewById(R.id.btDownload);

        head.setTypeface(typeface);

        orderList = new ArrayList<>();
        mAuth= FirebaseAuth.getInstance();
        Cart= FirebaseDatabase.getInstance().getReference().child("Purchase");


        Cart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                orderList.clear();
                for (DataSnapshot billChild : dataSnapshot.getChildren()) {
                    key=billChild.getKey().toString();
                    if (billChild.getValue() != null) {
                        String value = billChild.getValue().toString();
                        String[] arr = value.split(",");
                        Bill bill = new Bill();
                        try {
                            bill.setName(arr[0]);
                            bill.setQty(arr[1]);
                            bill.setTax(arr[2]);
                            bill.setAmount(arr[3]);
                            bill.setTotal(arr[4]);
                    //        total += Long.parseLong(arr[4]);
                            bill.setUserName(arr[5]);
                            bill.setPhon(arr[6]);
                            bill.setStatus(arr[7]);
                            bill.setDate(arr[8]);
                            dat = arr[8];
                            orderList.add(bill);

//                            total=total+Double.parseDouble(bill.getAmount());
                            Toast.makeText(DetailsActivity.this, ""+billChild.getKey(), Toast.LENGTH_SHORT).show();
                        } catch (IndexOutOfBoundsException e) {
                            Log.e("Array Err", e.getMessage());
                        }
                    }



                   // Data order = orderChild.getValue(Data.class);



               //     Toast.makeText(DetailsActivity.this, ""+order.getName(), Toast.LENGTH_SHORT).show();
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
        private List<Bill> orderList;
        private Context mCtx;
        private LayoutInflater inflater;

        OrderListAdapter(List<Bill> orderList, Context mCtx) {
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
                final Bill order = orderList.get(i);
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
