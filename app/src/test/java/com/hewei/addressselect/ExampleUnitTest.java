package com.hewei.addressselect;


import com.google.gson.stream.JsonReader;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testJson() throws IOException {
        InputStream in = new FileInputStream("E:\\workspace\\android_demo\\AddressSelect\\app\\src\\main\\assets\\address.json");
        JsonReader reader = new JsonReader(new InputStreamReader(in));
        loadFromJSONArray(reader);
    }

    private static void loadFromJSONArray(JsonReader reader) throws IOException {
        String value = null;
        String label = null;

        reader.beginArray();
        while(reader.hasNext()) {
            reader.beginObject();

            while (reader.hasNext()) {
                String name = reader.nextName();
                if ("label".equals(name)) {
                    label = reader.nextString();
                } else if ("value".equals(name)) {
                    value = reader.nextString();
                } else if ("children".equals(name)) {
                    //long[] ids = dao.addNode(nodeCache.getAll());
                    //nodeCache.reset();
                    loadFromJSONArray(reader);
                    //reader.skipValue();
                } else {
                    reader.skipValue();
                }
            }

            System.out.println(label);
            reader.endObject();
        }
        reader.endArray();
    }
}