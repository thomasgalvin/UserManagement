package galvin.users;

public class PermissionsException extends Exception
{
    public PermissionsException( String permission ){
        super( "User does not have requested permission: " + permission );
    }
}
