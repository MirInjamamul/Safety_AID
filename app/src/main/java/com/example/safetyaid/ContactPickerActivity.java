package com.example.safetyaid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.safetyaid.Model.Contact;
import com.example.safetyaid.adapter.ContactAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContactPickerActivity extends AppCompatActivity {

    private RecyclerView rv;
    private SharedPreferences prefs;
    private String mode;

    private List<Contact> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_picker);

        getSupportActionBar().setTitle("Pick contacts");

        prefs = getSharedPreferences("conf", MODE_PRIVATE);
        mode = getIntent().getDataString();

        list = new ArrayList<>();
        rv = findViewById(R.id.contactList);
        rv.setAdapter(new ContactAdapter(list));
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent contacts = new Intent(Intent.ACTION_PICK, ContactsContract.PhoneLookup.CONTENT_FILTER_URI)
                    .setType(ContactsContract.Contacts.CONTENT_TYPE);//Something doesn't seem to work... Disabling this for now
                startActivityForResult(contacts, 0);*/

                final View content = getLayoutInflater().inflate(R.layout.content_dialog_contact_entry, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(ContactPickerActivity.this);

                final ArrayAdapter<String> ad = new ArrayAdapter<>(ContactPickerActivity.this, R.layout.support_simple_spinner_dropdown_item);
                ad.addAll(((ContactAdapter)rv.getAdapter()).getGroups().toArray(new String[]{}));
                ((Spinner)content.findViewById(R.id.group_select)).setAdapter(ad);

                AlertDialog alert = builder.setView(content)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Contact c = new Contact(((TextView)content.findViewById(R.id.contact_name)).getText().toString(),
                                        ((TextView)content.findViewById(R.id.contact_number)).getText().toString(),
                                        ((String)((Spinner)content.findViewById(R.id.group_select)).getSelectedItem()));
                                list.add(c);
                                rv.getAdapter().notifyItemInserted(list.indexOf(c));
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .setNeutralButton("New group", null)
                        .create();
                alert.show();
                alert.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final EditText et = new EditText(ContactPickerActivity.this);
                        new AlertDialog.Builder(ContactPickerActivity.this)
                                .setTitle("Enter new group name")
                                .setView(et)
                                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ad.add(et.getText().toString());
                                    }
                                })
                                .show();
                    }
                });
            }
        });

        //Import data from shared prefs
        for(String contactString : prefs.getStringSet(mode, new HashSet<String>()))
        {
            String[] parts = contactString.split(";");

            Contact c = new Contact(
                    (parts.length >= 2 ? parts[1] : ""),
                    parts[0],
                    (parts.length >= 3 ? parts[2] : "General")
            );

            list.add(c);
            rv.getAdapter().notifyItemInserted(list.indexOf(c));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contactpicker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Set<String> newValues = new HashSet<>();
        for(int i = 0; i < rv.getAdapter().getItemCount(); i++)
        {
            Contact c = list.get(i);
            newValues.add(c.number + ";" + c.name + ";" + c.groupID);
        }
        prefs.edit().putStringSet(getString(R.string.var_numbers_notify), newValues).putStringSet(getString(R.string.var_numbers_notify), newValues).apply();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Set<String> newValues = new HashSet<>();
        for(int i = 0; i < rv.getAdapter().getItemCount(); i++)
        {
            Contact c = list.get(i);
            newValues.add(c.number + ";" + c.name + ";" + c.groupID);
        }
        prefs.edit().putStringSet(mode, newValues).apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
            Cursor cursor = getContentResolver().query(data.getData(), projection, null, null, null);

            if (cursor.moveToFirst()) {
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String name = cursor.getString(nameIndex);
                String number = cursor.getString(numberIndex);
            }

            cursor.close();
        }
    }

}