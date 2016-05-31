package octoteam.tahiti.server.auth.repository;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import octoteam.tahiti.server.shared.DatabaseRepository;
import octoteam.tahiti.server.shared.model.Account;

import java.sql.SQLException;

/**
 * 用于操作数据库中的用户表
 */
public class DatabaseAccountRepository extends DatabaseRepository<Account, Integer> implements AccountRepository {

    /**
     * 基于数据库的用户仓库
     *
     * @param connectionSource 数据库连接
     * @throws Exception
     */
    public DatabaseAccountRepository(ConnectionSource connectionSource) throws Exception {
        super(connectionSource, Account.class);
    }

    @Override
    public Account lookupAccountByUsername(String username) {
        try {
            QueryBuilder<Account, Integer> statementBuilder = getDAO().queryBuilder();
            statementBuilder.where().eq("username", username);
            Account account = getDAO().queryForFirst(statementBuilder.prepare());
            return account;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Account createAccount(Account account) {
        try {
            getDAO().create(account);
            return account;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
