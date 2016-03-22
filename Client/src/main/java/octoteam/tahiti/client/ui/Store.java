package octoteam.tahiti.client.ui;

import com.google.common.base.Function;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Store {

    private HashMap<String, Object> store = new HashMap<>();
    private HashMap<String, List<Function<Object, Void>>> observations = new HashMap<>();

    private Boolean batchOp = false;
    private List<Pair<Function<Object, Void>, Object>> changedObservations = new LinkedList<>();

    public void update(Runnable r) {
        beginUpdate();
        r.run();
        endUpdate();
    }

    public void beginUpdate() {
        if (batchOp) {
            return;
        }
        batchOp = true;
        changedObservations.clear();
    }

    public void init(String key, Object value) {
        store.put(key, value);
    }

    public void put(String key, Object value) {
        if (!batchOp) {
            throw new RuntimeException("beginUpdate not called");
        }
        store.put(key, value);
        if (observations.containsKey(key)) {
            observations.get(key).forEach(r -> changedObservations.add(new ImmutablePair<>(r, value)));
        }
    }

    public void endUpdate() {
        if (!batchOp) {
            return;
        }
        List<Pair<Function<Object, Void>, Object>> copy = new LinkedList<>(changedObservations);
        changedObservations.clear();

        copy.forEach(pair -> pair.getLeft().apply(pair.getRight()));
        batchOp = false;
    }

    public Object get(String key) {
        return store.get(key);
    }

    public void observe(String key, Function<Object, Void> r) {
        if (!observations.containsKey(key)) {
            observations.put(key, new LinkedList<>());
        }
        observations.get(key).add(r);
    }

}
