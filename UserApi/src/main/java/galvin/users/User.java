package galvin.users;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class User
{
    private String uuid;
    private String name;
    private String sortByName;
    private List<ContactInfo> contactInfo = new ArrayList();
    private List<String> roles = new ArrayList();
}
