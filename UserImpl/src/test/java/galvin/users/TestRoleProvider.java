package galvin.users;

public class TestRoleProvider extends DefaultRoleProvider {
    public static final String PERM_ADMIN = "admin";
    public static final String PERM_CALL_TRIAL = "call-trial";
    public static final String PERM_HOLD_TRIAL = "hold-trial";
    public static final String PERM_SPEAK_ILL = "speak-ill";
    public static final String PERM_DEMAND_WORSHIP = "demand-worship";
    public static final String PERM_BESEECH = "beseech";

    public static Role ADMINISTRATOR = new Role( "admin", "Administrator",
                                                 PERM_ADMIN, PERM_CALL_TRIAL, PERM_HOLD_TRIAL, PERM_SPEAK_ILL, PERM_DEMAND_WORSHIP, PERM_BESEECH );
    public static Role INQUISITOR = new Role( "inquisitor", "Inquisitor", PERM_CALL_TRIAL );
    public static Role JUSTICAR = new Role( "justicar", "Justicar", PERM_HOLD_TRIAL );
    public static Role ACCUSER = new Role( "accuser", "Accuser", PERM_SPEAK_ILL );
    public static Role CHOIRMASTER = new Role( "choirmaster", "Choirmaster", PERM_DEMAND_WORSHIP );
    public static Role SUPPLICANT = new Role( "supplicant", "Supplicant", PERM_BESEECH );

    public TestRoleProvider() {
        super( new Role[]{
            ADMINISTRATOR,
            INQUISITOR,
            JUSTICAR,
            ACCUSER,
            CHOIRMASTER,
            SUPPLICANT, } );
    }
}