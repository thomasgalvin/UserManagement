package galvin.users;

import com.galvin.db.PersistenceException;
import com.galvin.db.UuidFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultLoginManager implements LoginManager
{
    private static final Logger logger = LoggerFactory.getLogger( DefaultLoginManager.class );
    private static final long TOKEN_LIFESPAN = 1000 * 60 * 60 * 24 * 5; //five days in miliseconds
    private final UserDataStore dataStore;
    private HashMap<String, LoginToken> tokens = new HashMap();
    
    public DefaultLoginManager( UserDataStore dataStore ) {
        this.dataStore = dataStore;
    }
    
    /////////////
    /// login ///
    /////////////
    
    @Override
    public LoginToken login( String credential ) throws PersistenceException, LoginException{
        if( StringUtils.isBlank( credential) ){
            throw new LoginException();
        }
        
        User user = dataStore.retrieveByCredential( credential );
        if( user != null && user.isEnabled() ){
            return storeLoginToken( user );
        }
        
        throw new LoginException();
    }
    
    @Override
    public LoginToken login( String loginName, String password ) throws PersistenceException, LoginException{
        if( StringUtils.isBlank( loginName) || StringUtils.isBlank( password ) ){
            throw new LoginException();
        }
        
        User user = dataStore.retrieveByLoginName( loginName );
        if( user != null && user.isEnabled() ){
            if( !BCrypt.checkpw( password, user.getPasswordHash() ) ) {
                throw new LoginException();
            }
            
            return storeLoginToken( user );
        }
        
        throw new LoginException();
    }
    
    private LoginToken storeLoginToken( User user )throws PersistenceException { 
        LoginToken token = new LoginToken();
        token.setUuid( UuidFactory.generateUuid() );
        token.setExpires( new Date().getTime() + TOKEN_LIFESPAN );
        
        User clone = dataStore.retrieve( user.getUuid() );
        clone.setPasswordHash( null );
        token.setUser( user );
        
        purgeExpired();
        tokens.put( token.getUuid(), token );
        
        return token.clone();
    }
    
    ////////////////////////////
    /// token authentication ///
    ////////////////////////////
    
    @Override
    public LoginToken validate( String token ) throws PersistenceException, LoginException {
        LoginToken loginToken = tokens.get( token );
        if( loginToken != null ){
            if( isValid( loginToken ) )
            {
                loginToken.setExpires( new Date().getTime() + TOKEN_LIFESPAN );
                return loginToken.clone();
            }
        }

        throw new LoginException();
    }
    
    @Override
    public void demandPermission( String token, String permission ) throws PersistenceException, LoginException, PermissionsException {
        LoginToken loginToken = validate( token );
        if( !dataStore.hasPermission( loginToken.getUser().getUuid(), permission ) ){
            throw new PermissionsException( permission );
        }
    }
    
    private boolean isValid( LoginToken token ) throws PersistenceException {
        if( token != null ) {
            if( token.getExpires() >= new Date().getTime() ) {
                String uuid = token.getUser().getUuid();
                User user = dataStore.retrieve( uuid );
                if( user != null ) {
                    return user.isEnabled();
                }
            }
        }
        return false;
    }
    
    private synchronized void purgeExpired() throws PersistenceException {
        List<String> purgeTokens = new ArrayList();

        Set<Map.Entry<String, LoginToken>> entries = tokens.entrySet();
        Iterator<Map.Entry<String, LoginToken>> iter = entries.iterator();
        while( iter.hasNext() ){
            Map.Entry<String, LoginToken> entry = iter.next();
            LoginToken token = entry.getValue();

            if( !isValid( token ) ){
                purgeTokens.add( token.getUuid() );
            }
        }

        for( String token : purgeTokens ){
            tokens.remove( token );
        }
    }
    
    public void logout( String token ) throws PersistenceException{
        if( token != null ){
            tokens.remove( token );
        }
    }
    
    @Override
    public synchronized String encrypt( String password ) {
        String hashed = BCrypt.hashpw( password, BCrypt.gensalt() );
        return hashed;
    }
}
