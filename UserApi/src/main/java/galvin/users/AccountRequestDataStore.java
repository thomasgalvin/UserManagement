package galvin.users;

import com.galvin.db.PersistenceException;
import java.util.List;

public interface AccountRequestDataStore
{
    /**
     * Creates or updates a AccountRequest object.
     * 
     * This method should not persist the password or confirm password
     * fields. If the account request's password hash is empty,
     * one should be generated based on the provided passwords.
     * 
     * If this is an update, and neither the password, confirm password, 
     * or password hash are present, the original password hash 
     * should be preserved.
     * 
     * @param accountRequest the accountRequest to store
     * @return the UUID of the accountRequest
     * @throws PersistenceException if an error occurs. 
     * @throws PasswordMismatchException if no password hash the passwords mismatch or are empty
     */
    public String store( AccountRequest accountRequest ) throws PersistenceException, 
                                                                PasswordMismatchException;
    
    /**
     * Returns true if a accountRequest with the given UUID exists.
     * @param uuid the uuid to search for
     * @return true if a accountRequest matching the given criteria exists, false otherwise
     * @throws PersistenceException if an error occurs. 
     */
    public boolean exists( String uuid ) throws PersistenceException;
    
    /**
     * Returns true if a accountRequest with the given UUID exists.
     * @param credential the credential to search for
     * @return true if a accountRequest matching the given criteria exists, false otherwise
     * @throws PersistenceException if an error occurs. 
     */
    public boolean existsByCredential( String credential ) throws PersistenceException;
    
    /**
     * Returns true if a accountRequest with the given login name exists.
     * @param loginName the login name to search for
     * @return true if a accountRequest matching the given criteria exists, false otherwise
     * @throws PersistenceException if an error occurs. 
     */
    public boolean existsByLoginName( String loginName ) throws PersistenceException;
    
    /**
     * Returns a accountRequest with the given UUID, or null if no such accountRequest exists.
     * @param uuid the uuid identifying the accountRequest
     * @return the accountRequest
     * @throws PersistenceException if an error occurs
     */
    public AccountRequest retrieve( String uuid ) throws PersistenceException;
    
    /**
     * Returns a accountRequest with the given credential, or null if no such accountRequest exists.
     * @param credential the credential identifying the accountRequest
     * @return the accountRequest
     * @throws PersistenceException if an error occurs
     */
    public AccountRequest retrieveByCredential( String credential ) throws PersistenceException;
    
    /**
     * Returns a accountRequest with the given login name, or null if no such accountRequest exists.
     * @param loginName the login name identifying the accountRequest
     * @return the accountRequest
     * @throws PersistenceException if an error occurs
     */
    public AccountRequest retrieveByLoginName( String loginName ) throws PersistenceException;
    
    /**
     * Returns all of the accountRequests in the database.
     * @return the accountRequests
     * @throws PersistenceException 
     */
    public List<AccountRequest> retrieveAll() throws PersistenceException;
    
    /**
     * Deletes a accountRequest from the database
     * @param uuid the UUID of the accountRequest to be deleted
     * @return true if a accountRequest was deleted, false otherwise
     * @throws PersistenceException  if an error occurs
     */
    public boolean delete( String uuid ) throws PersistenceException;
    
    /**
     * Checks if a accountRequest has the given permission
     * @param uuid the uuid of the accountRequest
     * @param permission the permission being queried
     * @return true if the uuid corresponds to a accountRequest and that accountRequest has the 
     *         given permission, false otherwise.
     * @throws PersistenceException if an error occurs
     */
    public boolean hasPermission( String uuid, String permission ) throws PersistenceException;
    
    /**
     * Returns all roles associated with the accountRequest
     * @param uuid the uuid of the accountRequest
     * @return a list of Roles that accountRequest has
     * @throws PersistenceException if an error occurs
     */
    public List<Role> getRoles( String uuid ) throws PersistenceException;
}
