package octoteam.tahiti.client;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;

public class NaiveDb extends HashMap<String, Object> {

    private String filename;

    public NaiveDb(String filename) {
        this.filename = filename;
        try {
            File f = new File(filename);
            FileInputStream fin = new FileInputStream(f);
            byte[] data = new byte[(int) f.length()];
            fin.read(data);
            fin.close();
            JSON.parseObject(data, this.getClass());
        } catch (Exception ignored) {
        }
    }

    @Override
    public Object put(String key, Object value) {
        Object ret = super.put(key, value);
        save();
        return ret;
    }

    public void save() {
        try {
            String json = JSON.toJSONString(this);
            File f = new File(filename);
            FileOutputStream fout = new FileOutputStream(f);
            fout.write(json.getBytes());
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
