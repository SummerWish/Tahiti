package octoteam.tahiti.server.group.service;

import io.netty.channel.ChannelId;
import octoteam.tahiti.server.shared.microservice.rmi.IGroupServiceProvider;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class DefaultGroupService extends UnicastRemoteObject implements IGroupServiceProvider {

    private HashMap<String, HashSet<ChannelId>> groups = new HashMap<>();
    private HashMap<ChannelId, HashSet<String>> joinedGroups = new HashMap<>();

    public DefaultGroupService() throws RemoteException {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void joinGroup(ChannelId channelId, String group) throws RemoteException {
        if (groups.containsKey(group)) {
            groups.get(group).add(channelId);
        } else {
            HashSet<ChannelId> channelsOfGroup = new HashSet<>();
            channelsOfGroup.add(channelId);
            groups.put(group, channelsOfGroup);
        }
        if (joinedGroups.containsKey(channelId)) {
            joinedGroups.get(channelId).add(group);
        } else {
            HashSet<String> groups = new HashSet<>();
            groups.add(group);
            joinedGroups.put(channelId, groups);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void leaveGroup(ChannelId channelId, String group) throws RemoteException {
        if (groups.containsKey(group)) {
            groups.get(group).remove(channelId);
            if (groups.get(group).isEmpty()) {
                groups.remove(group);
            }
        }
        if (joinedGroups.containsKey(channelId)) {
            joinedGroups.get(channelId).remove(group);
            if (joinedGroups.get(channelId).isEmpty()) {
                joinedGroups.remove(channelId);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ChannelId> getChannelsInGroup(String group) throws RemoteException {
        if (groups.containsKey(group)) {
            return new ArrayList<>(groups.get(group));
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getJoinedGroups(ChannelId channelId) throws RemoteException {
        if (joinedGroups.containsKey(channelId)) {
            return new ArrayList<>(joinedGroups.get(channelId));
        } else {
            return new ArrayList<>();
        }
    }

}
