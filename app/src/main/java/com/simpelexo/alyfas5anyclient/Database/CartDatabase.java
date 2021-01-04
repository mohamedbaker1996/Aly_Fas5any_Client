package com.simpelexo.alyfas5anyclient.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

 import okhttp3.internal.Version;

@Database(version = 1,entities = CartItem.class,exportSchema = false)
public abstract class CartDatabase extends RoomDatabase {
    public abstract CartDAO cartDAO();
    public static CartDatabase instance;

    public static CartDatabase getInstance(Context context){
        if (instance== null)
        instance = Room.databaseBuilder(context,CartDatabase.class,"ALyFas5anyv5").build();

        return instance;
    }
}
