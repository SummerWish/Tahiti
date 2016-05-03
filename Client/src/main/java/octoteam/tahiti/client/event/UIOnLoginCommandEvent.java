package octoteam.tahiti.client.event;

import com.google.common.base.MoreObjects;

/**
 * 用户点击登录按钮事件
 */
public class UIOnLoginCommandEvent extends UIEvent {

    /**
     * 填写的用户名
     */
    private String username;

    /**
     * 填写的密码
     */
    private String password;

    public UIOnLoginCommandEvent(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("username", username)
                .add("password", password)
                .toString();
    }

}
