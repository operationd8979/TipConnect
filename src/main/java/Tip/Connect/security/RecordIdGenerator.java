package Tip.Connect.security;

import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Date;

public class RecordIdGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object){

        int fixedLength = 20;
        String prefix = "RECORD";
        long id= new Date().getTime();
        String baseUserId = prefix + Long.toString(id);
        if(baseUserId.length()<fixedLength){
            while (baseUserId.length()<fixedLength){
                baseUserId+="X";
            }
        }
        String result = Base64.getEncoder().encodeToString(baseUserId.getBytes());
        return result;
    }
}