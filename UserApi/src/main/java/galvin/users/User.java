package galvin.users;

import com.galvin.db.HasUuid;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class User implements HasUuid
{
    private String uuid;
    private String name;
    private String sortByName;
    private String displayName;
    private String title;
    private boolean enabled;
    
    //credentials will *either* be a PKI certificate, or a login name / password
    // combo. We DO NOT store the password unencrypted; instead, we use the 
    // BCrypt algorithm to store a one-time hash
    private String credential;
    private String loginName;
    private String passwordHash;
    
    private List<ContactInfo> contactInfo = new ArrayList();
    private List<String> roles = new ArrayList();
}
