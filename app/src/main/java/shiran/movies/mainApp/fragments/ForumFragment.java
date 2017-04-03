package shiran.movies.mainApp.fragments;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import shiran.movies.R;
import shiran.movies.U;
import shiran.movies.adapter.ForumListAdapter;
import shiran.movies.mainApp.model.Message;


public class ForumFragment extends Fragment {
    private final String DEFAULT_TOPIC = "General";
    private final String FORUM = "forum";
    private EditText txtInput;
    private ImageButton btnOpenSend, btnCancel, btnSend;
    private static String lastInputText;
    private CardView card;
    Spinner dropdown;
    DatabaseReference myRef;

    RecyclerView recyclerView;
    ForumListAdapter adapter;
    List<Message> messages = new ArrayList<>();

    public ForumFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View frag = inflater.inflate(R.layout.fragment_forum, container, false);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dropdown = (Spinner) frag.findViewById(R.id.spinner_forum);
        recyclerView = (RecyclerView) frag.findViewById(R.id.recycler_forum);
        txtInput = (EditText) frag.findViewById(R.id.txt_input_msg);
        btnOpenSend = (FloatingActionButton) frag.findViewById(R.id.fab_open_ok);
        btnCancel = (ImageButton) frag.findViewById(R.id.btn_cancel);
        btnSend = (ImageButton) frag.findViewById(R.id.btn_send);
        card = (CardView) frag.findViewById(R.id.card_send_panel);

        myRef = database.getReference(FORUM);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String[] items = new String[(int) dataSnapshot.getChildrenCount()];
                items[0] = DEFAULT_TOPIC;
                int i = 1;
                for (DataSnapshot topic : dataSnapshot.getChildren()) {
                    if (topic.getKey().equals(DEFAULT_TOPIC)) continue;
                    items[i] = topic.getKey();
                    i++;
                }


                try {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
                    dropdown.setAdapter(adapter);
                    dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                            loadTopic(((TextView) view).getText().toString());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                } catch (NullPointerException e) {
                    //if context == null / pager recycler in other page
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        btnOpenSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setStateOpen();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMsg();
            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtInput.getText().toString().isEmpty())
                    setStateClose();
                else
                    txtInput.setText("");
            }
        });

        return frag;
    }

    private void loadTopic(String topic) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference(FORUM + "/" + topic);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messages.clear();
                for (DataSnapshot msg : dataSnapshot.getChildren()) {
                    messages.add(msg.getValue(Message.class));
                }
                adapter = new ForumListAdapter(messages, getContext());
                LinearLayoutManager lm = new LinearLayoutManager(getContext());
                lm.setStackFromEnd(true);
                recyclerView.setLayoutManager(lm);

                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("test", "Failed to read value.", error.toException());
            }
        });
    }

    private void sendMsg() {
        Message m = new Message();
        m.setMessage(txtInput.getText().toString());
        m.setType(1);
        m.setSender(FirebaseAuth.getInstance().getCurrentUser().getUid());
        String key = myRef.push().getKey();
        myRef.child(key).setValue(m);
        setStateClose();
    }

    private void setStateClose() {
        btnOpenSend.setVisibility(View.VISIBLE);
        card.setVisibility(View.GONE);
        txtInput.setText("");
        U.hideKeyboard(getContext());
    }

    private void setStateOpen() {
        btnOpenSend.setVisibility(View.GONE);
        card.setVisibility(View.VISIBLE);
        txtInput.requestFocus();
        U.ShowKeyboard(getContext(), txtInput);

    }


    @Override
    public void onPause() {
        super.onPause();
        lastInputText = txtInput.getText().toString();
    }

    @Override
    public void onResume() {
        super.onResume();
        txtInput.setText(lastInputText);
    }
}
