package octoteam.tahiti.server.repository;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import octoteam.tahiti.server.model.Message;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseMessageRepository extends DatabaseRepository<Message, Integer> implements MessageRepository {

    /**
     * 基于数据库的消息仓库
     *
     * @param connectionSource 数据库连接
     * @throws Exception
     */
    public DatabaseMessageRepository(ConnectionSource connectionSource) throws Exception {
        super(connectionSource, Message.class);
    }

    /**
     * 添加消息
     *
     * @param msg
     * @return
     */
    public Message addMessage(Message msg) {
        try {
            getDAO().create(msg);
            return msg;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Message> getGroupMessagesSince(String groupId, long since) {
        QueryBuilder<Message, Integer> qb = getDAO().queryBuilder();
        try {
            qb.where()
                    .eq("groupId", groupId)
                    .gt("sendAt", since);
            PreparedQuery<Message> preparedQuery = qb.prepare();
            return getDAO().query(preparedQuery);
        } catch (SQLException ignored) {
            return new ArrayList<>();
        }
    }
}
