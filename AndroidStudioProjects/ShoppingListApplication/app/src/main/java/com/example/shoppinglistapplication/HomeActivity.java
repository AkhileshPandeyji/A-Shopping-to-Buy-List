package com.example.shoppinglistapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;


import com.example.shoppinglistapplication.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {
    private FloatingActionButton home_fab;
    private Button save_btn;
    private EditText name_edt;
    private EditText type_edt;
    private EditText price_edt;
    private EditText qty_edt;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private RecyclerView recyclerView;

    private long totalPrice = 0;
    private TextView tot_price_txt;

    private EditText name_upd;
    private EditText type_upd;
    private EditText price_upd;
    private EditText qty_upd;
    private Button update_btn;
    private Button del_btn;
    private String data_id;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Shopping List").child(uid);
        mDatabase.keepSynced(true);

        //recycler view basics
        recyclerView = findViewById(R.id.home_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Online Shopping List");

        home_fab = findViewById(R.id.home_fab);
        tot_price_txt = findViewById(R.id.tot_price_home);
        home_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog();
            }
        });

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                totalPrice = 0;
                for(DataSnapshot snap:dataSnapshot.getChildren()){
                    Data sData = snap.getValue(Data.class);
                    totalPrice+=sData.getQty()*sData.getPrice();
                }
                tot_price_txt.setText(String.valueOf(totalPrice));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Data,MyViewHolder> recyclerAdapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(
                Data.class,
                R.layout.item_data,
                MyViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(final MyViewHolder myViewHolder, final Data data, final int position) {
                myViewHolder.setName(data.getName());
                myViewHolder.setDate(data.getDate());
                myViewHolder.setType(data.getType());
                myViewHolder.setPrice(data.getPrice());
                myViewHolder.setQty(data.getQty());

                myViewHolder.myView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        data_id = data.getId();
                        updateDialog(data.getName(),data.getType(),""+data.getQty(),""+data.getPrice());
                    }
                });

            }
        };


        recyclerView.setAdapter(recyclerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.log_out){
            mAuth.signOut();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
       View myView;
       public MyViewHolder(View itemView){
           super(itemView);
           myView = itemView;
       }

       public void setName(String name){
           TextView name_txt = myView.findViewById(R.id.name_item);
           name_txt.setText(name);
       }
       public void setDate(String date){
           TextView date_txt = myView.findViewById(R.id.date_item);
           date_txt.setText(date);
       }
       public void setType(String type){
           TextView type_txt = myView.findViewById(R.id.type_item);
           type_txt.setText(type);
       }
       public void setPrice(int price){
           TextView price_txt = myView.findViewById(R.id.price_item);
           String sprice = "Rs."+price;
           price_txt.setText(sprice);
       }
       public void setQty(int qty){
           TextView qty_txt = myView.findViewById(R.id.qty_item);
           String sqty = "Qty:"+qty;
           qty_txt.setText(sqty);
       }
    }

    private void customDialog(){
        AlertDialog.Builder aDialog = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
        View view = inflater.inflate(R.layout.input_data,null);
        final AlertDialog fDialog = aDialog.create();
        fDialog.setView(view);
        fDialog.show();

        name_edt = view.findViewById(R.id.name_inp);
        type_edt = view.findViewById(R.id.type_inp);
        price_edt = view.findViewById(R.id.price_inp);
        qty_edt = view.findViewById(R.id.qty_inp);
        save_btn = view.findViewById(R.id.input_save);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = name_edt.getText().toString().trim();
                String type = type_edt.getText().toString().trim();
                String price = price_edt.getText().toString().trim();
                String quantity = qty_edt.getText().toString().trim();

                if(TextUtils.isEmpty(name)){
                    name_edt.setError("Required");
                }
                if(TextUtils.isEmpty(type)){
                    type_edt.setError("Required");
                }
                if(TextUtils.isEmpty(price)){
                    price_edt.setError("Required");
                }
                if(TextUtils.isEmpty(quantity)){
                    qty_edt.setError("Required");
                }

                String id = mDatabase.push().getKey();
                String date = DateFormat.getDateInstance().format(new Date());

                int price_int = Integer.parseInt(price);
                int qty = Integer.parseInt(quantity);

                Log.d("msg",name+":"+type+":"+price_int+":"+qty+":"+id+":"+date);
                Data data = new Data(name,price_int,qty,type,id,date);
                mDatabase.child(id).setValue(data);
                Toast.makeText(getApplicationContext(),"Item saved",Toast.LENGTH_SHORT).show();
                fDialog.dismiss();
            }
        });
    }
    private void updateDialog(String name,String type,String qty,String price){
        AlertDialog.Builder uDialog = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater layoutInflater = LayoutInflater.from(HomeActivity.this);
        View iView = layoutInflater.inflate(R.layout.update_layout,null);
        final AlertDialog fDialog = uDialog.create();

        name_upd = iView.findViewById(R.id.name_upd);
        type_upd = iView.findViewById(R.id.type_upd);
        price_upd = iView.findViewById(R.id.price_upd);
        qty_upd = iView.findViewById(R.id.qty_upd);
        update_btn = iView.findViewById(R.id.update_btn);
        del_btn = iView.findViewById(R.id.delete_btn);

        name_upd.setText(name);
        type_upd.setText(type);
        price_upd.setText(price);
        qty_upd.setText(qty);

        fDialog.setView(iView);
        fDialog.show();

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uName = name_upd.getText().toString().trim();
                String uType = type_upd.getText().toString().trim();
                String uPrice = price_upd.getText().toString().trim();
                String uQty = qty_upd.getText().toString().trim();

                int uPriceI = Integer.parseInt(uPrice);
                int uQtyI = Integer.parseInt(uQty);

                String uDate = DateFormat.getDateInstance().format(new Date());

                Data uData = new Data(uName,uPriceI,uQtyI,uType,data_id,uDate);
                mDatabase.child(data_id).setValue(uData);

                fDialog.dismiss();
            }
        });

        del_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDatabase.child(data_id).removeValue();
                fDialog.dismiss();

            }
        });

    }

}
