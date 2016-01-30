package galvin.users;

import com.galvin.db.PersistenceException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class SqliteAccountRequestDataStore implements AccountRequestDataStore {
    private final UserDataStore dataStore;
    private final LoginManager loginManager;

    public SqliteAccountRequestDataStore( File database,
                                          RoleProvider roleProvider,
                                          LoginManager loginManager ) throws PersistenceException {
        dataStore = new SqliteUserDataStore( database, roleProvider ) {
            @Override
            public User createEmptyUser() {
                return new AccountRequest();
            }
        };
        this.loginManager = loginManager;
    }

    @Override
    public String store( AccountRequest accountRequest ) throws PersistenceException, PasswordMismatchException {
        String uuid = accountRequest.getUuid();
        String password = accountRequest.getPassword();
        String confirm = accountRequest.getConfirmPassword();
        accountRequest.setPassword( null );
        accountRequest.setConfirmPassword( null );
        
        if( exists( uuid ) ){
            //this is an update; we can delegate loading the existing 
            //password hash to the user data store
            return dataStore.store( accountRequest );
        } 

        String hash = accountRequest.getPasswordHash();
        if( StringUtils.isBlank( hash ) ) {
            //this is a create, not an update. If no password hash is present,
            //make sure the passwords match, then generate the hash
            
            if( StringUtils.isBlank( password ) || StringUtils.isBlank( confirm ) ) {
                throw new PasswordMismatchException();
            }
            
            if( !password.equals( confirm ) ) {
                throw new PasswordMismatchException();
            }
            
            hash = loginManager.encrypt( password );
            accountRequest.setPasswordHash( hash );
        }

        return dataStore.store( accountRequest );
    }

    @Override
    public boolean exists( String uuid ) throws PersistenceException {
        return dataStore.exists( uuid );
    }

    @Override
    public boolean existsByCredential( String credential ) throws PersistenceException {
        return dataStore.existsByCredential( credential );
    }

    @Override
    public boolean existsByLoginName( String loginName ) throws PersistenceException {
        return dataStore.existsByLoginName( loginName );
    }

    @Override
    public AccountRequest retrieve( String uuid ) throws PersistenceException {
        return (AccountRequest)dataStore.retrieve( uuid );
    }

    @Override
    public AccountRequest retrieveByCredential( String credential ) throws PersistenceException {
        return (AccountRequest)dataStore.retrieveByCredential( credential );
    }

    @Override
    public AccountRequest retrieveByLoginName( String loginName ) throws PersistenceException {
        return (AccountRequest)dataStore.retrieveByLoginName( loginName );
    }

    @Override
    public List<AccountRequest> retrieveAll() throws PersistenceException {
        List<AccountRequest> result = new ArrayList();
        List users = dataStore.retrieveAll();
        result.addAll( users );
        return result;
    }

    @Override
    public boolean delete( String uuid ) throws PersistenceException {
        return dataStore.delete( uuid );
    }

    @Override
    public boolean hasPermission( String uuid, String permission ) throws PersistenceException {
        return dataStore.hasPermission( uuid, permission );
    }

    @Override
    public List<Role> getRoles( String uuid ) throws PersistenceException {
        return dataStore.getRoles( uuid );
    }

}
