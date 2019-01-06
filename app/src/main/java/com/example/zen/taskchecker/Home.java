package com.example.zen.taskchecker;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.zen.taskchecker.Modal.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class Home extends AppCompatActivity {

    private Toolbar tool;
    private FloatingActionButton btn;
    private RecyclerView recyclerView;

    private  FirebaseRecyclerAdapter adapter;

    private FirebaseAuth mAuth;
    private DatabaseReference database=FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        tool=findViewById(R.id.toolbar_home);
        setSupportActionBar(tool);
        getSupportActionBar().setTitle("Task Checker");
        btn=findViewById(R.id.button_create);

        recyclerView=findViewById(R.id.recycleview);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();
        String uid=mAuth.getUid();
        database=FirebaseDatabase.getInstance().getReference().child("TaskNote").child(uid);


        // RecyclerView..
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder myDialog=new AlertDialog.Builder(Home.this);
                LayoutInflater layoutInflater=LayoutInflater.from(Home.this);
                View myview=layoutInflater.inflate(R.layout.inputfield,null);
                myDialog.setView(myview);
                final AlertDialog alertDialog=myDialog.create();

                final EditText title=myview.findViewById(R.id.title_edt);
                final EditText note=myview.findViewById(R.id.note_edt);
                TextView textView=myview.findViewById(R.id.Taskid);

                Button button=myview.findViewById(R.id.btn);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String mTitle=title.getText().toString().trim();
                        String mNote=note.getText().toString().trim();

                        if(TextUtils.isEmpty(mTitle))
                        {
                            title.setError("Required Filed..");
                            return;
                        }
                        if(TextUtils.isEmpty(mNote))
                        {
                            note.setError("Required Filed..");
                            return;
                        }
                        String id=database.push().getKey();
                        String date= DateFormat.getDateInstance().format(new Date());

                        Data data=new Data(mNote,date, mTitle, id);


                        database.child(id).setValue(data);
                        Toast.makeText(getApplicationContext(),"Task Inserted",Toast.LENGTH_LONG).show();
                        alertDialog.dismiss();

                    }
                });
                alertDialog.show();

            }
        });

        queryList();


        //setSupportActionBar(tool);
       // getSupportActionBar().setTitle("Task Checker");
    }

    void queryList()
    {
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();
        String uid=mAuth.getUid();


        String id="-LVY0xw940tIQVv6_zG2";
        Query query = FirebaseDatabase.getInstance()
                .getReference().child("TaskNote").child(uid).orderByChild("id");
        final FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>().
                setQuery(query,Data.class)
                .build();

        adapter= new FirebaseRecyclerAdapter<Data, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Data model) {
                holder.setTitle(model.getTitle());
                holder.setNote(model.getNote());
                holder.setDate(model.getDate());


            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item, viewGroup, false);
                return new  ViewHolder(view);
            }
        };

          recyclerView.setAdapter(adapter);


    }

    @Override
    protected void onStart() {
        super.onStart();

        adapter.startListening();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{

        View myView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            myView=itemView;

        }
        public void setTitle(String title)
        {
            TextView mTitle=myView.findViewById(R.id.item_title);
            mTitle.setText(title);

        }
        public void setNote(String note)
        {
            TextView mNote=myView.findViewById(R.id.item_note);
            mNote.setText(note);

        }
        public void setDate(String Date)
        {
            TextView mDate=myView.findViewById(R.id.item_date);
            mDate.setText(Date);
        }
    }
}


