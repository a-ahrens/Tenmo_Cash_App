package com.techelevator.dao;

import com.techelevator.tenmo.Exceptions.Account.CreateAccountFailException;

import com.techelevator.tenmo.Exceptions.User.InvalidUserException;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcUserDao;

import com.techelevator.tenmo.model.Account.Account;
import com.techelevator.tenmo.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

    public class JdbcAccountDaoTests extends BaseDaoTests {


        private static final User USER_1 = new User(1001, "user1", "user1", "USER");
        private static final User USER_2 = new User(1001, "user1", "user1", "USER");
        private static final User USER_3 = new User(1001, "user1", "user1", "USER");

        private static final Account ACCOUNT_1 = new Account(2001, 1001, BigDecimal.valueOf(1000.00));
        private static final Account ACCOUNT_2 = new Account(2002, 1002, BigDecimal.valueOf(500.00));
        private static final Account ACCOUNT_3 = new Account(2003, 1003, BigDecimal.valueOf(1500.00));

        private JdbcAccountDao sut;
        private Account testAccount;

        @Before
        public void setup() {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            sut = new JdbcAccountDao(jdbcTemplate);
            testAccount = new Account(2000, 1000, BigDecimal.valueOf(1000));
        }

        //Test if account was created
        //Test if account is null
        //Test if accountId is used


        @Test
        public void account_not_created_returns_null() {
            Account createdAccount = new Account(2000, 1001, BigDecimal.valueOf(1000));
            Assert.assertNull(createdAccount);

        }
        @Test
        public void getAccount_returns_null_when_accountId_not_found() {
            Account account = sut.getAccountByAccountId(99);
            Assert.assertNull("getAccount failed to return null for id not in database", account);
        }

        @Test
        public void find_account_with_accountId() {
            Account account = sut.getAccountByAccountId(2001);
            assertAccountsMatch("getAccount returned wrong or partial data", ACCOUNT_1, account);
            account = sut.getAccountByAccountId(2002);
            assertAccountsMatch("getAccount returned wrong or partial data", ACCOUNT_2, account);
            account = sut.getAccountByAccountId(2003);
            assertAccountsMatch("getAccount returned wrong or partial data", ACCOUNT_3, account);

        }

        //Test if userId is found
        //Test if associated account is null

        @Test
        public void find_account_with_userId() {
            Account account = sut.getAccountByUserId(1001);
            assertAccountsMatch("getAccount returned wrong or partial data", ACCOUNT_1, account);
            account = sut.getAccountByAccountId(1002);
            assertAccountsMatch("getAccount returned wrong or partial data", ACCOUNT_2, account);
            account = sut.getAccountByAccountId(1003);
            assertAccountsMatch("getAccount returned wrong or partial data", ACCOUNT_3, account);

        }

        @Test
        public void getAccount_returns_null_when_userId_not_found() {
            Account account = sut.getAccountByUserId(99);
            Assert.assertNull("getAccount failed to return null for id not in database", account);
        }

        //Test if correct balance is returned
        @Test
        public void created_account_has_balance(){
            BigDecimal balance= sut.getBalance(2001);
            Assert.assertTrue(sut.getBalance(2001).compareTo(new BigDecimal(1000))== 0);

        }

        @Test
        public void get_balance_with_accountId() {
            BigDecimal balanceExpected = sut.getBalance(2001);
            Assert.assertEquals(balanceExpected, sut.getBalance(2001));

        }

        //Test if balance is returned
        //Test if updated.Account.getBalance is returned

        @Test
        public void add_to_balance_shows_updated_balance() {
            Account updatedAccount = sut.getAccountByAccountId(2002);

            updatedAccount.setAccountId(2000);
            updatedAccount.setUserId(1000);
            updatedAccount.setBalance(BigDecimal.valueOf(99));

            sut.getBalance(2000);

            Account retrievedAccount = sut.getAccountByAccountId(2002);
            assertAccountsMatch("Balance has expected values when retrieved", updatedAccount, retrievedAccount);

        }


        //Test if balance is returned
        //Test if balance is null

        @Test
        public void subtract_from_balance_shows_updated_balance(){
            Account updatedAccount = sut.getAccountByAccountId(2002);

            updatedAccount.setAccountId(2000);
            updatedAccount.setUserId(1000);
            updatedAccount.setBalance(BigDecimal.valueOf(99));

            sut.getBalance(2000);

            Account retrievedAccount = sut.getAccountByAccountId(2002);
            assertAccountsMatch("Balance has expected values when retrieved", updatedAccount, retrievedAccount);

        }

        @Test
        public void no_balance_returns_null(){
            User createdUser = new User(1000, "user0", "user", "USER");
            Assert.assertNull(createdUser);
        }


        private void assertAccountsMatch(String s, Account expected, Account actual) {
            Assert.assertEquals(expected.getAccountId(), actual.getAccountId());
            Assert.assertEquals(expected.getUserId(), actual.getUserId());
            Assert.assertEquals(expected.getBalance(), actual.getBalance());

        }


    }


//    @Test(expected = AccountNotFoundException.class)
//    public void account_not_found() {
//        sut.getAccountByUserId("Account not found.");
//
//    }


