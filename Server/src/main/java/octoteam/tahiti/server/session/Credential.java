package octoteam.tahiti.server.session;

import com.google.common.base.MoreObjects;
import octoteam.tahiti.server.model.Account;

/**
 * 存放于 Session 的用户凭证
 */
public class Credential {

    /**
     * 用户唯一识别号
     */
    private int UID;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户
     */
    private Account account;

    /**
     * 验证状态
     */
    private boolean authenticated;

    /**
     * 根据传入参数实例化一个用户凭证
     *
     * @param UID           用户唯一识别号（int）
     * @param username      用户名 （String）
     * @param authenticated 验证状态（boolean）
     */
    public Credential(int UID, String username, boolean authenticated) {
        this.UID = UID;
        this.username = username;
        this.account = null;
        this.authenticated = authenticated;
    }

    /**
     * 由 Account Model 构造一个标记为已登录的认证信息
     *
     * @param account     用户
     * @param groupNumber 用户分组
     */
    public Credential(Account account) {
        this.UID = account.getId();
        this.username = account.getUsername();
        this.account = account;
        this.authenticated = true;
    }

    public int getUID() {
        return UID;
    }

    public String getUsername() {
        return username;
    }

    public Account getAccount() {
        return account;
    }

    /**
     * 判断此凭证是否已被验证
     *
     * @return 已验证返回 true，否则返回 false。
     */
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("UID", UID)
                .add("username", username)
                .add("account", account)
                .add("authenticated", authenticated)
                .toString();
    }

    /**
     * 获得一个表示游客的认证信息
     *
     * @return 游客认证信息
     */
    public static Credential getGuestCredential() {
        return new Credential(0, "Guest", false);
    }

}
