package octoteam.tahiti.server.shared.microservice.rmi;

import octoteam.tahiti.server.shared.model.Chat;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IGroupServiceProvider extends Remote {

    /**
     * 加入群组
     *
     * @param channelId Channel ID
     * @param group     组名
     * @throws RemoteException
     */
    public void joinGroup(String channelId, String group) throws RemoteException;

    /**
     * 离开群组
     *
     * @param channelId Channel ID
     * @param group     组名
     * @throws RemoteException
     */
    public void leaveGroup(String channelId, String group) throws RemoteException;

    /**
     * 查询发布消息所影响的 Channel 列表
     *
     * @param chat 消息
     * @return 受影响的 Channel ID 列表
     * @throws RemoteException
     */
    public List<String> getDirtyChannels(Chat chat) throws RemoteException;

}
