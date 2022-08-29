package com.techelevator.dao;


import com.techelevator.tenmo.Exceptions.Account.CreateAccountFailException;

import com.techelevator.tenmo.Exceptions.User.InvalidUserException;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.Account.Account;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserAccount;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class JdbcUserDaoTests extends BaseDaoTests {


    private static final User USER_1 = new User(1001, "user1", "user1", "USER");
    private static final User USER_2 = new User(1002, "user2", "user2", "USER");
    private static final User USER_3 = new User(1003, "user3", "user3", "USER");


    private JdbcUserDao sut;


    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcUserDao(jdbcTemplate);


    }


    // Test if username is returned
    // Test returns empty if not found

    @Test
    public void find_username_with_userId() {
        int userId = sut.findIdByUsername(USER_1.getUsername());
        Assert.assertEquals(USER_1.getId(), userId);
    }

    @Test
    public void getusername_returns_null_when_id_not_found() {
        User username = sut.findByUsername("User1");
        Assert.assertNull(username);
    }


    // Test if code returns userId in list
    //Test to make sure method includes logged in user
    //Test to make sure user list is not null/ empty


    @Test
    public void find_all_users_including_userId() {
        List<UserAccount> users = sut.findAll();
        Assert.assertNotNull(users);
        Assert.assertEquals(3, users.size());
        Assert.assertEquals(USER_1, users.get(0));
        Assert.assertEquals(USER_2, users.get(1));
        Assert.assertEquals(USER_3, users.get(2));
    }


    //Test if method prints list of users
    //Test to make sure list does not include logged in user
    //Need to throw an exception if no other users are found

    // TODO  @Test
    //  User loggedInUser=sut.findOtherUsers()


//Test to ensure correct username was returned
    //Test if username was not found, exception expected

    @Test(expected = UsernameNotFoundException.class)
    public void user_not_found() {
        sut.findByUsername("User not found.");

    }

    @Test
    public void matching_username_was_returned() {
        User user = sut.findByUsername(USER_1.getUsername());
        Assert.assertEquals(USER_1, user);


    }
    @Test(expected = InvalidUserException.class)
    public void invalid_user_input() {
        sut.findByUsername("username");


        //Test to see if new user is returned
        //Test to see if starting balance is 1000
        //need a new test method for testing account creation, format for create method
        //is username, password
        //test exceptions for user creation
    }
    @Test
    public void create_user_created_newUser() {
        User createdUser = new User(1000, "user0", "user", "USER");
        boolean createdNewUser = sut.create(createdUser.getUsername(), createdUser.getPassword());
        Assert.assertTrue(createdNewUser);
        Assert.assertNotNull("createUser returned null", createdUser);

    }

    @Test
    public void user_not_created_returns_null(){
        User createdUser = new User(1000, "user0", "user", "USER");
        Assert.assertNull(createdUser);
    }



    @Test(expected = CreateAccountFailException.class)
    public void create_account_fails_throws_exception() {
        sut.create("user", "user1");



    }


    @Test
    public void createNewUser() {
        boolean userCreated = sut.create("TEST_USER","test_password");
        Assert.assertTrue(userCreated);
        User user = sut.findByUsername("TEST_USER");
        Assert.assertEquals("TEST_USER", user.getUsername());
    }

}
