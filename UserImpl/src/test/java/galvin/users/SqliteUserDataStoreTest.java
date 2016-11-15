package galvin.users;

import com.galvin.db.UuidFactory;
import java.io.File;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SqliteUserDataStoreTest {
    
    public SqliteUserDataStoreTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Test 
    public void testOnDisk() throws Exception {
        File file = new File( "target/users.db" );
        SqliteUserDataStore dataStore = new SqliteUserDataStore( file, new TestRoleProvider() );
        LoginManager loginManager = new DefaultLoginManager( dataStore );
        runTests( dataStore, loginManager );
    }
    
    private void runTests( SqliteUserDataStore dataStore, LoginManager loginManager ) throws Exception{
        String password = "password";
        String passwordHash = loginManager.encrypt( password );
        
        User user = new User();
        user.setName( "Thomas Galvin" );
        user.setSortByName( "Galvin, Thomas" );
        user.setDisplayName( "Thomas" );
        user.setTitle( "Field Marshall of the Fell Hordes of the Dying Light" );
        user.setEnabled( true );
        user.setCredential( "12345-abcde" );
        user.setLoginName( "tgalvin" );
        user.setPasswordHash( passwordHash );
        
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setType( "email" );
        contactInfo.setDescription( "Home email" );
        contactInfo.setContact( "thomas@foo.bar" );
        user.getContactInfo().add( contactInfo );
        
        contactInfo = new ContactInfo();
        contactInfo.setType( "phone" );
        contactInfo.setDescription( "Work phone" );
        contactInfo.setContact( "555.555.5555" );
        user.getContactInfo().add( contactInfo );
        
        user.getRoles().add( TestRoleProvider.ADMINISTRATOR.getUuid() );
        user.getRoles().add( TestRoleProvider.INQUISITOR.getUuid() );
        user.getRoles().add( TestRoleProvider.JUSTICAR.getUuid() );
        
        String uuid = dataStore.store( user );
        Assert.assertNotNull( "Store operation returned null uuid", uuid );
        
        User loaded = dataStore.retrieve( uuid );
        Assert.assertEquals( "Loaded user did not match original", user, loaded );
        
        loaded = dataStore.retrieveByCredential( "12345-abcde" );
        Assert.assertEquals( "Load by credential: user did not match original", user, loaded );
        
        loaded = dataStore.retrieveByLoginName( "tgalvin" );
        Assert.assertEquals( "Load by credential: user did not match original", user, loaded );
        
        boolean exists = dataStore.exists( uuid );
        Assert.assertTrue( "Exists query failed", exists );
        
        exists = dataStore.existsByCredential( "12345-abcde" );
        Assert.assertTrue( "Exists query failed", exists );
        
        exists = dataStore.existsByLoginName( "tgalvin" );
        Assert.assertTrue( "Exists query failed", exists );
        
        exists = dataStore.exists( "jktj34k31jtk3jtk3t4" );
        Assert.assertFalse( "Exists query failed", exists );
        
        exists = dataStore.existsByCredential( "hk5hk45kyk2j5yk5yj4" );
        Assert.assertFalse( "Exists query failed", exists );
        
        exists = dataStore.existsByLoginName( "r3kq45tj35kjk3jt" );
        Assert.assertFalse( "Exists query failed", exists );
        
        List<User> users = dataStore.retrieveAll();
        Assert.assertEquals( "Unexpected user count", 1, users.size() );
        
        List<Role> loadedRoles = dataStore.getRoles( uuid );
        Assert.assertTrue( "Did not have role: " + TestRoleProvider.ADMINISTRATOR.getName(), loadedRoles.contains( TestRoleProvider.ADMINISTRATOR ) );
        Assert.assertTrue( "Did not have role: " + TestRoleProvider.INQUISITOR.getName(), loadedRoles.contains( TestRoleProvider.INQUISITOR ) );
        Assert.assertTrue( "Did not have role: " + TestRoleProvider.JUSTICAR.getName(), loadedRoles.contains( TestRoleProvider.JUSTICAR ) );
        
        Assert.assertFalse( "Did should not have role: " + TestRoleProvider.ACCUSER.getName(), loadedRoles.contains( TestRoleProvider.ACCUSER ) );
        Assert.assertFalse( "Did should not have role: " + TestRoleProvider.CHOIRMASTER.getName(), loadedRoles.contains( TestRoleProvider.CHOIRMASTER ) );
        Assert.assertFalse( "Did should not have role: " + TestRoleProvider.SUPPLICANT.getName(), loadedRoles.contains( TestRoleProvider.SUPPLICANT ) );
        
        Assert.assertTrue( "Did not have permission: " + TestRoleProvider.PERM_ADMIN, dataStore.hasPermission( uuid, TestRoleProvider.PERM_ADMIN ) );
        Assert.assertTrue( "Did not have permission: " + TestRoleProvider.PERM_CALL_TRIAL, dataStore.hasPermission( uuid, TestRoleProvider.PERM_CALL_TRIAL ) );
        Assert.assertTrue( "Did not have permission: " + TestRoleProvider.PERM_HOLD_TRIAL, dataStore.hasPermission( uuid, TestRoleProvider.PERM_HOLD_TRIAL ) );
        Assert.assertTrue( "Did not have permission: " + TestRoleProvider.PERM_SPEAK_ILL, dataStore.hasPermission( uuid, TestRoleProvider.PERM_SPEAK_ILL ) );
        Assert.assertTrue( "Did not have permission: " + TestRoleProvider.PERM_DEMAND_WORSHIP, dataStore.hasPermission( uuid, TestRoleProvider.PERM_DEMAND_WORSHIP ) );
        Assert.assertTrue( "Did not have permission: " + TestRoleProvider.PERM_BESEECH, dataStore.hasPermission( uuid, TestRoleProvider.PERM_BESEECH ) );
        
        LoginToken token = loginManager.login( "12345-abcde" );
        Assert.assertNotNull( "Login manager did not return a token for credential", token );
        
        loginManager.validate( token.getUuid() );
        loginManager.demandPermission( token.getUuid(), TestRoleProvider.PERM_ADMIN );
        loginManager.demandPermission( token.getUuid(), TestRoleProvider.PERM_CALL_TRIAL );
        loginManager.demandPermission( token.getUuid(), TestRoleProvider.PERM_HOLD_TRIAL );
        loginManager.demandPermission( token.getUuid(), TestRoleProvider.PERM_SPEAK_ILL );
        loginManager.demandPermission( token.getUuid(), TestRoleProvider.PERM_DEMAND_WORSHIP );
        loginManager.demandPermission( token.getUuid(), TestRoleProvider.PERM_BESEECH );
        
        token = loginManager.login( "tgalvin", password );
        Assert.assertNotNull( "Login manager did not return a token for credential", token );
        
        loginManager.validate( token.getUuid() );
        loginManager.demandPermission( token.getUuid(), TestRoleProvider.PERM_ADMIN );
        loginManager.demandPermission( token.getUuid(), TestRoleProvider.PERM_CALL_TRIAL );
        loginManager.demandPermission( token.getUuid(), TestRoleProvider.PERM_HOLD_TRIAL );
        loginManager.demandPermission( token.getUuid(), TestRoleProvider.PERM_SPEAK_ILL );
        loginManager.demandPermission( token.getUuid(), TestRoleProvider.PERM_DEMAND_WORSHIP );
        loginManager.demandPermission( token.getUuid(), TestRoleProvider.PERM_BESEECH );
        
        password = "12345";
        passwordHash = loginManager.encrypt( password );
        
        
        String originalPasswordHash = user.getPasswordHash();
        user.setPasswordHash( null );
        user.setTitle( "Grand Lord of the Swarming Mists" );
        uuid = dataStore.store( user );
        
        loaded = dataStore.retrieve( uuid );
        user.setPasswordHash( originalPasswordHash );
        Assert.assertEquals( "Loaded, updated user did not match original", user, loaded );
        
        ////////////////////////////////////////////////////////////////////////
        
        
        user = new User();
        user.setName( "Samantha Redgrave" );
        user.setSortByName( "Redgrave, Samantha" );
        user.setDisplayName( "Sam" );
        user.setTitle( "High Priestess of the Oncoming Storms" );
        user.setEnabled( true );
        user.setCredential( "09876-zyxwv" );
        user.setLoginName( "sam" );
        user.setPasswordHash( passwordHash );
        
        contactInfo = new ContactInfo();
        contactInfo.setType( "email" );
        contactInfo.setDescription( "Home email" );
        contactInfo.setContact( "same@foo.bar" );
        user.getContactInfo().add( contactInfo );
        
        contactInfo = new ContactInfo();
        contactInfo.setType( "phone" );
        contactInfo.setDescription( "Work phone" );
        contactInfo.setContact( "555.123.4567" );
        user.getContactInfo().add( contactInfo );
        
        user.getRoles().add( TestRoleProvider.ACCUSER.getUuid() );
        user.getRoles().add( TestRoleProvider.CHOIRMASTER.getUuid() );
        user.getRoles().add( TestRoleProvider.SUPPLICANT.getUuid() );
        
        String uuid2 = dataStore.store( user );
        Assert.assertNotNull( "Store operation returned null uuid", uuid2 );
        
        loaded = dataStore.retrieve( uuid2 );
        Assert.assertEquals( "Loaded user did not match original", user, loaded );
        
        loadedRoles = dataStore.getRoles( uuid2 );
        
        Assert.assertTrue( "Did not have role: " + TestRoleProvider.ACCUSER.getName(), loadedRoles.contains( TestRoleProvider.ACCUSER ) );
        Assert.assertTrue( "Did not have role: " + TestRoleProvider.CHOIRMASTER.getName(), loadedRoles.contains( TestRoleProvider.CHOIRMASTER ) );
        Assert.assertTrue( "Did not have role: " + TestRoleProvider.SUPPLICANT.getName(), loadedRoles.contains( TestRoleProvider.SUPPLICANT ) );
        
        Assert.assertTrue( "Did should not have role: " + TestRoleProvider.ADMINISTRATOR.getName(), loadedRoles.contains( TestRoleProvider.ACCUSER ) );
        Assert.assertTrue( "Did should not have role: " + TestRoleProvider.INQUISITOR.getName(), loadedRoles.contains( TestRoleProvider.CHOIRMASTER ) );
        Assert.assertTrue( "Did should not have role: " + TestRoleProvider.JUSTICAR.getName(), loadedRoles.contains( TestRoleProvider.SUPPLICANT ) );
        
        Assert.assertFalse( "Should not have permission: " + TestRoleProvider.PERM_ADMIN, dataStore.hasPermission( uuid2, TestRoleProvider.PERM_ADMIN ) );
        Assert.assertFalse( "Should not have permission: " + TestRoleProvider.PERM_CALL_TRIAL, dataStore.hasPermission( uuid2, TestRoleProvider.PERM_CALL_TRIAL ) );
        Assert.assertFalse( "Should not have permission: " + TestRoleProvider.PERM_HOLD_TRIAL, dataStore.hasPermission( uuid2, TestRoleProvider.PERM_HOLD_TRIAL ) );
        
        Assert.assertTrue( "Did not have permission: " + TestRoleProvider.PERM_SPEAK_ILL, dataStore.hasPermission( uuid2, TestRoleProvider.PERM_SPEAK_ILL ) );
        Assert.assertTrue( "Did not have permission: " + TestRoleProvider.PERM_DEMAND_WORSHIP, dataStore.hasPermission( uuid2, TestRoleProvider.PERM_DEMAND_WORSHIP ) );
        Assert.assertTrue( "Did not have permission: " + TestRoleProvider.PERM_BESEECH, dataStore.hasPermission( uuid2, TestRoleProvider.PERM_BESEECH ) );
        
        token = loginManager.login( "09876-zyxwv" );
        Assert.assertNotNull( "Login manager did not return a token for credential", token );
        
        loginManager.validate( token.getUuid() );
        loginManager.demandPermission( token.getUuid(), TestRoleProvider.PERM_SPEAK_ILL );
        loginManager.demandPermission( token.getUuid(), TestRoleProvider.PERM_DEMAND_WORSHIP );
        loginManager.demandPermission( token.getUuid(), TestRoleProvider.PERM_BESEECH );
        
        shouldNotHavePermission( loginManager, token, TestRoleProvider.PERM_ADMIN );
        shouldNotHavePermission( loginManager, token, TestRoleProvider.PERM_CALL_TRIAL );
        shouldNotHavePermission( loginManager, token, TestRoleProvider.PERM_HOLD_TRIAL );
        
        token = loginManager.login( "sam", password );
        Assert.assertNotNull( "Login manager did not return a token for credential", token );
        
        loginManager.validate( token.getUuid() );
        
        loginManager.demandPermission( token.getUuid(), TestRoleProvider.PERM_SPEAK_ILL );
        loginManager.demandPermission( token.getUuid(), TestRoleProvider.PERM_DEMAND_WORSHIP );
        loginManager.demandPermission( token.getUuid(), TestRoleProvider.PERM_BESEECH );
        
        shouldNotHavePermission( loginManager, token, TestRoleProvider.PERM_ADMIN );
        shouldNotHavePermission( loginManager, token, TestRoleProvider.PERM_CALL_TRIAL );
        shouldNotHavePermission( loginManager, token, TestRoleProvider.PERM_HOLD_TRIAL );
        
        boolean foundOne = false;
        boolean foundTwo = false;
        users = dataStore.retrieveAll();
        Assert.assertEquals( "Unexpected user count", 2, users.size() );
        for( User tmp : users ){
            if( uuid.endsWith( tmp.getUuid() ) ){
                foundOne = true;
            }
            else if( uuid2.endsWith( tmp.getUuid() ) ){
                foundTwo = true;
            }
        }
        
        Assert.assertTrue( "Did not find original user", foundOne );
        Assert.assertTrue( "Did not find second user", foundTwo );
        
        ////////////////////////////////////////////////////////////////////////
        
        try{
            loginManager.login( UuidFactory.generateUuid() );
        }
        catch( LoginException ex ){
            //this is what we wanted
        }
        
        try{
            loginManager.login( "sam", "badpassword" );
        }
        catch( LoginException ex ){
            //this is what we wanted
        }
        
        try{
            loginManager.login( "baduser", "badpassword" );
        }
        catch( LoginException ex ){
            //this is what we wanted
        }
        
        ////////////////////////////////////////////////////////////////////////

        
        password = "67890";
        dataStore.changePassword(uuid, password);
        user = dataStore.retrieve(uuid);
        token = loginManager.login( user.getLoginName(), password );
        Assert.assertNotNull( "Change password failed", token );
        
        
        ////////////////////////////////////////////////////////////////////////
        
        
        boolean deleted = dataStore.delete( uuid );
        Assert.assertTrue( "Delete returned false", deleted );
        
        loaded = dataStore.retrieve( uuid );
        Assert.assertNull( "Data store returned a deleted user", loaded );
        
        deleted = dataStore.delete( uuid );
        Assert.assertFalse( "Delete returned true for already deleted user", deleted );
        
        deleted = dataStore.delete( uuid2 );
        Assert.assertTrue( "Delete returned false", deleted );
        
        loaded = dataStore.retrieve( uuid2 );
        Assert.assertNull( "Data store returned a deleted user", loaded );
        
        deleted = dataStore.delete( uuid2 );
        Assert.assertFalse( "Delete returned true for already deleted user", deleted );
    }
    
    private void shouldNotHavePermission( LoginManager loginManager, LoginToken loginToken, String permission ) throws Exception {
        try{
            loginManager.demandPermission( loginToken.getUuid(), permission );
            throw new Exception( "User should not have had permission: " + permission );
        }
        catch( PermissionsException ex ){
            //this is what we wanted
        }
    }
}
