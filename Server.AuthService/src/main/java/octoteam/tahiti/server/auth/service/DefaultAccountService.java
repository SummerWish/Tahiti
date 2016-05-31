package octoteam.tahiti.server.auth.service;

import octoteam.tahiti.server.auth.repository.AccountRepository;
import octoteam.tahiti.server.shared.microservice.rmi.AccountNotFoundException;
import octoteam.tahiti.server.shared.microservice.rmi.AccountNotMatchException;
import octoteam.tahiti.server.shared.microservice.rmi.IAuthServiceProvider;
import octoteam.tahiti.server.shared.model.Account;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class DefaultAccountService extends UnicastRemoteObject implements IAuthServiceProvider {

    private AccountRepository accountRepository;

    /**
     * 构造方法，获得一个用户集合
     *
     * @param accountRepository 用户集合
     */
    public DefaultAccountService(AccountRepository accountRepository) throws RemoteException {
        super();
        this.accountRepository = accountRepository;
    }

    @Override
    public Account getMatchedAccount(String username, String password)
            throws AccountNotFoundException, AccountNotMatchException {
        Account account = accountRepository.lookupAccountByUsername(username);
        if (account == null) throw new AccountNotFoundException();
        if (!account.isPasswordMatches(password)) throw new AccountNotMatchException();
        return account;
    }

}
