package galvin.users;

import com.galvin.db.PersistenceException;
import java.util.List;

public interface UserDataStore
{
    /**
     * Creates or updates a User object.
     * 
     * If this is an update, and the password hash is not present,
     * the original password hash should be preserved.
     * 
     * @param user the user to store
     * @return the UUID of the user
     * @throws PersistenceException if an error occurs. 
     */
    public String store( User user ) throws PersistenceException;
    
    /**
     * Changes a user's password
     * @param userUuid the user's uuid
     * @param password the new password
     * @throws PersistenceException if an error occurs. 
     */
    public void changePassword( String userUuid, String password ) throws PersistenceException;
    
    /**
     * Returns true if a user with the given UUID exists.
     * @param uuid the uuid to search for
     * @return true if a user matching the given criteria exists, false otherwise
     * @throws PersistenceException if an error occurs. 
     */
    public boolean exists( String uuid ) throws PersistenceException;
    
    /**
     * Returns true if a user with the given UUID exists.
     * @param credential the credential to search for
     * @return true if a user matching the given criteria exists, false otherwise
     * @throws PersistenceException if an error occurs. 
     */
    public boolean existsByCredential( String credential ) throws PersistenceException;
    
    /**
     * Returns true if a user with the given login name exists.
     * @param loginName the login name to search for
     * @return true if a user matching the given criteria exists, false otherwise
     * @throws PersistenceException if an error occurs. 
     */
    public boolean existsByLoginName( String loginName ) throws PersistenceException;
    
    /**
     * Returns a user with the given UUID, or null if no such user exists.
     * @param uuid the uuid identifying the user
     * @return the user
     * @throws PersistenceException if an error occurs
     */
    public User retrieve( String uuid ) throws PersistenceException;
    
    /**
     * Returns a user with the given credential, or null if no such user exists.
     * @param credential the credential identifying the user
     * @return the user
     * @throws PersistenceException if an error occurs
     */
    public User retrieveByCredential( String credential ) throws PersistenceException;
    
    /**
     * Returns a user with the given login name, or null if no such user exists.
     * @param loginName the login name identifying the user
     * @return the user
     * @throws PersistenceException if an error occurs
     */
    public User retrieveByLoginName( String loginName ) throws PersistenceException;
    
    /**
     * Returns all of the users in the database.
     * @return the users
     * @throws PersistenceException 
     */
    public List<User> retrieveAll() throws PersistenceException;
    
    /**
     * Deletes a user from the database
     * @param uuid the UUID of the user to be deleted
     * @return true if a user was deleted, false otherwise
     * @throws PersistenceException  if an error occurs
     */
    public boolean delete( String uuid ) throws PersistenceException;
    
    /**
     * Checks if a user has the given permission
     * @param uuid the uuid of the user
     * @param permission the permission being queried
     * @return true if the uuid corresponds to a user and that user has the 
     *         given permission, false otherwise.
     * @throws PersistenceException if an error occurs
     */
    public boolean hasPermission( String uuid, String permission ) throws PersistenceException;
    
    /**
     * Returns all roles associated with the user
     * @param uuid the uuid of the user
     * @return a list of Roles that user has
     * @throws PersistenceException if an error occurs
     */
    public List<Role> getRoles( String uuid ) throws PersistenceException;
}
