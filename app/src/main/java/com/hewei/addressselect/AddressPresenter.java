package com.hewei.addressselect;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.hewei.addressselect.model.AddressDAO;
import com.hewei.addressselect.model.AddressNode;
import com.hewei.addressselect.model.AppDataBase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by fengyinpeng on 2018/9/3.
 */
public class AddressPresenter {
    private static final String TAG = "AddressPresenter";

    public static void initAddressDb(final Context context) {
        AppDataBase.init(context);

        Log.d(TAG, "load begin!");
        if (isInited()) {
            Log.d(TAG, "load not need!");
            return;
        }

        new AsyncTask<String, Integer, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                AddressDAO dao = AppDataBase.getInstance().getAddressDAO();
                try {
                    JSONArray jsonArray = new JSONArray(loadFileFromAssets(context, "address.json"));
                    loadFromJSONArray(jsonArray, dao, 0);
                    Log.d(TAG, "load finished!");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
    }

    private static void loadFromJSONArray(JSONArray array, AddressDAO dao, long parentId) throws JSONException {
        String label;
        String value;
        JSONObject object;

        int LEN = array.length();
        for (int i = 0; i < LEN; i++) {
            object = array.getJSONObject(i);
            label = object.optString("label");
            value = object.optString("value");
            JSONArray children = object.optJSONArray("children");

            AddressNode node = new AddressNode();
            node.label = label;
            node.value = value;
            node.parentId = parentId;
            long id = dao.addNode(node);
            if (children != null) {
                loadFromJSONArray(children, dao, id);
            }
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
