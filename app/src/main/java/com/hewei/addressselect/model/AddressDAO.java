package com.hewei.addressselect.model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by fengyinpeng on 2018/9/3.
 */

@Dao
public interface AddressDAO {
    @Insert
    long[] addNode(List<AddressNode> nodes);

    @Insert
    long addNode(AddressNode node);

    @Query("select * from address_nodes where parentId = :parentId")
    LiveData<List<AddressNode>> queryNode(long parentId);

    @Query("select * from address_nodes where id = :id")
    LiveData<AddressNode> queryNode2(long id);
}
