package octoteam.tahiti.server.shared.model;

import com.google.common.base.MoreObjects;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@DatabaseTable(tableName = "accounts")
public class Account implements Serializable {

    /**
     * 用户唯一 id
     */
    @DatabaseField(generatedId = true)
    private int id;

    /**
     * 用户名
     */
    @DatabaseField(canBeNull = false)
    private String username;

    /**
     * 用户密码哈希值
     */
    @DatabaseField(canBeNull = false)
    private String hash;

    /**
     * 生成用户密码哈希值的字符串
     */
    @DatabaseField(canBeNull = false)
    private String salt;

    public Account() {

    }

    public Account(String username, String password) {
        this.username = username;
        this.salt = RandomStringUtils.randomAlphanumeric(32);
        this.hash = hashPassword(password, salt);
    }

    public void assignId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    /**
     * 匹配用户密码
     *
     * @param password 用户明文密码
     * @return 匹配则返回 true，否则返回 false
     */
    public boolean isPasswordMatches(String password) {
        return hashPassword(password, salt).equals(hash);
    }

    /**
     * 根据用户明文密码生成哈希值
     *
     * @param password 用户明文密码
     * @param salt     字符串
     * @return 用户密码哈希值
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update((password + salt).getBytes("UTF-8"));
            byte[] digest = md.digest();
            return String.format("%064x", new java.math.BigInteger(1, digest));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("username", username)
                .add("hash", hash)
                .add("salt", salt)
                .toString();
    }
}
