package galvin.users;

import lombok.Data;

@Data
public class ContactInfo
{
    private String uuid;
    private String type;
    private String contact;
    private String description;
}
