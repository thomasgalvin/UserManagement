package galvin.users.dw;

import galvin.users.LoginManager;
import galvin.users.LoginToken;
import galvin.users.PassChange;
import galvin.users.User;
import galvin.users.UserDataStore;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.eclipse.jetty.server.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path( "account" )
@Produces( MediaType.APPLICATION_JSON )
@Consumes( MediaType.APPLICATION_JSON )
public class AccountResource
{
    private static final Logger logger = LoggerFactory.getLogger( AccountResource.class );
    private final LoginManager loginManager;
    private final UserDataStore users;

    public AccountResource( LoginManager loginManager, UserDataStore users ) {
        this.loginManager = loginManager;
        this.users = users;
    }
    
    @GET
    @Path( "/" )
    public User myAccount( @Context HttpServletRequest httpRequest,
                           @HeaderParam( "X-Auth-Token" ) String token ) throws WebApplicationException {
        try{
            LoginToken loginToken = loginManager.validate(token);
            return loginToken.getUser();
        }catch( Throwable t ){
            logger.error( "Error", t );
            throw new WebApplicationException(t);
        }
    }
    
    @POST
    @Path( "/" )
    public User updateMyAccount( @Context HttpServletRequest httpRequest,
                                 @HeaderParam( "X-Auth-Token" ) String token,
                                 User user ) throws WebApplicationException {
        try {
            LoginToken loginToken = loginManager.validate(token);
            User authenticatedUser = loginToken.getUser();
            if( authenticatedUser.getUuid().equals( user.getUuid() ) ){
                user.setRoles( authenticatedUser.getRoles() );
                users.store(user);
            }
            else{
                throw new WebApplicationException(Response.SC_FORBIDDEN);
            }
            return null;
        }catch( Throwable t ){
            logger.error( "Error", t );
            throw new WebApplicationException(t);
        }
    }
    
    @POST
    @Path( "/change-password" )
    public void changeMyPassword( @Context HttpServletRequest httpRequest,
                                  @HeaderParam( "X-Auth-Token" ) String token,
                                  PassChange passchange ) throws WebApplicationException {
        try {
            LoginToken loginToken = loginManager.validate(token);
            User authenticatedUser = loginToken.getUser();
        }catch( Throwable t ){
            logger.error( "Error", t );
            throw new WebApplicationException(t);
        }
    }
}
