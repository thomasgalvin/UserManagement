package galvin.users;

import com.galvin.db.HasUuid;
import lombok.Data;

@Data
public class ContactInfo implements HasUuid
{
    private String uuid;
    private String type;
    private String contact;
    private String description;
    private int order;
}
