package com.hewei.addressselect;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.stream.JsonReader;
import com.hewei.addressselect.model.AddressDAO;
import com.hewei.addressselect.model.AddressNode;
import com.hewei.addressselect.model.AppDataBase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengyinpeng on 2018/9/3.
 */
public class AddressLoader {
    private static final String TAG = "AddressLoader";

    public static void initAddressDb(final Context context) {
        Log.d(TAG, "load begin!");
        if (isInited()) {
            Log.d(TAG, "load not need!");
            return;
        }

        new AsyncTask<String, Integer, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                AddressDAO dao = AppDataBase.getInstance().getAddressDAO();
                //AddressDAO dao = new TestDAO();
                InputStream in = null;
                try {
                    AssetManager manager = context.getAssets();
                    in = manager.open("address.json");
                    NodeCache nodeCache = new NodeCache();
                    loadFromJSONArray(new JsonReader(new InputStreamReader(in)), dao, 0, nodeCache);
                    dao.addNode(nodeCache.getAll());
                    Log.d(TAG, "load finished!");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                return null;
            }
        }.execute();
    }

    private static final class NodeCache {
        final int CACHE_SIZE = 100;
        private int current = 0;

        private ArrayList<AddressNode> mData = new ArrayList<>(CACHE_SIZE);

        public AddressNode get() {
            AddressNode node;
            if (current >= 0 && current < mData.size()) {
                node = mData.get(current);
            } else {
                node = new AddressNode();
                mData.add(node);
            }

            current++;
            return node;
        }

        public List<AddressNode> getAll() {
            return mData.subList(0, current);
        }

        public boolean isFull() {
            return (current == CACHE_SIZE);
        }

        public void reset() {
            current = 0;
        }
    }

    private static void loadFromJSONArray(JsonReader reader, AddressDAO dao, long parentId,
                                          NodeCache nodeCache) throws IOException {
        reader.beginArray();
        while(reader.hasNext()) {
            reader.beginObject();

            AddressNode node = nodeCache.get();
            node.parentId = parentId;

            while (reader.hasNext()) {
                String name = reader.nextName();
                if ("label".equals(name)) {
                    node.label = reader.nextString();
                } else if ("value".equals(name)) {
                    node.value = reader.nextString();
                } else if ("children".equals(name)) {
                    long[] ids = dao.addNode(nodeCache.getAll());
                    nodeCache.reset();
                    loadFromJSONArray(reader, dao, ids[ids.length - 1], nodeCache);
                } else {
                    reader.skipValue();
                }
            }

            if (nodeCache.isFull()) {
                dao.addNode(nodeCache.getAll());
                nodeCache.reset();
            }
            reader.endObject();
        }
        reader.endArray();
    }

    private static class TestDAO implements AddressDAO {
        private int sId = 1;

        @Override
        public long[] addNode(List<AddressNode> nodes) {
            long[] ids = new long[nodes.size()];
            int index = 0;
            for (AddressNode node : nodes) {
                node.id = sId++;
                ids[index++] = node.id;
                Log.d(TAG, node.id + "," + node.value + "," + node.parentId + ", " + node.label);
            }

            return ids;
        }

        @Override
        public long[] addAll(AddressNode... nodes) {
            return new long[0];
        }

        @Override
        public long addNode(AddressNode node) {
            return 0;
        }

        @Override
        public LiveData<List<AddressNode>> queryNode(long parentId) {
            return null;
        }

        @Override
        public LiveData<AddressNode> queryNode2(long id) {
            return null;
        }
    }

    public static String loadFileFromAssets(Context mContext, String name) {
        AssetManager assetManager = mContext.getAssets();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            InputStream in = assetManager.open(name);
            int n;
            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }

            in.close();
            return out.toString("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static boolean isInited() {
        final String QUERY_STR = "select id from address_nodes limit 1;";
        boolean ret = false;
        Cursor cursor = AppDataBase.getInstance().getOpenHelper().getReadableDatabase().query(QUERY_STR);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                ret = true;
            }

            cursor.close();
            return ret;
        }

        return false;
    }
}
