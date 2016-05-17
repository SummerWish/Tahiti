package octoteam.tahiti.client;

import octoteam.tahiti.protocol.SocketMessageProtos.Message;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * 基于有序序列的回调仓库
 */
public class CallbackRepository {

    private static AtomicLong msgSequence = new AtomicLong();

    private ConcurrentHashMap<Long, Consumer<Message>> callbacks = new ConcurrentHashMap<>();

    /**
     * 获得下一个序列值
     *
     * @return 新序列值
     */
    public long getNextSequence() {
        return msgSequence.incrementAndGet();
    }

    /**
     * 获得下一个序列值, 并存储回调
     *
     * @param r 回调
     * @return 新序列值
     */
    public long getNextSequence(Consumer<Message> r) {
        long seq = getNextSequence();
        if (r != null) callbacks.put(seq, r);
        return seq;
    }

    /**
     * 根据序列值调用回调
     *
     * @param seqId 序列值
     * @param msg   消息内容
     */
    public void resolveCallback(long seqId, Message msg) {
        if (callbacks.containsKey(seqId)) {
            Consumer<Message> r = callbacks.get(seqId);
            callbacks.remove(seqId);
            r.accept(msg);
        }
    }

}
