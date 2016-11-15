package galvin.users;

import com.galvin.db.PersistenceException;
import com.galvin.db.UuidFactory;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqliteUserDataStore implements UserDataStore
{
    private static final Logger logger = LoggerFactory.getLogger( SqliteUserDataStore.class );
    private final String connectionUrl;
    private final RoleProvider roleProvider;
    
    private static final String TABLE_CREATE_USER = 
        "create table if not exists users ("
      + "uuid text primary key,"
      + "name text,"
      + "sortByName text,"
      + "displayName text,"
      + "title text,"
      + "enabled integer,"
      + "credential text,"
      + "loginName text,"
      + "passwordHash text"
      + ");";
    
    private static final String CREATE_USER = 
        "insert into users"
      + "(       uuid, name, sortByName, displayName, title, enabled, credential, loginName, passwordHash )"
      + "values( ?,    ?,    ?,          ?,           ?,     ?,       ?,          ?,         ? );";
    
    private static final String UPDATE_PASSWORD = 
        "update users set passwordHash = ? where uuid = ?";
    
    private static final String RETRIEVE_USER = 
        "select uuid as uuid, "
      + "       name as name,"
      + "       sortByName as sortByName,"
      + "       displayName as displayName,"
      + "       title as title,"
      + "       enabled as enabled,"
      + "       credential as credential,"
      + "       loginName as loginName,"
      + "       passwordHash as passwordHash"
      + " from users where uuid = ?";
    
    private static final String RETRIEVE_USER_BY_CREDENTIAL = 
        "select uuid as uuid, "
      + "       name as name,"
      + "       sortByName as sortByName,"
      + "       displayName as displayName,"
      + "       title as title,"
      + "       enabled as enabled,"
      + "       credential as credential,"
      + "       loginName as loginName,"
      + "       passwordHash as passwordHash"
      + " from users where credential = ?";
    
    private static final String RETRIEVE_USER_BY_LOGIN_NAME = 
        "select uuid as uuid, "
      + "       name as name,"
      + "       sortByName as sortByName,"
      + "       displayName as displayName,"
      + "       title as title,"
      + "       enabled as enabled,"
      + "       credential as credential,"
      + "       loginName as loginName,"
      + "       passwordHash as passwordHash"
      + " from users where loginName = ?";
    
    private static final String RETRIEVE_USERS = 
        "select uuid as uuid, "
      + "       name as name,"
      + "       sortByName as sortByName,"
      + "       displayName as displayName,"
      + "       title as title,"
      + "       enabled as enabled,"
      + "       credential as credential,"
      + "       loginName as loginName,"
      + "       passwordHash as passwordHash"
      + " from users";
    
    private static final String USER_EXISTS = 
        "select uuid as uuid from users where uuid = ?;";
    
    private static final String USER_EXISTS_BY_LOGIN_NAME = 
        "select uuid as uuid from users where loginName = ?;";
    
    private static final String USER_EXISTS_BY_CREDENTIAL = 
        "select uuid as uuid from users where credential = ?;";
    
    private static final String DELETE_USER = 
        "delete from users where uuid = ?;";
    
    private static final String RETRIEVE_PASSWORD_HASH_FOR_USER =
        "select passwordHash as passwordHash from users where uuid = ?";
    
    
    
    private static final String TABLE_CREATE_CONTACT_INFO = 
        "create table if not exists contactInfo ("
      + "uuid text primary key,"
      + "userUuid text,"
      + "type text,"
      + "contact text,"
      + "description text,"
      + "position integer"
      + ");";
    
    private static final String CREATE_CONTACT_INFO =
        "insert into contactInfo"
      + "(       uuid, userUuid, type, contact, description, position )"
      + "values( ?,    ?,        ?,    ?,       ?,           ? );";
    
    private static final String RETRIEVE_CONTACT_INFO_FOR_USER = 
        "select uuid as uuid,"
      + "       userUuid as userUuid,"
      + "       type as type,"
      + "       contact as contact,"
      + "       description as description,"
      + "       position as position"
      + " from contactInfo"
      + " where userUuid = ?"
      + " order by position asc;";
    
    private static final String DELETE_CONTACT_INFO_FOR_USER = 
        "delete from contactInfo where userUuid = ?;";
    
    
    
    private static final String TABLE_CREATE_ROLES = 
        "create table if not exists roles ("
      + "role text not null,"
      + "userUuid text not null,"
      + "primary key( role, userUuid )"
      + ");";
    
    private static final String CREATE_ROLE =
        "insert into roles"
      + "(       role, userUuid )"
      + "values( ?,    ? );";
    
    private static final String RETRIEVE_ROLES_FOR_USER = 
        "select role as role from roles where userUuid = ?"
      + " order by role asc;";
    
    private static final String DELETE_ROLES_FOR_USER = 
        "delete from roles where userUuid = ?;";
    
    /**
     * Connects to or creates the database using the specified file.
     * 
     * All tables will be created if necessary.
     * 
     * @param database the location of the database file
     * @param rolesProvider the role provider
     * @param loginManager the login manager
     * 
     * @throws PersistenceException if we are unable to connect to 
     * or create the database.
     */
    public SqliteUserDataStore( File database, RoleProvider rolesProvider ) throws PersistenceException{
        this.connectionUrl = "jdbc:sqlite:" + database.getAbsolutePath();
        this.roleProvider = rolesProvider;
        loadDriver();
        createTables();
    }
    
    private void createTables() throws PersistenceException {
        try ( Connection connection = DriverManager.getConnection( connectionUrl ) ) {
            connection.setAutoCommit( false );

            PreparedStatement statement = connection.prepareStatement( TABLE_CREATE_USER );
            statement.executeUpdate();

            statement = connection.prepareStatement( TABLE_CREATE_CONTACT_INFO );
            statement.executeUpdate();

            statement = connection.prepareStatement( TABLE_CREATE_ROLES );
            statement.executeUpdate();

            connection.commit();
        }
        catch( SQLException ex ) {
            throw new PersistenceException( "Error creating database", ex );
        }
    }
    
    private void listTables() throws PersistenceException {
        try ( Connection connection = DriverManager.getConnection( connectionUrl ) ) {
            PreparedStatement statement = connection.prepareStatement( "SELECT * FROM sqlite_master WHERE type='table';" );
            ResultSet results = statement.executeQuery();
            while( results.next() ){
                System.out.println( "Table: " + results.getString( 1 ) );
                try{
                    int i = 2;
                    while(true){
                        System.out.println( "    " + results.getString( i ) );
                        i++;
                    }
                }catch( Throwable t ){}
            }
        }
        catch( SQLException ex ) {
            throw new PersistenceException( "Error querying database", ex );
        }
    }

    private static void loadDriver() throws PersistenceException {
        try {
            Class.forName( "org.sqlite.JDBC" );
        }
        catch( ClassNotFoundException ex ) {
            throw new PersistenceException( "SQLite JDBC driver not found", ex );
        }
    }

    @Override
    public String store( User user ) throws PersistenceException {
        if( user == null ) {
            throw new NullPointerException( "User cannot be null" );
        }

        boolean exists = false;
        
        if( StringUtils.isBlank( user.getUuid() ) ){
            UuidFactory.ensureUuid( user );
        }
        else {
            exists = exists( user.getUuid() );
        }
        
        if( exists ){
            update( user );
        }
        else {
            create( user );
        }

        return user.getUuid();
    }
    
    @Override
    public void changePassword( String userUuid, String password ) throws PersistenceException{
        if( isBlank( userUuid ) || isBlank( password ) ) {
            throw new NullPointerException( "User uuid and password cannot be empty" );
        }

        User user = retrieve( userUuid );
        if( user == null ) {
            throw new PersistenceException( "No user with UUID " + userUuid + " exists" );
        }
        
        String hash = DefaultLoginManager.encryptPassword(password);
        user.setPasswordHash(password);
        
        try ( Connection connection = DriverManager.getConnection( connectionUrl ) ) {
            PreparedStatement statement = connection.prepareStatement( UPDATE_PASSWORD );
            statement.setString(1, hash);
            statement.setString(2, userUuid);
            statement.executeUpdate();
        }
        catch(Throwable t ){
            logger.error( "Error", t );
            throw new PersistenceException(t);
        }
        
    }
    
    private void create( User user ) throws PersistenceException {
        try ( Connection connection = DriverManager.getConnection( connectionUrl ) ) {
            connection.setAutoCommit( false );
            
            PreparedStatement statement = connection.prepareStatement( CREATE_USER );
            statement.setString( 1, user.getUuid() );
            statement.setString( 2, user.getName() );
            statement.setString( 3, user.getSortByName() );
            statement.setString( 4, user.getDisplayName() );
            statement.setString( 5, user.getTitle() );
            statement.setBoolean( 6, user.isEnabled() );
            statement.setString( 7, user.getCredential() );
            statement.setString( 8, user.getLoginName() );
            statement.setString( 9, user.getPasswordHash() );
            statement.executeUpdate();
            
            if( user.getContactInfo() != null ){
                for( ContactInfo contactInfo : user.getContactInfo() ){
                    UuidFactory.ensureUuid( contactInfo );
                    
                    statement = connection.prepareStatement( CREATE_CONTACT_INFO );
                    statement.setString( 1, contactInfo.getUuid() );
                    statement.setString( 2, user.getUuid() );
                    statement.setString( 3, contactInfo.getType() );
                    statement.setString( 4, contactInfo.getContact() );
                    statement.setString( 5, contactInfo.getDescription() );
                    statement.setInt( 6, contactInfo.getOrder() );
                    statement.executeUpdate();
                }
            }
            
            if( user.getRoles() != null ){
                for( String role : user.getRoles() ){
                    statement = connection.prepareStatement( CREATE_ROLE );
                    statement.setString( 1, role );
                    statement.setString( 2, user.getUuid() );
                    statement.executeUpdate();
                }
            }
            
            connection.commit();
        }
        catch( SQLException ex ) {
            throw new PersistenceException( "Error updating database", ex );
        }
    }
    
    private void update( User user ) throws PersistenceException {
        //when a client updates a user, they generally won't
        //send the password along. In this case, we will grab 
        //the original and add it to the data we're storing
        if( StringUtils.isBlank( user.getPasswordHash() ) ){
            try ( Connection connection = DriverManager.getConnection( connectionUrl ) ) {
                PreparedStatement statement = connection.prepareStatement( RETRIEVE_PASSWORD_HASH_FOR_USER );
                statement.setString( 1, user.getUuid() );

                ResultSet results = statement.executeQuery();
                if( results.next() ) {
                    String passwordHash = results.getString( "passwordHash" );
                    user.setPasswordHash( passwordHash );
                }
            }
            catch( SQLException ex ) {
                throw new PersistenceException( "Error querying database", ex );
            }
        }

        //this is ... less than optimal        
        delete( user.getUuid() );
        store( user );
    }
    
    @Override
    public boolean exists( String uuid ) throws PersistenceException {
        if( StringUtils.isBlank( uuid ) ) {
            return false;
        }

        try ( Connection connection = DriverManager.getConnection( connectionUrl ) ) {
            PreparedStatement statement = connection.prepareStatement( USER_EXISTS );
            statement.setString( 1, uuid );
            ResultSet results = statement.executeQuery();
            return results.next();
        }
        catch( SQLException ex ) {
            throw new PersistenceException( "Error querying database", ex );
        }
    }
    
    @Override
    public boolean existsByCredential( String credential ) throws PersistenceException {
        if( StringUtils.isBlank( credential ) ) {
            return false;
        }

        try ( Connection connection = DriverManager.getConnection( connectionUrl ) ) {
            PreparedStatement statement = connection.prepareStatement( USER_EXISTS_BY_CREDENTIAL );
            statement.setString( 1, credential );
            ResultSet results = statement.executeQuery();
            return results.next();
        }
        catch( SQLException ex ) {
            throw new PersistenceException( "Error querying database", ex );
        }
    }
    
    @Override
    public boolean existsByLoginName( String loginName ) throws PersistenceException {
        if( StringUtils.isBlank( loginName ) ) {
            return false;
        }

        try ( Connection connection = DriverManager.getConnection( connectionUrl ) ) {
            PreparedStatement statement = connection.prepareStatement( USER_EXISTS_BY_LOGIN_NAME );
            statement.setString( 1, loginName );
            ResultSet results = statement.executeQuery();
            return results.next();
        }
        catch( SQLException ex ) {
            throw new PersistenceException( "Error querying database", ex );
        }
    }
    
    @Override
    public User retrieve( String uuid ) throws PersistenceException {
        try ( Connection connection = DriverManager.getConnection( connectionUrl ) ) {
            PreparedStatement statement = connection.prepareStatement( RETRIEVE_USER );
            statement.setString( 1, uuid );
            ResultSet results = statement.executeQuery();

            User user = null;

            if( results.next() ) {
                user = unmarshallUser( results, connection );
            }

            return user;
        }
        catch( SQLException ex ) {
            throw new PersistenceException( "Error querying database", ex );
        }
    }
    
    @Override
    public User retrieveByCredential( String credential ) throws PersistenceException {
        try ( Connection connection = DriverManager.getConnection( connectionUrl ) ) {
            PreparedStatement statement = connection.prepareStatement( RETRIEVE_USER_BY_CREDENTIAL );
            statement.setString( 1, credential );
            ResultSet results = statement.executeQuery();

            User user = null;

            if( results.next() ) {
                user = unmarshallUser( results, connection );
            }

            return user;
        }
        catch( SQLException ex ) {
            throw new PersistenceException( "Error querying database", ex );
        }
    }
    
    @Override
    public User retrieveByLoginName( String loginName ) throws PersistenceException {
        try ( Connection connection = DriverManager.getConnection( connectionUrl ) ) {
            PreparedStatement statement = connection.prepareStatement( RETRIEVE_USER_BY_LOGIN_NAME );
            statement.setString( 1, loginName );
            ResultSet results = statement.executeQuery();

            User user = null;

            if( results.next() ) {
                user = unmarshallUser( results, connection );
            }

            return user;
        }
        catch( SQLException ex ) {
            throw new PersistenceException( "Error querying database", ex );
        }
    }
    
    @Override
    public List<User> retrieveAll() throws PersistenceException{
        List<User> result = new ArrayList();
        
        try ( Connection connection = DriverManager.getConnection( connectionUrl ) ) {
            PreparedStatement statement = connection.prepareStatement( RETRIEVE_USERS );
            ResultSet results = statement.executeQuery();

            while( results.next() ) {
                User user = unmarshallUser( results, connection );
                result.add( user );
            }
        }
        catch( SQLException ex ) {
            throw new PersistenceException( "Error querying database", ex );
        }
        
        return result;
    }
    
    private User unmarshallUser( ResultSet results, Connection connection ) throws SQLException {
        User user = createEmptyUser();
        user.setUuid( results.getString( "uuid" ) );
        user.setName( results.getString( "name" ) );
        user.setSortByName( results.getString( "sortByName" ) );
        user.setDisplayName( results.getString( "displayName" ) );
        user.setTitle( results.getString( "title" ) );
        user.setEnabled( results.getBoolean( "enabled" ) );
        user.setCredential( results.getString( "credential" ) );
        user.setLoginName( results.getString( "loginName" ) );
        user.setPasswordHash( results.getString( "passwordHash" ) );

        PreparedStatement statement = connection.prepareStatement( RETRIEVE_CONTACT_INFO_FOR_USER );
        statement.setString( 1, user.getUuid() );

        results = statement.executeQuery();
        while( results.next() ) {
            ContactInfo contactInfo = new ContactInfo();
            contactInfo.setUuid( results.getString( "uuid" ) );
            contactInfo.setType( results.getString( "type" ) );
            contactInfo.setContact( results.getString( "contact" ) );
            contactInfo.setDescription( results.getString( "description" ) );
            contactInfo.setOrder( results.getInt( "position" ) );

            user.getContactInfo().add( contactInfo );
        }
        
        statement = connection.prepareStatement( RETRIEVE_ROLES_FOR_USER );
        statement.setString( 1, user.getUuid() );

        results = statement.executeQuery();
        while( results.next() ) {
            user.getRoles().add( results.getString( "role" ) );
        }
        
        return user;
    }
    
    @Override
    public boolean delete( String uuid ) throws PersistenceException {
        if( StringUtils.isBlank( uuid ) ) {
            return false;
        }

        try ( Connection connection = DriverManager.getConnection( connectionUrl ) ) {
            connection.setAutoCommit( false );
            
            PreparedStatement statement = connection.prepareStatement( DELETE_USER );
            statement.setString( 1, uuid );
            int usersDeleted = statement.executeUpdate();
            
            statement = connection.prepareStatement( DELETE_CONTACT_INFO_FOR_USER );
            statement.setString( 1, uuid );
            statement.executeUpdate();
            
            statement = connection.prepareStatement( DELETE_ROLES_FOR_USER );
            statement.setString( 1, uuid );
            statement.executeUpdate();
            
            connection.commit();
            return usersDeleted != 0;
        }
        catch( SQLException ex ) {
            throw new PersistenceException( "Error deleting from database", ex );
        }
    }

    @Override
    public boolean hasPermission( String uuid, String permission ) throws PersistenceException {
        if( roleProvider == null ){
            return false;
        }
        
        if( StringUtils.isBlank( uuid ) || StringUtils.isBlank( permission ) ) {
            return false;
        }
        
        try ( Connection connection = DriverManager.getConnection( connectionUrl ) ) {
            PreparedStatement statement = connection.prepareStatement( RETRIEVE_ROLES_FOR_USER );
            statement.setString( 1, uuid );

            List<String> roles = new ArrayList();
            
            ResultSet results = statement.executeQuery();
            while( results.next() ) {
                String role = results.getString( "role" );
                roles.add( role );
            }
            
            List<String> permissions = roleProvider.permissions( roles );
            return permissions.contains( permission );
        }
        catch( SQLException ex ) {
            throw new PersistenceException( "Error deleting from database", ex );
        }
    }
    
    @Override
    public List<Role> getRoles( String uuid ) throws PersistenceException {
        List<Role> result = new ArrayList();

        if( !StringUtils.isBlank( uuid ) ) {
            try ( Connection connection = DriverManager.getConnection( connectionUrl ) ) {
                PreparedStatement statement = connection.prepareStatement( RETRIEVE_ROLES_FOR_USER );
                statement.setString( 1, uuid );

                ResultSet results = statement.executeQuery();
                while( results.next() ) {
                    String role = results.getString( "role" );
                    result.add( roleProvider.get( role ) );
                }
            }
            catch( SQLException ex ) {
                throw new PersistenceException( "Error deleting from database", ex );
            }
        }

        return result;
    }
    
    public User createEmptyUser(){
        return new User();
    }
}
