package octoteam.tahiti.server.shared.microservice.rmi;

import octoteam.tahiti.server.shared.model.Chat;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IStorageServiceProvider extends Remote {

    /**
     * 查询某个组下一个时间点以来所有的消息
     *
     * @param since   起始时间点(排除)
     * @param groupId 组名
     * @return 消息列表
     * @throws RemoteException
     */
    List<Chat> getGroupChatsSince(String groupId, long since) throws RemoteException;

    /**
     * 记录一个消息
     *
     * @param UID      用户 ID
     * @param username 用户名
     * @param groupId  组名
     * @param content  内容
     * @return 消息对象
     * @throws RemoteException
     */
    Chat addChat(int UID, String username, String groupId, String content) throws RemoteException;

}
