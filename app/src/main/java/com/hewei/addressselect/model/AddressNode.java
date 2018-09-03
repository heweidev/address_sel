package com.hewei.addressselect.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by fengyinpeng on 2018/9/3.
 */

@Entity(tableName = "address_nodes", indices = {@Index("id")})
public class AddressNode {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String label;
    public String value;
    public long parentId;
}
