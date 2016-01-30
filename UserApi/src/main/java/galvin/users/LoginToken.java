package galvin.users;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Data
public class LoginToken implements Cloneable
{
    private static final Logger logger = LoggerFactory.getLogger( LoginToken.class );
    private String uuid;
    private long expires;
    private User user;

    @Override
    protected LoginToken clone() {
        try {
            LoginToken result = (LoginToken)super.clone();
            result.uuid = uuid;
            result.expires = expires;
            result.user = user;
            return result;
        }
        catch( CloneNotSupportedException ex ){
            logger.error( "Clone not supported", ex );
            return null;
        }
    }
    
}
