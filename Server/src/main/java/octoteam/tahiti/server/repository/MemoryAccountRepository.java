package octoteam.tahiti.server.repository;

import octoteam.tahiti.server.model.Account;

import java.util.HashMap;

/**
 * 用于操作内存内的用户表，用于测试，不建议在实际环境中使用
 */
public class MemoryAccountRepository implements AccountRepository {

    private HashMap<String, Account> accounts = new HashMap<>();

    @Override
    public Account lookupAccountByUsername(String username) {
        return accounts.get(username);
    }

    @Override
    public Account createAccount(Account account) {
        account.assignId(accounts.size() + 1);
        accounts.put(account.getUsername(), account);
        return account;
    }

}
