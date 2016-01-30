package galvin.users;

import com.galvin.db.PersistenceException;

public interface LoginManager
{
    /**
     * Logs into the system with a PKI-like certificate
     * @param credential the certificate to use as a login credential
     * @return a login token
     * @throws PersistenceException if an error occurred contacting the database
     * @throws LoginException if the login failed
     */
    public LoginToken login( String credential ) throws PersistenceException, LoginException;
    
    /**
     * Logs into the system with a user name and password.
     * @param loginName the user name
     * @param password the (unencrypted) password
     * @return a login token
     * @throws PersistenceException if an error occurred contacting the database
     * @throws LoginException if the login failed
     */
    public LoginToken login( String loginName, String password ) throws PersistenceException, LoginException;
    
    /**
     * Validates that a token's UUID corresponds to a current, valid session.
     * @param token the login token UUID; e.g. token.gertUuid()
     * @return a login token
     * @throws PersistenceException if an error occurred contacting the database
     * @throws LoginException if the token does not correspond to a current login
     *         session, or if the token does not correspond to a valid, enabled
     *         user.
     */
    public LoginToken validate( String token ) throws PersistenceException, LoginException;
    
    /**
     * Ensures the user represented by the login token has the given permission.
     * @param token the login token
     * @param permission the permission
     * @throws PersistenceException if an error occurred contacting the database
     * @throws LoginException if the token does not correspond to a current login
     *         session, or if the token does not correspond to a valid, enabled
     *         user.
     * @throws PermissionsException if the login token is valid, but the
     *         user does not have the appropriate permission.
     */
    public void demandPermission( String token, String permission ) throws PersistenceException, LoginException, PermissionsException;
    
    /**
     * Hashes the password in a cryptographically secure manner.
     * @param password the unencrypted password.
     * @return the encrypted password hash.
     */
    public String encrypt( String password );
}
