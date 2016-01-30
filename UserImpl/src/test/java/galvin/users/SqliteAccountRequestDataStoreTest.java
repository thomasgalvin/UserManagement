/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

/**
 *
 * @author galvint
 */
public class SqliteAccountRequestDataStoreTest {
    
    public SqliteAccountRequestDataStoreTest() {
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
        File accountsFile = new File( "target/accountRequests.db" );
        File usersFile = new File( "target/accountRequestUsers.db" );
        RoleProvider roleProvider = new TestRoleProvider();
        
        UserDataStore users = new SqliteUserDataStore( usersFile, roleProvider );
        LoginManager loginManager = new DefaultLoginManager( users );
        
        SqliteAccountRequestDataStore dataStore = new SqliteAccountRequestDataStore(usersFile, roleProvider, loginManager );
        runTests( dataStore, loginManager );
    }
    
    private void runTests( SqliteAccountRequestDataStore dataStore,
                           LoginManager loginManager ) throws Exception{
        String password = "password";
        String passwordHash = loginManager.encrypt( password );
        
        AccountRequest user = new AccountRequest();
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
        
        AccountRequest loaded = dataStore.retrieve( uuid );
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
        
        List<AccountRequest> users = dataStore.retrieveAll();
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
        
        
        user = new AccountRequest();
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
        
        boolean foundOne = false;
        boolean foundTwo = false;
        users = dataStore.retrieveAll();
        Assert.assertEquals( "Unexpected user count", 2, users.size() );
        for( AccountRequest tmp : users ){
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
        
        // test that the data store's password + confirm -> hash is working
        
        user = new AccountRequest();
        user.setName( "Thomas Galvin" );
        user.setSortByName( "Galvin, Thomas" );
        user.setDisplayName( "Thomas" );
        user.setTitle( "Field Marshall of the Fell Hordes of the Dying Light" );
        user.setEnabled( true );
        user.setCredential( "12345-abcde" );
        user.setLoginName( "tgalvin" );
        user.setPasswordHash( null );
        user.setPassword( "password" );
        user.setConfirmPassword( "password" );
        
        contactInfo = new ContactInfo();
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
        
        uuid = dataStore.store( user );
        
        loaded = dataStore.retrieve( uuid );
        String originalHash = user.getPasswordHash();
        String loadedHash = loaded.getPasswordHash();
        user.setPasswordHash( null );
        loaded.setPasswordHash( null );
        Assert.assertEquals( "Loaded user did not match original", user, loaded );
        Assert.assertEquals( "Loaded hash did not match original", originalHash, loadedHash );
        
        user = dataStore.retrieve( uuid );
        user.setTitle( "Indellible master of the Creeping Darkness" );
        dataStore.store( user );
        loaded = dataStore.retrieve( uuid );
        Assert.assertEquals( "Loaded user did not match original", user, loaded );
        
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
}
