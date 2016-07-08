package galvin.users;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * This class extends the User class to contain an unencrypted
 * password and password confirmation. 
 * 
 * These are not meant to be stored! This class is only intended as a 
 * data transfer object between the client and the back end. When the
 * account request is stored, the password and confirmation should
 * be compared. If they are not equal, a PasswordMismatchException should
 * be thrown. If they are equal, the password should be hashed (for example,
 * using LoginManager's encrypt() function), the passwordHash should be set
 * to this value, and the password and confirmation should be set to null.
 */
@Data @ToString(callSuper = true) @EqualsAndHashCode(callSuper = true)
public class AccountRequest extends User
{
    private String password;
    private String confirmPassword;
}
