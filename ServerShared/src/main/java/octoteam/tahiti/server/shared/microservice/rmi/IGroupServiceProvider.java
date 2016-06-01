package octoteam.tahiti.server.shared.microservice.rmi;

import io.netty.channel.ChannelId;

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
    public void joinGroup(ChannelId channelId, String group) throws RemoteException;

    /**
     * 离开群组
     *
     * @param channelId Channel ID
     * @param group     组名
     * @throws RemoteException
     */
    public void leaveGroup(ChannelId channelId, String group) throws RemoteException;

    /**
     * 查询群组中所有的 Channel ID
     *
     * @param group 组名
     * @return Channel ID 列表
     * @throws RemoteException
     */
    public List<ChannelId> getChannelsInGroup(String group) throws RemoteException;

    /**
     * 查询一个 Channel 所加入的群组
     *
     * @param channelId Channel ID
     * @return 群组列表
     * @throws RemoteException
     */
    public List<String> getJoinedGroups(ChannelId channelId) throws RemoteException;

}
