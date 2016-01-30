package galvin.users;

import java.util.List;

/**
 * Provides a static list of roles and permissions for a given system.
 * 
 */
public interface RoleProvider
{
    /**
     * Returns all of the available roles.
     * @return all of the available roles.
     */
    public Role[] get();
    
    /**
     * Returns the role with the given UUID, or null if no such role exists.
     * @param roleUuid the role's uuid
     * @return the role
     */
    public Role get( String roleUuid );
    
    /**
     * Returns all roles with any of the given UUIDs.
     * @param roleUuids the role UUIDs
     * @return the matching roles
     */
    public List<Role> get( List<String> roleUuids );
    
    /**
     * Returns a list of permissions associated with the given roles.
     * @param roleUuids the role UUIDs
     * @return the permissions
     */
    public List<String> permissions( List<String> roleUuids );
    
    /**
     * Returns a list of permissions associated with a given role UUID
     * @param roleUuid the UUID of the role
     * @return  the permissions
     */
    public List<String> permissions( String roleUuid );
}
