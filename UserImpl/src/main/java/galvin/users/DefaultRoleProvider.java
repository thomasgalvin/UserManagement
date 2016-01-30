package galvin.users;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DefaultRoleProvider implements RoleProvider {
    private final Role[] roles;

    public DefaultRoleProvider( Role... roles ) {
        this.roles = roles;
    }

    @Override
    public Role get( String roleUuid ) {
        if( roles != null ) {
            for( Role role : roles ) {
                if( roleUuid.equals( role.getUuid() ) ) {
                    return role.clone();
                }
            }
        }

        return null;
    }

    @Override
    public List<Role> get( List<String> roleUuids ) {
        Set<Role> result = new HashSet();

        if( roles != null ) {
            for( Role role : roles ) {
                for( String uuid : roleUuids ) {
                    if( uuid.equals( role.getUuid() ) ) {
                        result.add( role.clone() );
                    }
                }
            }
        }

        List<Role> list = new ArrayList();
        list.addAll( result );
        return list;
    }

    @Override
    public List<String> permissions( List<String> roleUuids ) {
        Set<String> result = new HashSet();

        List<Role> roles = get( roleUuids );
        for( Role role : roles ) {
            result.addAll( role.getPermissions() );
        }

        List<String> list = new ArrayList();
        list.addAll( result );
        Collections.sort( list );
        return list;
    }

    @Override
    public List<String> permissions( String roleUuid ) {
        List<String> roles = Arrays.asList( new String[]{ roleUuid } );
        return permissions( roles );
    }

    @Override
    public Role[] get() {
        if( roles == null ) {
            return new Role[ 0 ];
        }

        int index = 0;
        Role[] result = new Role[ roles.length ];
        for( Role role : roles ) {
            result[index] = role.clone();
            index++;
        }
        return result;
    }

}
