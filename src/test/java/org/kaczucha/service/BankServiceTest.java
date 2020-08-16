package org.kaczucha.service;


import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kaczucha.Client;
import org.kaczucha.repository.InMemoryClientRepository;

import java.lang.ref.SoftReference;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

public class BankServiceTest {
    private BankService service;
    private List<Client> clients;


    @BeforeEach
    public void setup() {
        clients = new LinkedList<>();
        service = new BankService(new InMemoryClientRepository(clients));
    }

    @Test
    public void transfer_allParamsOk_fundsTransferred() {
        //given
        final String emailFrom = "a@a.pl";
        final String emailTo = "b@b.pl";
        final Client clientFrom = new Client("Alek", emailFrom, 1000);
        final Client clientTo = new Client("Bartek", emailTo, 500);
        clients.add(clientFrom);
        clients.add(clientTo);
        final double amount = 100;
        //when
        service.transfer(emailFrom, emailTo, amount);
        //then
        final Client actualFromClient = service.findByEmail(emailFrom);
        final Client actualToClient = service.findByEmail(emailTo);
        final Client expectedClientFrom = new Client("Alek", emailFrom, 900);
        final Client expectedClientTo = new Client("Bartek", emailTo, 600);


        final SoftAssertions softAssertions = new SoftAssertions();
        softAssertions
                .assertThat(expectedClientFrom)
                .isEqualTo(actualFromClient);
        softAssertions
                .assertThat(expectedClientTo)
                .isEqualTo(actualToClient);
        softAssertions.assertAll();
    }

    @Test
    public void transfer_amountWithDot_allParamsOk_fundsTransferred() {
        //given
        final String emailFrom = "a@a.pl";
        final String emailTo = "b@b.pl";
        final Client clientFrom = new Client("Alek", emailFrom, 1000);
        final Client clientTo = new Client("Bartek", emailTo, 500);
        clients.add(clientFrom);
        clients.add(clientTo);
        final double amount = 100.5;
        //when
        service.transfer(emailFrom, emailTo, amount);
        //then
        final Client actualFromClient = service.findByEmail(emailFrom);
        final Client actualToClient = service.findByEmail(emailTo);
        final Client expectedClientFrom = new Client("Alek", emailFrom, 899.5);
        final Client expectedClientTo = new Client("Bartek", emailTo, 600.5);


        final SoftAssertions softAssertions = new SoftAssertions();
        softAssertions
                .assertThat(expectedClientFrom)
                .isEqualTo(actualFromClient);
        softAssertions
                .assertThat(expectedClientTo)
                .isEqualTo(actualToClient);
        softAssertions.assertAll();
    }

    @Test
    public void transfer_allFounds_fundsTransferred() {
        // given
        final String emailFrom = "a@a.pl";
        final String emailTo = "b@b.pl";
        final Client clientFrom = new Client("Alek", emailFrom, 1000);
        final Client clientTo = new Client("Bartek", emailTo, 500);
        clients.add(clientFrom);
        clients.add(clientTo);
        final double amount = 1000;
        // when
        service.transfer(emailFrom, emailTo, amount);
        // then
        final Client actualFromClient = service.findByEmail(emailFrom);
        final Client actualToClient = service.findByEmail(emailTo);
        final Client expectedClientFrom = new Client("Alek", emailFrom, 0);
        final Client expectedClientTo = new Client("Bartek", emailTo, 1500);

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions
                .assertThat(expectedClientFrom)
                .isEqualTo(actualFromClient);
        softAssertions
                .assertThat(expectedClientTo)
                .isEqualTo(actualToClient);
        softAssertions.assertAll();
    }

    @Test
    public void transfer_notEnoughFunds_thrownNoSufficientFundsException() {
        // given
        final String emailFrom = "a@a.pl";
        final String emailTo = "b@b.pl";
        final Client clientFrom = new Client("Alek", emailFrom, 100);
        final Client clientTo = new Client("Bartek", emailTo, 500);
        clients.add(clientFrom);
        clients.add(clientTo);
        final double amount = 1000;
        // when/then
        Assertions.assertThrows(
                NoSufficientFundsException.class,
                () -> service.transfer(emailFrom, emailTo, amount)
        );
    }

    @Test
    public void transfer_negativeAmount_thrownIllegalArgumentException() {
        // given
        final String emailFrom = "a@a.pl";
        final String emailTo = "b@b.pl";
        final Client clientFrom = new Client("Alek", emailFrom, 100);
        final Client clientTo = new Client("Bartek", emailTo, 500);
        clients.add(clientFrom);
        clients.add(clientTo);
        final double amount = -1000;
        // when/then
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.transfer(emailFrom, emailTo, amount)
        );
    }

    @Test
    public void transfer_zero_thrownIllegalArgumentException() {
        // given
        final String emailFrom = "a@a.pl";
        final String emailTo = "b@b.pl";
        final Client clientFrom = new Client("Alek", emailFrom, 100);
        final Client clientTo = new Client("Bartek", emailTo, 500);
        clients.add(clientFrom);
        clients.add(clientTo);
        final double amount = 0;
        // when/then
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.transfer(emailFrom, emailTo, amount)
        );
    }

    @Test
    public void transfer_amount_to_yourself_thrownIllegalArgumentException() {
        // given
        final String emailFrom = "a@a.pl";
        final Client clientFrom = new Client("Alek", emailFrom, 100);
        clients.add(clientFrom);
        final double amount = 100;
        // when/then
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.transfer(emailFrom, emailFrom, amount)
        );
    }

    @Test
    public void transfer_amount_between_clientsWithTheSameEmail_thrownIllegalArgumentException() {
        // given
        final String emailFrom = "a@a.pl";
        final String emailTo = "a@a.pl";
        final Client clientFrom = new Client("Alek", emailFrom, 100);
        final Client clientTo = new Client("Bartek", emailTo, 500);
        clients.add(clientFrom);
        clients.add(clientTo);
        final double amount = 50;
        // when/then
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.transfer(emailFrom, emailTo, amount)
        );
    }


    @Test
    public void transfer_negativeWithDotAmount_thrownIllegalArgumentException() {
        // given
        final String emailFrom = "a@a.pl";
        final String emailTo = "b@b.pl";
        final Client clientFrom = new Client("Alek", emailFrom, 20);
        final Client clientTo = new Client("Bartek", emailTo, 500);
        clients.add(clientFrom);
        clients.add(clientTo);
        final double amount = -10.1;
        // when/then
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.transfer(emailFrom, emailTo, amount)
        );


    }

    @Test
    public void withdraw_correctAmount_balanceChangedCorrectly() {
        //given
        final String email = "a@a.pl";
        final Client client = new Client("Alek", email, 100);
        clients.add(client);
        //when
        service.withdraw(email, 50);
//then
        Client expectedClient = new Client("Alek", email, 50);
        Assertions.assertTrue(clients.contains((expectedClient)));
    }

    @Test
    public void withdraw_allBalance_balanceSetToZero() {
        //given
        final String email = "a@a.pl";
        final Client client = new Client("Alek", email, 100);
        clients.add(client);
        //when
        service.withdraw(email, 100);
//then
        Client expectedClient = new Client("Alek", email, 0);
        Assertions.assertTrue(clients.contains((expectedClient)));
    }

    @Test
    public void withdraw_negativeAmount_throwIllegalArgumentException() {
        //given
        final String email = "a@a.pl";
        final Client client = new Client("Alek", email, 100);
        clients.add(client);
        //when
        final int amount = -100;
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.withdraw(email, amount)
        );
    }

    @Test
    public void withdraw_zeroAmount_throwIllegalArgumentException() {
        //given
        final String email = "a@a.pl";
        final Client client = new Client("Alek", email, 100);
        clients.add(client);
        //when
        final int amount = 0;
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.withdraw(email, amount)
        );
    }

    @Test
    public void withdraw_amountBiggerThanBalance_throwsNoSufficientFundsException() {
        //given
        final String email = "a@a.pl";
        final Client client = new Client("Alek", email, 100);
        clients.add(client);
        //when
        final int amount = 1000;
        Assertions.assertThrows(
                NoSufficientFundsException.class,
                () -> service.withdraw(email, amount)
        );
    }

    @Test
    public void withdraw_icorrectRmail_throwsIllegalArgumentException() {
        //given
        final String email = "incorrect.email@a.pl";
        final int amount = 1000;
        //when/then
        Assertions.assertThrows(
                NoSuchElementException.class,
                ()-> service.withdraw(email,amount)
        );

    }
    @Test
    public void withdraw_upperCaseEmail_balanceChangedCorrectly() {
        //given
        final String email = "A@A.pl";
        final Client client = new Client("Alek", "a@a.pl", 100);
        clients.add(client);
        //when
        service.withdraw(email, 50);
        //then
        Client expectedClient = new Client("Alek", "a@a.pl", 50);
        Assertions.assertTrue(clients.contains((expectedClient)));
    }
    @Test
    public void withdraw_nullEmail_throwIllegalArgumentException() {
        //given
        final String email = null;
        final int amount = 1000;
        //when/then

        Assertions.assertThrows(
                IllegalArgumentException.class,
                ()-> service.withdraw(email,amount)
        );
    }


    @Test
    public void deleteClientWithCashInBankAccount_throwIllegalArgumentException() {
        //given
        final String email = "a@a.pl";
        final Client client = new Client("Alek", email, 100);
        clients.add(client);
        clients.remove(client);
        //when/then
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.delete(client));
    }

    @Test
    public void deleteClientWithNullEmail_throwIllegalArgumentException() {
        //given
        final String email = null;
        final Client client = new Client("Alek", email, 0);
        clients.add(client);
        clients.remove(client);
        //when/then
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.delete(client));
    }

    @Test
    public void deleteClientWithicorrectEmail_throwsNoSuchElementEx() {
        //given
        final String email = "incorrect.email@a.pl";
        final Client client = new Client("Alek", email, 0);
        clients.add(client);
        clients.remove(client);

        //when/then
        Assertions.assertThrows(
                NoSuchElementException.class,
                ()-> service.delete(client)
        );

    }
}


//    @Test
//    public void transfer_toSameClient_thrownException() {
//        //given
//        final String email = "a@a.pl";
//        final Client client = new Client("Alek", email, 100);
//        clients.add(client);
//        // when/then
//        Assertions.assertThrows(
//                IllegalArgumentException.class,
//                () -> service.transfer(email, email, 10)
//        );
//
//    }
//
//    @Test
//    public void withdraw_correctAmount_balanceChangedCorrectly() {
//        //given
//        final String email = "a@a.pl";
//        final Client client = new Client("Alek", email, 100);
//        clients.add(client);
//        //when
//        service.withdraw(email, 50);
//        //then
//        Client expectedClient = new Client("Alek", email, 50);
//        final Client actualClient = clients.get(0);
//        Assertions.assertEquals(expectedClient, actualClient);
//    }
//
//    @Test
//    public void withdraw_correctFloatingPointAmount_balanceChangedCorrectly() {
//        //given
//        final String email = "a@a.pl";
//        final Client client = new Client("Alek", email, 100);
//        clients.add(client);
//        //when
//        service.withdraw(email, 50.5);
//        //then
//        Client expectedClient = new Client("Alek", email, 49.5);
//        final Client actualClient = clients.get(0);
//        Assertions.assertEquals(expectedClient, actualClient);
//    }
//
//    @Test
//    public void withdraw_allBalance_balanceSetToZero() {
//        //given
//        final String email = "a@a.pl";
//        final Client client = new Client("Alek", email, 100);
//        clients.add(client);
//        service.withdraw(email, 100);
//        //when
//        //then
//        Client expectedClient = new Client("Alek", email, 0);
//        final Client actualClient = clients.get(0);
//        Assertions.assertEquals(expectedClient, actualClient);
//    }
//
//    @Test
//    public void withdraw_negativeAmount_throwsIllegalArgumentException() {
//        //given
//        final String email = "a@a.pl";
//        final Client client = new Client("Alek", email, 100);
//        clients.add(client);
//        final int amount = -100;
//        //when
//        Assertions.assertThrows(
//                IllegalArgumentException.class,
//                () -> service.withdraw(email, amount)
//        );
//    }
//
//    @Test
//    public void withdraw_zeroAmount_throwsIllegalArgumentException() {
//        //given
//        final String email = "a@a.pl";
//        final Client client = new Client("Alek", email, 100);
//        clients.add(client);
//        final int amount = 0;
//        //when
//        Assertions.assertThrows(
//                IllegalArgumentException.class,
//                () -> service.withdraw(email, amount)
//        );
//    }
//
//    @Test
//    public void withdraw_amountBiggerThenBalance_throwsNoSufficientFundsException() {
//        //given
//        final String email = "a@a.pl";
//        final Client client = new Client("Alek", email, 100);
//        clients.add(client);
//        final int amount = 1000;
//        //when
//        Assertions.assertThrows(
//                NoSufficientFundsException.class,
//                () -> service.withdraw(email, amount)
//        );
//    }
//
//    @Test
//    public void withdraw_incorrectEmail_throwsNoSuchElementException() {
//        //given
//        final String email = "incorrect_email@a.pl";
//        final int amount = 1000;
//        //when/then
//        Assertions.assertThrows(
//                NoSuchElementException.class,
//                () -> service.withdraw(email, amount)
//        );
//    }
//
//
//    @Test
//    public void withdraw_upperCaseEmail_balanceChangedCorrectly() {
//        //given
//        final String email = "A@A.PL";
//        final Client client = new Client("Alek", "a@a.pl", 100);
//        clients.add(client);
//        //when
//        service.withdraw(email, 50);
//        //then
//        Client expectedClient = new Client("Alek", "a@a.pl", 50);
//        final Client actualClient = clients.get(0);
//        Assertions.assertEquals(expectedClient, actualClient);
//    }
//
//    @Test
//    public void withdraw_nullEmail_throwsIllegalArgumentException() {
//        //given
//        final String email = null;
//        final int amount = 1000;
//        //when/then
//        Assertions.assertThrows(
//                IllegalArgumentException.class,
//                () -> service.withdraw(email, amount)
//        );
//    }


