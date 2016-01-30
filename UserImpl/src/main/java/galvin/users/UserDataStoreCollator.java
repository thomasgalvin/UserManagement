package galvin.users;

import com.galvin.db.PersistenceException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDataStoreCollator implements UserDataStore
{
    private static final Logger logger = LoggerFactory.getLogger( UserDataStoreCollator.class );
    private List<UserDataStore> dataStores = new ArrayList();
    
    public UserDataStoreCollator( UserDataStore ... dataStores ){
        if( dataStores != null ){
            for( UserDataStore dataStore : dataStores ){
                this.dataStores.add( dataStore );
            }
        }
    }

    @Override    
    public String store( User user ) throws PersistenceException {
        String result = null;
        
        for( UserDataStore dataStore : dataStores ){
            String current = dataStore.store( user );
            if( StringUtils.isEmpty( result ) ){
                result = current;
            }
        }
        
        return result;
    }
    
    @Override
    public boolean exists( String uuid ) throws PersistenceException{
        boolean result = false;
        
        for( UserDataStore dataStore : dataStores ){
            boolean current = dataStore.exists( uuid );
            result |= current;
        }
        
        return result;
    }
    
    @Override
    public User retrieve( String uuid ) throws PersistenceException {
        User result = null;
        
        for( UserDataStore dataStore : dataStores ){
            User current = dataStore.retrieve( uuid );
            if( result == null ){
                result = current;
            }
        }
        
        return result;
    }
    
    @Override
    public boolean existsByCredential( String credential ) throws PersistenceException {
        boolean result = false;
        
        for( UserDataStore dataStore : dataStores ){
            result |= dataStore.existsByLoginName( credential );
        }
        
        return result;
    }
    
    @Override
    public boolean existsByLoginName( String loginName ) throws PersistenceException {
        boolean result = false;
        
        for( UserDataStore dataStore : dataStores ){
            result |= dataStore.existsByLoginName( loginName );
        }
        
        return result;
    }
    
    @Override
    public User retrieveByCredential( String credential ) throws PersistenceException{
        User result = null;
        
        for( UserDataStore dataStore : dataStores ){
            User current = dataStore.retrieveByCredential( credential );
            if( result == null ){
                result = current;
            }
        }
        
        return result;
    }
    
    @Override
    public User retrieveByLoginName( String loginName ) throws PersistenceException{
        User result = null;
        
        for( UserDataStore dataStore : dataStores ){
            User current = dataStore.retrieveByLoginName( loginName );
            if( result == null ){
                result = current;
            }
        }
        
        return result;
    }
    
    @Override
    public List<User> retrieveAll() throws PersistenceException{
        List<User> result = new ArrayList();
        
        for( UserDataStore dataStore : dataStores ){
            List<User> current = dataStore.retrieveAll();
            if( current != null ){
                result.addAll( current );
            }
        }
        
        return result;
    }
    
    @Override
    public boolean delete( String uuid ) throws PersistenceException{
        boolean result = false;
        
        for( UserDataStore dataStore : dataStores ){
            boolean current = dataStore.exists( uuid );
            result |= current;
        }
        
        return result;
    }
    
    @Override
    public boolean hasPermission( String uuid, String permission ) throws PersistenceException{
        boolean result = false;
        
        for( UserDataStore dataStore : dataStores ){
            boolean current = dataStore.hasPermission( uuid, permission );
            result |= current;
        }
        
        return result;
    }
    
    @Override
    public List<Role> getRoles( String uuid ) throws PersistenceException {
        List<Role> result = new ArrayList();
        
        for( UserDataStore dataStore : dataStores ){
            List<Role> current = dataStore.getRoles( uuid );
            if( current != null ){
                result.addAll( current );
            }
        }
        
        return result;
    }
}
