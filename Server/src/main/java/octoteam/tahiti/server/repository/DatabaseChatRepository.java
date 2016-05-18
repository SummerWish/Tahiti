package octoteam.tahiti.server.repository;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import octoteam.tahiti.server.model.Chat;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseChatRepository extends DatabaseRepository<Chat, Integer> implements ChatRepository {

    /**
     * 基于数据库的消息仓库
     *
     * @param connectionSource 数据库连接
     * @throws Exception
     */
    public DatabaseChatRepository(ConnectionSource connectionSource) throws Exception {
        super(connectionSource, Chat.class);
    }

    /**
     * 添加消息
     *
     * @param chatmsg
     * @return
     */
    public Chat addChat(Chat chatmsg) {
        try {
            getDAO().create(chatmsg);
            return chatmsg;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Chat> getGroupChatsSince(String groupId, long since) {
        QueryBuilder<Chat, Integer> qb = getDAO().queryBuilder();
        try {
            qb.where()
                    .eq("groupId", groupId)
                    .gt("sendAt", since);
            PreparedQuery<Chat> preparedQuery = qb.prepare();
            return getDAO().query(preparedQuery);
        } catch (SQLException ignored) {
            return new ArrayList<>();
        }
    }
}
