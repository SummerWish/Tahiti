package octoteam.tahiti.shared.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.HashMap;
import java.util.HashSet;

public class ExtendedContext {

    private ChannelGroup connectedChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public ChannelId addToManagedChannels(Channel ch) {
        connectedChannels.add(ch);
        return ch.id();
    }

    public Channel lookupManagedChannels(ChannelId id) {
        return connectedChannels.find(id);
    }

    private final static AttributeKey<HashSet<String>> ATTR_KEY_IN_GROUPS = AttributeKey.valueOf("__in_groups");

    /**
     * @deprecated
     */
    private HashMap<String, ChannelGroup> groups = new HashMap<>();

    /**
     * @deprecated
     */
    private final ChannelFutureListener remover = future -> {
        Channel channel = future.channel();
        HashSet<String> g = channel.attr(ATTR_KEY_IN_GROUPS).get();
        if (g != null) {
            for (String group : g) {
                leave(channel, group);
            }
        }
    };

    /**
     * @param channel
     * @param group
     * @deprecated
     */
    public void join(Channel channel, String group) {
        // TODO: lock groups
        if (!groups.containsKey(group)) {
            groups.put(group, new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));
        }
        groups.get(group).add(channel);
        HashSet<String> g = channel.attr(ATTR_KEY_IN_GROUPS).get();
        if (g == null) {
            g = new HashSet<>();
            channel.attr(ATTR_KEY_IN_GROUPS).set(g);
        }
        g.add(group);
        channel.closeFuture().addListener(remover);
    }

    /**
     * @param channel
     * @param group
     * @deprecated
     */
    public void leave(Channel channel, String group) {
        // TODO: lock groups
        if (!groups.containsKey(group)) {
            return;
        }
        HashSet<String> g = channel.attr(ATTR_KEY_IN_GROUPS).get();
        if (g != null) {
            g.remove(group);
        }
        ChannelGroup cg = groups.get(group);
        cg.remove(channel);
        if (cg.size() == 0) {
            groups.remove(group);
        }
        channel.closeFuture().removeListener(remover);
    }

    /**
     * @param channel
     * @return
     * @deprecated
     */
    public String[] getJoinedGroups(Channel channel) {
        HashSet<String> g = channel.attr(ATTR_KEY_IN_GROUPS).get();
        if (g == null) {
            return new String[0];
        } else {
            return g.toArray(new String[g.size()]);
        }
    }

    /**
     * @param group
     * @return
     * @deprecated
     */
    public ChannelGroup of(String group) {
        if (groups.containsKey(group)) {
            return groups.get(group);
        } else {
            return new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        }
    }

}
