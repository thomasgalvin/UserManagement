package galvin.users;

import com.galvin.db.UuidFactory;
import java.util.List;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;

/**
 * System user roles; eg Administrator, Editor, Contributor, etc.
 * 
 * These are meant to be maintained statically by a Role Provider,
 * not managed inside of a database.
 */
@Getter @EqualsAndHashCode
public class Role implements Cloneable
{
    private static final Logger logger = LoggerFactory.getLogger( Role.class );
    private final String uuid;
    private final String name;
    private final List<String> permissions;
    
    public Role( String uuid, String name, String ... permissions ){
        if( StringUtils.isBlank( uuid ) ){
            this.uuid = UuidFactory.generateUuid();
        }
        else{
            this.uuid = uuid;
        }
        
        this.name = name;
        
        ImmutableList.Builder<String> builder = new ImmutableList.Builder();
        if( permissions != null ){
            builder.add( permissions );
        }
        this.permissions = builder.build();
    }

    @Override
    public Role clone() {
        String[] perms = permissions.toArray( new String[0] );
        Role result = new Role( uuid, name, perms );
        return result;
    }
}
