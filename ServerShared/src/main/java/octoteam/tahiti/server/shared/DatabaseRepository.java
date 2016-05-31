package octoteam.tahiti.server.shared;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public abstract class DatabaseRepository<T1, T2> {

    private Dao<T1, T2> DAO;

    public DatabaseRepository(ConnectionSource connectionSource, Class<T1> type) throws Exception {
        DAO = DaoManager.createDao(connectionSource, type);
        try {
            TableUtils.createTable(connectionSource, type);
        } catch (SQLException ignored) {
        }
    }

    public Dao<T1, T2> getDAO() {
        return DAO;
    }
}
