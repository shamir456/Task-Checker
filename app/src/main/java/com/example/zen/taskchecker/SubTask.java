package com.example.zen.taskchecker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

public class SubTask extends AppCompatActivity {


    private Toolbar tool;
    private FloatingActionButton btn;
    private RecyclerView recyclerView;

    //update fields
    private EditText titleup;
    private EditText noteup;
    private Button btnDel;
    private Button btnUp;

    private Button complete;
    private Button close;


    //Firebase
    private FirebaseRecyclerAdapter adapter;

    //Variables
    private String title;
    private String post_key;
    private String note;
    private View view;

    private String pId;
    private String date;

    private FirebaseAuth mAuth;
    private DatabaseReference database= FirebaseDatabase.getInstance().getReference();
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
        if(getIntent()!=null)
        {
            pId=getIntent().getStringExtra("Id");


            System.out.println(pId);
        }
        if(!pId.isEmpty())
        {
            database=FirebaseDatabase.getInstance().getReference().child("TaskNote").child(uid).child("SubTasks");

            System.out.println(database);

        }



        // RecyclerView..
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder myDialog=new AlertDialog.Builder(SubTask.this);
                LayoutInflater layoutInflater=LayoutInflater.from(SubTask.this);
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
                        int status=0;

                        Data data=new Data(mNote,date, mTitle, id,status);


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
//        if(getIntent()!=null)
//        {
//            pId=getIntent().getStringExtra("ProductId");
//
//
//            System.out.println(pId);
//        }
//        if(!pId.isEmpty())
//        {
//            database=FirebaseDatabase.getInstance().getReference().child("TaskNote").child(uid).child(pId);
//
//        }



        Query query = FirebaseDatabase.getInstance()
                .getReference().child("TaskNote").child(uid).child("SubTasks").orderByChild("id");
        final FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>().
                setQuery(query,Data.class)
                .build();

        adapter= new FirebaseRecyclerAdapter<Data, SubTask.ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SubTask.ViewHolder holder, final int position, @NonNull final Data model) {
                holder.setTitle(model.getTitle());
                holder.setNote(model.getNote());
                holder.setDate(model.getDate());
                //       holder.setstatus(model.getStatus());
                holder.myView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        post_key=getRef(position).getKey();
                        title=model.getTitle();
                        note=model.getNote();
                        date=model.getDate();

                        if(model.getStatus()==0)
                        {
                            UpdateData();

                        }
                        else if(model.getStatus()==1)
                        {
                            showCompletedNote();
                        }


                    }
                });


            }

            @NonNull
            @Override
            public SubTask.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item, viewGroup, false);
                return new SubTask.ViewHolder(view);
            }
        };


        recyclerView.setAdapter(adapter);


    }

    private void showCompletedNote() {

        final AlertDialog.Builder dialog=new AlertDialog.Builder(SubTask.this);
        LayoutInflater inflater=LayoutInflater.from(SubTask.this);

        View view=inflater.inflate(R.layout.show,null);
        dialog.setView(view);
        final AlertDialog alertDialog=dialog.create();

        titleup=view.findViewById(R.id.title_upd);
        noteup=view.findViewById(R.id.note_upd);
        btnDel=view.findViewById(R.id.btn_del);
        btnUp=view.findViewById(R.id.btn_upd);
        close=view.findViewById(R.id.btn_close);


        titleup.setText(title.toString());
        titleup.setSelection(title.length());
        noteup.setText(note.toString());
        noteup.setSelection(note.length());



        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                title=titleup.getText().toString().trim();
                note=noteup.getText().toString().trim();

                int status=0;

                String mDate=DateFormat.getDateInstance().format(new Date());

                Data data=new Data(note,mDate, title, post_key,status);
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
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                //String mDate=DateFormat.getDateInstance().format(new Date());

                alertDialog.dismiss();



            }
        });

        alertDialog.show();



    }


    public void UpdateData()
    {
        final AlertDialog.Builder dialog=new AlertDialog.Builder(SubTask.this);
        LayoutInflater inflater=LayoutInflater.from(SubTask.this);

        View view=inflater.inflate(R.layout.update,null);
        dialog.setView(view);
        final AlertDialog alertDialog=dialog.create();


        titleup=view.findViewById(R.id.title_update);
        noteup=view.findViewById(R.id.note_update);
        btnDel=view.findViewById(R.id.btn_delete);
        btnUp=view.findViewById(R.id.btn_update);
        complete=view.findViewById(R.id.btn_complete);


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

                int status=0;

                String mDate=DateFormat.getDateInstance().format(new Date());

                Data data=new Data(note,mDate, title, post_key,status);
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
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int status=1;

                //String mDate=DateFormat.getDateInstance().format(new Date());

                Data data=new Data(note,date, title, post_key,status);
                database.child(post_key).setValue(data);
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
                Toast.makeText(SubTask.this, "Loging out..", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                startActivity(new Intent(SubTask.this,MainActivity.class));
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
//        public void setstatus(int status)
//        {
//            TextView stat=myView.findViewById(R.id.stat);
//            stat.setText("status");
//            stat.setBackgroundColor(0xc31d17);
//            if (status==1)
//            stat.setBackgroundColor(0xc31d17);
//            else if (status==0)
//                stat.setBackgroundColor(0xc31d17);
//
//
//        }
    }
}
