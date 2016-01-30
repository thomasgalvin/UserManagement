package galvin.users;

import com.galvin.db.PersistenceException;
import java.util.List;

public class NoOpUserDataStore implements UserDataStore
{
    @Override
    public String store( User user ) throws PersistenceException {
        return null;
    }
    
    @Override
    public boolean exists( String uuid ) throws PersistenceException{
        return false;
    }
    
    @Override
    public boolean existsByCredential( String credential ) throws PersistenceException{
        return false;
    }
    
    @Override
    public boolean existsByLoginName( String loginName ) throws PersistenceException{
        return false;
    }
    
    @Override
    public User retrieve( String uuid ) throws PersistenceException {
        return null;
    }
    
    @Override
    public User retrieveByCredential( String credential ) throws PersistenceException{
        return null;
    }
    
    @Override
    public User retrieveByLoginName( String loginName ) throws PersistenceException{
        return null;
    }
    
    @Override
    public List<User> retrieveAll() throws PersistenceException{
        return null;
    }
    
    @Override
    public boolean delete( String uuid ) throws PersistenceException{
        return false;
    }
    
    @Override
    public boolean hasPermission( String uuid, String permission ) throws PersistenceException{
        return false;
    }
    
    @Override
    public List<Role> getRoles( String uuid ) throws PersistenceException {
        return null;
    }
}
