package com.example.zen.taskchecker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.*;
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

    //update fields
    private EditText titleup;
    private EditText noteup;
    private  Button btnDel;
    private Button btnUp;

    //Firebase
    private  FirebaseRecyclerAdapter adapter;

    //Variables
    private String title;
    private String post_key;
    private String note;
    private View view;

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



        Query query = FirebaseDatabase.getInstance()
                .getReference().child("TaskNote").child(uid).orderByChild("id");
        final FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>().
                setQuery(query,Data.class)
                .build();

        adapter= new FirebaseRecyclerAdapter<Data, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull final Data model) {
                holder.setTitle(model.getTitle());
                holder.setNote(model.getNote());
                holder.setDate(model.getDate());
                holder.myView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        post_key=getRef(position).getKey();
                        title=model.getTitle();
                        note=model.getNote();

                        UpdateData();
                    }
                });


            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                 view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item, viewGroup, false);
                return new  ViewHolder(view);
            }
        };


          recyclerView.setAdapter(adapter);


    }


    public void UpdateData()
    {
        final AlertDialog.Builder dialog=new AlertDialog.Builder(Home.this);
        LayoutInflater inflater=LayoutInflater.from(Home.this);

        View view=inflater.inflate(R.layout.update,null);
        dialog.setView(view);
        final AlertDialog alertDialog=dialog.create();


        titleup=view.findViewById(R.id.title_update);
        noteup=view.findViewById(R.id.note_update);
        btnDel=view.findViewById(R.id.btn_delete);
        btnUp=view.findViewById(R.id.btn_update);

 // titleup.setText(title);
        titleup.setText(title.toString());
        titleup.setSelection(title.length());
        noteup.setText(note.toString());
        noteup.setSelection(note.length());

        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                title=titleup.getText().toString().trim();
                note=noteup.getText().toString().trim();

                String mDate=DateFormat.getDateInstance().format(new Date());

                Data data=new Data(note,mDate, title, post_key);
                database.child(post_key).setValue(data);

                alertDialog.dismiss();
            }
        });
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.child(post_key).removeValue();


                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        adapter.startListening();
    }

    @SuppressLint("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.logout:
                // Single menu item is selected do something
                // Ex: launching new activity/screen or show alert message
                Toast.makeText(Home.this, "Loging out..", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                startActivity(new Intent(Home.this,MainActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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


