package com.hewei.addressselect;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;

import com.hewei.addressselect.model.AddressNode;
import com.hewei.addressselect.model.AppDataBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengyinpeng on 2018/9/3.
 */
public class AddressItem implements TreeSelectFragment.Node {
    private AddressNode node;
    private int depth;

    public AddressItem(AddressNode node, int depth) {
        this.node = node;
        this.depth = depth;

        if (node == null) {
            this.depth = 0;
        }
    }

    @Override
    public String getDisplayString() {
        return node == null ? "root" : node.label;
    }

    @Override
    public LiveData<List<TreeSelectFragment.Node>> getChildren() {
        long id = node == null ? 0 : node.id;

        return Transformations.map(AppDataBase.getInstance().getAddressDAO().queryNode(id),
                new Function<List<AddressNode>, List<TreeSelectFragment.Node>>() {
                    @Override
                    public List<TreeSelectFragment.Node> apply(List<AddressNode> input) {
                        List<TreeSelectFragment.Node> list = new ArrayList<>(input.size());
                        for (AddressNode node : input) {
                            AddressItem item = new AddressItem(node, depth + 1);
                            list.add(item);
                        }

                        return list;
                    }
                });
    }

    @Override
    public int getDepth() {
        return depth;
    }

    @Override
    public String toString() {
        return getDisplayString();
    }
}
