package com.hewei.addressselect;

import android.app.Dialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by fengyinpeng on 2017/5/13.
 */

public class TreeSelectFragment extends DialogFragment implements AdapterView.OnItemClickListener, TabLayout.OnTabSelectedListener {
    private Node mCurNode;
    private List<Node> mSelects = new LinkedList<>();
    private Node mDataProvider;
    private boolean mSelectByManual = false;

    private TabLayout mTabLayout;
    private ListView mListView;
    private BaseAdapter mAdapter;
    private OnSelectFinishedListener mOnSelectFinishedListener;

    public interface OnSelectFinishedListener {
        void onSelectFinished(List<Node> sel);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //builder.setTitle("所在地区");

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View root = inflater.inflate(R.layout.fragment_tree_select, null);
        builder.setView(root);

        AlertDialog dialog = builder.create();
        mTabLayout = (TabLayout) root.findViewById(R.id.tabs);
        mTabLayout.addOnTabSelectedListener(this);
        mListView = (ListView) root.findViewById(R.id.list);
        mListView.setOnItemClickListener(this);

        if (mDataProvider != null) {
            expandNode(mDataProvider);
        }

        return dialog;
    }

    public void setOnSelectFinishedListener(OnSelectFinishedListener listener) {
        mOnSelectFinishedListener = listener;
    }

    public void expandNode(Node node) {
        if (mCurNode == node) {
            return;
        }

        mCurNode = node;

        int depth = node.getDepth();
        mSelectByManual = true;     // 这里很重要，锁住onTabSelected触发
        updateTabs();
        mTabLayout.getTabAt(depth).select();
        mSelectByManual = false;

        node.getChildren().observe(this, new Observer<List<Node>>() {
            @Override
            public void onChanged(@Nullable List<Node> nodes) {
                if (nodes != null && nodes.size() > 0) {
                    mAdapter = new ArrayAdapter<Node>(getContext(),
                            android.R.layout.simple_list_item_1, nodes);
                    mListView.setAdapter(mAdapter);
                } else {
                    if (mOnSelectFinishedListener != null) {
                        mOnSelectFinishedListener.onSelectFinished(mSelects);
                    }
                    dismiss();
                }
            }
        });
    }

    private void updateTabs() {
        int deta = mSelects.size() + 1 - mTabLayout.getTabCount();
        if (deta > 0) {
            for (int i = 0; i < deta; i++) {
                mTabLayout.addTab(mTabLayout.newTab());
            }
        } else {
            for (int i = 0; i < -deta; i++) {
                mTabLayout.removeTabAt(i);
            }
        }

        int index = 0;
        for (Node node : mSelects) {
            mTabLayout.getTabAt(index).setText(node.getDisplayString());
            index++;
        }

        int count = mTabLayout.getTabCount();
        mTabLayout.getTabAt(count - 1).setText(R.string.select);
    }

    public void setDataProvider(Node provider) {
        mDataProvider = provider;
    }

    public void refreshView() {
        expandNode(mDataProvider);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ArrayAdapter<Node> adapter = (ArrayAdapter<Node>) parent.getAdapter();

        Node node = (Node) mAdapter.getItem(position);
        int depth = mCurNode.getDepth();
        if (depth < mSelects.size()) {
            mSelects.set(depth, node);
        } else {
            mSelects.add(node);
        }

        Node item = adapter.getItem(position);
        expandNode(item);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (mSelectByManual) {
            return;
        }

        int position = tab.getPosition();
        mSelects = mSelects.subList(0, position);

        if (position == 0) {
            expandNode(mDataProvider);
        } else {
            Node node = mSelects.get(position - 1);
            if (node != null) {
                expandNode(node);
            }
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    public interface Node {
        String getDisplayString();
        LiveData<List<Node>> getChildren();
        int getDepth();
    }
}
