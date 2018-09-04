package com.hewei.addressselect.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by fengyinpeng on 2018/9/3.
 */

@Database(entities = {
        AddressNode.class
    }, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    private static AppDataBase sInst;

    public static void init(Context context) {
        if (sInst != null) {
            throw new IllegalStateException("init only once!");
        }

        sInst = Room.databaseBuilder(context, AppDataBase.class, "address")
                .build();
    }

    public static AppDataBase getInstance() {
        if (sInst == null) {
            throw new IllegalStateException("call init first!");
        }

        return sInst;
    }

    public abstract AddressDAO getAddressDAO();
}
