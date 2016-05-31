package octoteam.tahiti.server.shared.microservice.rmi;

import octoteam.tahiti.server.shared.model.Account;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IAuthServiceProvider extends Remote {

    /**
     * 根据用户名及密码在用户集合内中查找用户
     *
     * @param username 用户名
     * @param password 密码
     * @return 返回查找到的用户
     * @throws AccountNotFoundException
     * @throws AccountNotMatchException
     */
    public Account getMatchedAccount(String username, String password)
            throws RemoteException, AccountNotFoundException, AccountNotMatchException;

}
