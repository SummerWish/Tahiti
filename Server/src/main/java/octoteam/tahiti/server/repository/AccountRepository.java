package octoteam.tahiti.server.repository;

import octoteam.tahiti.server.model.Account;

public interface AccountRepository {

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 成功查找到该用户则返回 Account，否则返回 null
     */
    public Account lookupAccountByUsername(String username);

    /**
     * 根据指定用户名及密码创建用户
     *
     * @param account 新用户
     * @return 创建成功则返回创建的 Account， 否则返回 null
     */
    public Account createAccount(Account account);

}
