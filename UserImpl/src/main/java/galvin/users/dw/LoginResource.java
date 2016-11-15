package galvin.users.dw;

import galvin.users.Credentials;
import galvin.users.LoginCooldown;
import galvin.users.LoginException;
import galvin.users.LoginManager;
import galvin.users.LoginToken;
import galvin.users.UserDataStore;
import java.security.cert.X509Certificate;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path( "login" )
@Produces( MediaType.APPLICATION_JSON )
@Consumes( MediaType.APPLICATION_JSON )
public class LoginResource {
    private static final Logger logger = LoggerFactory.getLogger(LoginResource.class );
    private final LoginCooldown cooldown = new LoginCooldown();
    private final LoginManager loginManager;
    private final UserDataStore users;

    public LoginResource( LoginManager loginManager, UserDataStore users ) {
        this.loginManager = loginManager;
        this.users = users;
    }

    ////////////////////////////////////////////////////////////////////////////
    /// login
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Logs in, using a PKI certificate if one if provided, or a
     * username/password if a PKI certificate is not available.
     * <p>
     * Auditing is handled in the LoginManager class.
     *
     * @param httpRequest
     * @param credentials
     * @return a login token
     * @throws WebApplicationException
     */
    @POST
    @Path( "login" )
    public LoginToken loginPOST( @Context HttpServletRequest httpRequest,
                                 Credentials credentials ) throws WebApplicationException {
        return login( httpRequest, credentials );
    }

    @GET
    @Path( "login" )
    public LoginToken loginGET( @Context HttpServletRequest httpRequest,
                                Credentials credentials ) throws WebApplicationException {
        return login( httpRequest, credentials );
    }

    public LoginToken login( HttpServletRequest httpRequest,
                             Credentials credentials ) throws WebApplicationException {
        try {
            cooldown.recordAttempt( httpRequest );

            // if the client has presented a login name, attempt to 
            // validate it. If this fails, kick out with an error.
            if( credentials != null ) {
                if( !isBlank( credentials.login ) ) {
                    LoginToken result = loginManager.login( credentials.login, credentials.password );
                    cooldown.recordSuccess( httpRequest );
                    return result;
                }
            }

            // if no login/password has been provided, attempt
            // to validate any certificate present
            if( httpRequest != null ) {
                X509Certificate[] certificates = (X509Certificate[])httpRequest.getAttribute( "javax.servlet.request.X509Certificate" );
                if( certificates != null && certificates.length != 0 ) {
                    for( X509Certificate certificate : certificates ) {
                        try {
                            LoginToken result = loginManager.login( getID( certificate ) );
                            cooldown.recordSuccess( httpRequest );
                            return result;
                        }
                        catch( Throwable t ) {
                            //no-op; multiple certs may have been sent
                        }
                    }
                }
            }

            //if no valid login/password or certificate was presented, 
            // throw an error
            throw new LoginException();
        }
        catch( Throwable t ) {
            throw new WebApplicationException( t );
        }
    }

    private String getID( X509Certificate certificate ){
        return certificate.getSubjectX500Principal().getName() + 
               "@" +
               certificate.getIssuerX500Principal().getName();
    }
    
    ////////////////////////////////////////////////////////////////////////////
    /// logout
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Logs a user out of the application.
     * <p>
     * The Login Token represented by the `token` variable will be
     * invalidated and removed.
     * <p>
     * Auditing is handled in the LoginManager class.
     *
     * @param token the UUID of the token to invalidate
     */
    @POST
    @Path( "logout/{token}" )
    public void logout( @PathParam( "token" ) String token ) {
        try {
            loginManager.logout( token );
        }
        catch( Throwable t ) {
            throw new WebApplicationException( t );
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    /// authentication
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Ensures that a uuid corresponds to a valid login.
     * <p>
     * Auditing is handled in the LoginManager class.
     *
     * @param httpRequest the HTTP Request
     * @param token       the uuid of the token
     * @return the validated login token
     * @throws WebApplicationException if the user is not logged in
     */
    @GET
    @Path( "validate/{token}" )
    public LoginToken validate( @Context HttpServletRequest httpRequest,
                                @PathParam( "token" ) String token ) throws WebApplicationException {
        try {
            cooldown.recordAttempt( httpRequest );
            LoginToken result = loginManager.validate( token );
            cooldown.recordSuccess( httpRequest );
            return result;
        }
        catch( Throwable t ) {
            throw new WebApplicationException( t );
        }
    }

}
