package Tip.Connect.security;

import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;

public class UserIdGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object){

        int fixedLength = 20;
        String prefix = "TIP";
        JdbcConnectionAccess con = session.getJdbcConnectionAccess();

        try {
            JdbcConnectionAccess jdbcConnectionAccess = session.getJdbcConnectionAccess();
            Connection connection = jdbcConnectionAccess.obtainConnection();
            Statement statement = connection.createStatement();
            String query = "select count(id) as id from app_user";

            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                long id= resultSet.getLong(1);
                String baseUserId = prefix + Long.toString(id);
                if(baseUserId.length()<fixedLength){
                    while (baseUserId.length()<fixedLength){
                        baseUserId+="T";
                    }
                }
                String result = Base64.getEncoder().encodeToString(baseUserId.getBytes());
                return result;
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
