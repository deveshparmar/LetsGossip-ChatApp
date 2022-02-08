package com.codewithdevesh.letsgossip.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.codewithdevesh.letsgossip.R;
import com.codewithdevesh.letsgossip.adapter.ContactAdapter;
import com.codewithdevesh.letsgossip.databinding.ActivityContactBinding;
import com.codewithdevesh.letsgossip.model.RecentChatModel;
import com.codewithdevesh.letsgossip.model.UserModel;
import com.codewithdevesh.letsgossip.utilities.SessionManagement;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ContactActivity extends AppCompatActivity {
    private ActivityContactBinding binding;
    private List<UserModel>list ;
    private ContactAdapter adapter;
    private FirebaseUser user;
    private DatabaseReference reference;
    private static final int READ_CONTACTS=79;
    private ListView contactList;
    private ArrayList mobileContactList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_contact);
        SessionManagement.init(ContactActivity.this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        list = new ArrayList<>();

        if(user!=null){
            getContactFromPhone();
        }
        if(mobileContactList!=null){
            getContactList();
        }
        binding.sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filter(s);
                return false;
            }
        });

    }

    private void getContactList() {
        reference = FirebaseDatabase.getInstance().getReference("User");
        reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
               for(DataSnapshot data : dataSnapshot.getChildren()){

                    UserModel model = data.getValue(UserModel.class);

                    if(model.getPhoneNo()!=null && !model.getPhoneNo().equals(SessionManagement.getUserPhoneNo())){
                        if(mobileContactList.contains(model.getPhoneNo())){
                            binding.shimmerLayoutC.stopShimmer();
                            binding.shimmerLayoutC.setVisibility(View.GONE);
                            list.add(model);
                        }
                    }

               }
               adapter = new ContactAdapter(list,ContactActivity.this);
               binding.rvContactList.setAdapter(adapter);
               binding.rvContactList.setHasFixedSize(true);
               binding.rvContactList.setLayoutManager(new LinearLayoutManager(ContactActivity.this));
               adapter.notifyDataSetChanged();
            }
        });
    }

    private void getContactFromPhone() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            mobileContactList = getContacts();
        } else {
            requestPermission();
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
            // show UI part if you want here to show some rationale !!!
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    READ_CONTACTS);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    READ_CONTACTS);
        }
    }

    @SuppressLint("Range")
    private ArrayList getAllPhoneContacts() {
        ArrayList<String> phoneList = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(

                        cur.getColumnIndex(ContactsContract.Contacts._ID));
//                String name = cur.getString(cur.getColumnIndex(
//                        ContactsContract.Contacts.DISPLAY_NAME));
//                nameList.add(name);

                if (cur.getInt(cur.getColumnIndex( ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneList.add(phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
        return phoneList;
    }
    private ArrayList getContacts(){
        ArrayList<String> phoneList = new ArrayList<>();
        final String[] PROJECTION = new String[] {
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, null);
        if (cursor != null) {
            try {
                final int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                String name, number;
                while (cursor.moveToNext()) {
                    name = cursor.getString(nameIndex);
                    number = cursor.getString(numberIndex);
                    phoneList.add(number);

                }
            } finally {
                cursor.close();
            }
        }
        return phoneList;
    }
    private void filter(String newText) {
        ArrayList<UserModel>filterList = new ArrayList<>();
        for(UserModel model: list){
            if(model.getName().toLowerCase(Locale.ROOT).contains(newText.toLowerCase(Locale.ROOT))){
                filterList.add(model);
            }
        }
        if(filterList.isEmpty()){
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        }else{
            adapter.filterList(filterList);
        }
    }

}