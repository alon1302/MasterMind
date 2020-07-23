package com.example.mastermind.model.firebase;

import android.content.Context;
import android.nfc.Tag;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.mastermind.model.game.Record;
import com.example.mastermind.model.listeners.DataChangedListener;
import com.example.mastermind.model.user.User;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Queue;

public class RecordsRepo {

    public interface GetRecordsListener{
        void OnGetRecords();
    }

    private static ArrayList<RecordPerUser> records = null;
    private static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Records");
    private static final String TAG = "RecordsRepo";

    public static void addRecord(Record record, Context context){
        databaseReference.child(record.getTime() + record.getId()).setValue(record).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: successful");
            }
        });
    }

    private static void addSorted(RecordPerUser record) {
        boolean isIn = false;
        String key = "";
        if (records.isEmpty() || record.getTime()<records.get(0).getTime()) {
            records.add(0, record);
            Log.d(TAG, "ADD SORTED: ajk,shvfijsAGVFOUASDVFOUASDVFOUASDFVOAUFGOUASDFGV" + records);
            isIn = true;
        }
        else {
            for (int i = 1; i < records.size(); i++) {
                if (record.getTime() < records.get(i).getTime()) {
                    records.add(i - 1, record);
                    Log.d(TAG, "ADD SORTEDs: ajk,shvfijsAGVFOUASDVFOUASDVFOUASDFVOAUFGOUASDFGV" + records);
                    isIn = true;
                }
            }
        }
        if (!isIn){
            records.add(records.size()-1, record);
        }
        if (isIn && records.size()>10){
            key = records.get(10).getTime() + records.get(10).getUser().getValue().getId();
        }
        if (!key.equals("")){
            databaseReference.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "onSuccess: successful");
                }
            });
        }
    }

//    public static void sort(){
//        for (int i =0; i<records.size()-1; i++){
//            for (int j = 0; j<records.size()-i-1; j++){
//                if (records.get(j).getTime()> records.get(i).getTime()){
//                    RecordPerUser temp = records.get(j);
//                    records.set(j, records.get(i));
//                    records.set(i,temp);
//                }
//            }
//        }
//    }

    public static ArrayList<RecordPerUser> getRecords(Context context) {
        if (records == null){
            records = new ArrayList<>();
            loadRecords(context);
        }
        return records;
    }

    private static void loadRecords(final Context context){
        Query query = databaseReference.limitToFirst(10).orderByValue();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s: snapshot.getChildren()) {
                    Record r = s.getValue(Record.class);
                    RecordPerUser record = new RecordPerUser(r.getTime(), getUserInfo(r.getId(), context));
                    addSorted(record);
//                    DataChangedListener dataChangedListener = (DataChangedListener) context;
//                    dataChangedListener.onDataChanged();
                    Log.d(TAG, "onDataChange: " + record);
                }
                Log.d(TAG, "getRecords: ajk,shvfijsAGVFOUASDVFOUASDVFOUASDFVOAUFGOUASDFGV" + records);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: ERROR");
            }
        });
    }

    private static MutableLiveData<User> getUserInfo(String id, final Context context) {
        final MutableLiveData<User> mutableLiveData = new MutableLiveData<>();
        FirebaseDatabase.getInstance().getReference().child("Users").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                mutableLiveData.setValue(user);
                DataChangedListener dataChangedListener = (DataChangedListener) context;
                dataChangedListener.onDataChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: ERROR");
            }
        });
        return mutableLiveData;
    }

}
