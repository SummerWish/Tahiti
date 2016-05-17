package octoteam.tahiti.client.ui;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 该模块实现了响应式状态存储。
 * 其他模块可以从 Store 中订阅状态项，从而在状态项发生改变的时候获得通知。
 */
public class Store {

    private HashMap<String, Object> store = new HashMap<>();
    private HashMap<String, List<Consumer<Object>>> observations = new HashMap<>();

    private Boolean batchOp = false;
    private List<Pair<Consumer<Object>, Object>> changedObservations = new LinkedList<>();

    /**
     * 启动更新过程, 在执行完毕后自动结束更新过程
     *
     * @param r 需要执行的内容
     */
    public void update(Runnable r) {
        beginUpdate();
        r.run();
        endUpdate();
    }

    /**
     * 启动更新过程
     */
    public void beginUpdate() {
        if (batchOp) {
            return;
        }
        batchOp = true;
        changedObservations.clear();
    }

    /**
     * 存储初始状态, 不会触发更新通知
     *
     * @param key   状态名
     * @param value 初始值
     */
    public void init(String key, Object value) {
        store.put(key, value);
    }

    /**
     * 更新状态, 会触发更新通知
     *
     * @param key   状态名
     * @param value 新的值
     */
    public void put(String key, Object value) {
        if (!batchOp) {
            throw new RuntimeException("beginUpdate not called");
        }
        store.put(key, value);
        if (observations.containsKey(key)) {
            observations.get(key).forEach(r -> changedObservations.add(new ImmutablePair<>(r, value)));
        }
    }

    /**
     * 结束更新过程, 并触发更新通知
     */
    public void endUpdate() {
        if (!batchOp) {
            return;
        }
        List<Pair<Consumer<Object>, Object>> copy = new LinkedList<>(changedObservations);
        changedObservations.clear();

        copy.forEach(pair -> pair.getLeft().accept(pair.getRight()));
        batchOp = false;
    }

    /**
     * 获得状态的值
     *
     * @param key 状态名
     * @return 值
     */
    public Object get(String key) {
        return store.get(key);
    }

    /**
     * 为状态添加监听器
     *
     * @param key 状态名
     * @param r   在状态被改变时所被执行的内容
     */
    public void observe(String key, Consumer<Object> r) {
        if (!observations.containsKey(key)) {
            observations.put(key, new LinkedList<>());
        }
        observations.get(key).add(r);
    }

}
