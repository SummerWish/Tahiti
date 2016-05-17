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

    /**
     * 填写的组名
     */
    private String group;

    public UIOnLoginCommandEvent(String username, String password, String group) {
        this.username = username;
        this.password = password;
        this.group = group;
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

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("username", username)
                .add("password", password)
                .add("group", group)
                .toString();
    }
}
