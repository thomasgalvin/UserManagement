package galvin.users;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginCooldown
{
    private static final Logger logger = LoggerFactory.getLogger( LoginCooldown.class );
    private static final int MAX_UNHINDERED_ATTEMPTS = 10;
    private static final long ATTEMPTS_EXPIRE_AFTER = 1000 * 60 * 60; //one hour
    private final HashMap<String, LoginAttempt> attempts = new HashMap();
    
    public void recordSuccess( HttpServletRequest httpRequest ){
        String address = httpRequest.getRemoteAddr();
        if( isBlank( address ) ){
            return;
        }
        
        LoginAttempt attempt = getAttempts( address );
        attempt.count = 0;
    }
    
    public void recordAttempt( HttpServletRequest httpRequest ){
        String address = httpRequest.getRemoteAddr();
        if( isBlank( address ) ){
            return;
        }
        
        recordAttempt( address );
    }
    
    public void recordAttempt( String ipAddress ){
        if( !isBlank(ipAddress) ){
            LoginAttempt attempt = getAttempts( ipAddress );
            logAndSleep( attempt );
        }
    }
    
    private LoginAttempt getAttempts( String address ){
        LoginAttempt result = attempts.get( address );
        if( result == null ){
            result = new LoginAttempt();
            attempts.put( address, result );
        }
        
        long now = System.currentTimeMillis();
        long expires = now + ATTEMPTS_EXPIRE_AFTER;
        if( now >= expires ){
            result.count = 0;
        }
        
        result.timestamp = now;
        return result;
    }
    
    private void logAndSleep( LoginAttempt attempt ){
        if( attempt.count > MAX_UNHINDERED_ATTEMPTS ){
            int badAttempts = MAX_UNHINDERED_ATTEMPTS - attempt.count;
            
            try{
                switch( badAttempts ){
                    case 1:{
                        Thread.sleep( 1_000 );
                        break;
                    }
                    case 2:{
                        Thread.sleep( 2_000 );
                        break;
                    }
                    case 3:{
                        Thread.sleep( 5_000 );
                        break;
                    }
                    case 4:{
                        Thread.sleep( 10_000 );
                        break;
                    }
                    default:{
                        Thread.sleep( 20_000 );
                        break;
                    }
                }
            }
            catch( InterruptedException ex ){}
        }
        
        attempt.count++;
    }
    
    class LoginAttempt{
        public int count;
        public long timestamp = System.currentTimeMillis();
    }
}
