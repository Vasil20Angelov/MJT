package bg.sofia.uni.fmi.mjt.mail;

import bg.sofia.uni.fmi.mjt.mail.exceptions.AccountAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.AccountNotFoundException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.FolderAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.FolderNotFoundException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.InvalidPathException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.RuleAlreadyDefinedException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class OutlookTest {

    public static final Account SAMPLE_ACCOUNT = new Account("account1.mail", "account1");
    public static final String SAMPLE_MAIL_METADATA = """
            sender: testy@gmail.com
            subject: Hello, MJT!
            recipients: pesho@gmail.com, gosho@gmail.com,
            received: 2022-12-08 14:14""";

    public Outlook getSampleOutlook() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount(SAMPLE_ACCOUNT.name(), SAMPLE_ACCOUNT.emailAddress());

        return outlook;
    }

    public Mail getSampleMail() {
        Account sender = new Account("testy@gmail.com", "testy");
        Set<String> recipients = new HashSet<>(List.of("pesho@gmail.com", "gosho@gmail.com"));
        String subject = "Hello, MJT!";
        String body = "Some info about the exam.";
        LocalDateTime dateTime = LocalDateTime.of(2022,12,8, 14,14);

        return new Mail(sender, recipients, subject, body, dateTime);
    }

    @Test
    public void testAddNewAccountThrowsWhenTheAccountNameIsNull() {
        Outlook outlook = new Outlook();
        assertThrows(IllegalArgumentException.class, () -> outlook.addNewAccount(null, "acc.mail"),
                "addNewAccount must throw IllegalArgumentException when the account name is null!");
    }

    @Test
    public void testAddNewAccountThrowsWhenTheAccountEmailIsNull() {
        Outlook outlook = new Outlook();
        assertThrows(IllegalArgumentException.class, () -> outlook.addNewAccount("acc", null),
                "addNewAccount must throw IllegalArgumentException when the account email is null!");
    }

    @Test
    public void testAddNewAccountReturnsAnAccountWithTheGivenNameAndEmail() {
        Outlook outlook = new Outlook();
        String name = "acc";
        String email = "acc.mail";

        Account expected = new Account(email, name);
        Account result = outlook.addNewAccount(name, email);

        assertEquals(expected, result, "Expected result: account with the same name and email as the given!");
    }

    @Test
    public void testAddNewAccountThrowsWhenTryingToAddAnAccountWithAlreadyExistingName() {
        Outlook outlook = getSampleOutlook();

        assertThrows(AccountAlreadyExistsException.class,
                () -> outlook.addNewAccount(SAMPLE_ACCOUNT.name(), "other.mail"),
                "Expected AccountAlreadyExistsException to be thrown as there is already an account" +
                        "with the given name");
    }

    @Test
    public void testAddNewAccountThrowsWhenTryingToAddAnAccountWithAlreadyExistingEmail() {
        Outlook outlook = getSampleOutlook();

        assertThrows(AccountAlreadyExistsException.class,
                () -> outlook.addNewAccount("account2", SAMPLE_ACCOUNT.emailAddress()),
                "Expected AccountAlreadyExistsException to be thrown as there is already an account" +
                        "with the given email");
    }

    @Test
    public void testCreateFolderThrowsWhenTheGivenAccountNameIsNull() {
        Outlook outlook = new Outlook();
        assertThrows(IllegalArgumentException.class, () -> outlook.createFolder(null, "/inbox/fmi"),
                "createFolder must throw IllegalArgumentException when the account name is null!");
    }

    @Test
    public void testCreateFolderThrowsWhenTheGivenPathIsNull() {
        Outlook outlook = new Outlook();
        assertThrows(IllegalArgumentException.class, () -> outlook.createFolder("accName", null),
                "createFolder must throw IllegalArgumentException when the path is null!");
    }

    @Test
    public void testCreateFolderThrowsWhenThereIsNoAccountWithSuchNameRegistered() {
        Outlook outlook = new Outlook();
        assertThrows(AccountNotFoundException.class, () -> outlook.createFolder("acc", "inbox/fmi"),
                "createFolder must throw AccountNotFoundException when there is no account with that name!");
    }

    @Test
    public void testCreateFolderThrowsWhenThereIsAnExistingFolderWithThatPath() {
        Outlook outlook = getSampleOutlook();
        String path = "/inbox/fmi";
        outlook.createFolder(SAMPLE_ACCOUNT.name(), path);

        assertThrows(FolderAlreadyExistsException.class, () ->  outlook.createFolder(SAMPLE_ACCOUNT.name(), path),
                "createFolder must throw FolderAlreadyExistsException " +
                        "when there is existing folder with that path!");
    }

    @Test
    public void testAddFolderThrowsWhenTheGivenPathDoesNotStartFromTheInboxFolder() {
        Outlook outlook = getSampleOutlook();
        String path = "/otherFolder";

        assertThrows(InvalidPathException.class, () -> outlook.createFolder(SAMPLE_ACCOUNT.name(), path),
                "createFolder must throw InvalidPathException " +
                        "when the given folder path does not start with /inbox");
    }

    @Test
    public void testAddFolderThrowsWhenTheGivenPathContainsMissingIntermediateFolders() {
        Outlook outlook = getSampleOutlook();
        String path = "/inbox/mjt/izpit";

        assertThrows(InvalidPathException.class, () -> outlook.createFolder(SAMPLE_ACCOUNT.name(), path),
                "createFolder must throw InvalidPathException " +
                        "when the given folder path contains intermediate folders that do not exist");
    }

    @Test
    public void testAddRuleThrowsWhenTheGivenAccountNameIsNull() {
        Outlook outlook = new Outlook();
        assertThrows(IllegalArgumentException.class,
                () -> outlook.addRule(null, "/folder", "ruleDef", 4),
                "addRule must throw IllegalArgumentException when the accountName is null!");
    }

    @Test
    public void testAddRuleThrowsWhenTheGivenFolderPathIsNull() {
        Outlook outlook = new Outlook();
        assertThrows(IllegalArgumentException.class,
                () -> outlook.addRule("acc", null, "ruleDef", 4),
                "addRule must throw IllegalArgumentException when the folder path is null!");
    }

    @Test
    public void testAddRuleThrowsWhenTheGivenRuleDefinitionIsNull() {
        Outlook outlook = new Outlook();
        assertThrows(IllegalArgumentException.class,
                () -> outlook.addRule("acc", "/path", null, 4),
                "addRule must throw IllegalArgumentException when the rule definition is null!");
    }

    @Test
    public void testAddRuleThrowsWhenTheGivenAccountNameDoesNotExist() {
        Outlook outlook = new Outlook();
        assertThrows(AccountNotFoundException.class,
                () -> outlook.addRule("acc", "/path", "ruleDef", 4),
                "addRule must throw AccountNotFoundException when the given account does not exist!");
    }

    @Test
    public void testAddRuleThrowsWhenTheGivenFolderPathDoesNotExist() {
        Outlook outlook = getSampleOutlook();
        assertThrows(FolderNotFoundException.class,
                () -> outlook.addRule(SAMPLE_ACCOUNT.name(), "/inbox/fmi", "ruleDef", 4),
                "addRule must throw FolderNotFoundException when the given folder path does not exist!");
    }

    @Test
    public void testAddRuleThrowsWhenTheGivenPriorityIsOutOfBounds() {
        Outlook outlook = getSampleOutlook();

        assertThrows(IllegalArgumentException.class,
                () -> outlook.addRule(SAMPLE_ACCOUNT.name(), "/inbox", "ruleDef", -1),
                "addRule must throw IllegalArgumentException when priority is lower than the lower bound!");

        assertThrows(IllegalArgumentException.class,
                () -> outlook.addRule(SAMPLE_ACCOUNT.name(), "/inbox", "ruleDef", 11),
                "addRule must throw IllegalArgumentException when priority is higher than the upper bound!");
    }

    @Test
    public void testAddRuleThrowsWhenTheRuleDefinitionContainsAnyConditionMoreThanOnce() {
        Outlook outlook = getSampleOutlook();

        String ruleDefinition = """
                subject-includes: mjt, izpit, 2022
                subject-or-body-includes: izpit
                from: stoyo@fmi.bg
                subject-or-body-includes: izpit22
                """;

        assertThrows(RuleAlreadyDefinedException.class,
                () -> outlook.addRule(SAMPLE_ACCOUNT.name(), "/inbox", ruleDefinition, 5),
                "addRule must throw RuleAlreadyDefinedException " +
                        "when rule definition contains any condition more than once!");
    }

    @Test
    public void testReceiveMailThrowsWhenAnyOfTheParametersIsNull() {
        Outlook outlook = new Outlook();
        assertThrows(IllegalArgumentException.class,
                () -> outlook.receiveMail("acc", null, "123"),
                "receiveMail must throw IllegalArgumentException when any of the parameters is null!");
    }

    @Test
    public void testReceiveMailThrowsWhenTheGivenAccountNameDoesNotExist() {
        Outlook outlook = new Outlook();
        assertThrows(AccountNotFoundException.class,
                () -> outlook.receiveMail("acc", "metadata", "123"),
                "receiveMail must throw AccountNotFoundException when the given account does not exist!");
    }

    @Test
    public void testGetMailsFromFolderThrowsWhenTheAccountNameIsNull() {
        Outlook outlook = new Outlook();
        assertThrows(IllegalArgumentException.class,
                () -> outlook.getMailsFromFolder(null, "path"),
                "getMailsFromFolder must throw IllegalArgumentException when the accountName is null!");
    }

    @Test
    public void testGetMailsFromFolderThrowsWhenThePathIsNull() {
        Outlook outlook = new Outlook();
        assertThrows(IllegalArgumentException.class,
                () -> outlook.getMailsFromFolder("acc", null),
                "getMailsFromFolder must throw IllegalArgumentException when the path is null!");
    }

    @Test
    public void testGetMailsFromFolderThrowsWhenTheGivenAccountNameDoesNotExist() {
        Outlook outlook = new Outlook();
        assertThrows(AccountNotFoundException.class,
                () -> outlook.getMailsFromFolder("acc", "/path"),
                "getMailsFromFolder must throw AccountNotFoundException" +
                        " when the given account does not exist!");
    }

    @Test
    public void testGetMailsFromFolderThrowsWhenTheGivenFolderPathDoesNotExist() {
        Outlook outlook = getSampleOutlook();
        assertThrows(FolderNotFoundException.class,
                () -> outlook.getMailsFromFolder(SAMPLE_ACCOUNT.name(), "/path"),
                "getMailsFromFolder must throw FolderNotFoundException" +
                        " when the given folder path does not exist for the selected account!");
    }

    @Test
    public void testGetMailsFromFolderReturnsAnEmptyCollectionWhenThereAreNoMailsInTheSelectedFolder() {
        Outlook outlook = getSampleOutlook();
        assertTrue(outlook.getMailsFromFolder(SAMPLE_ACCOUNT.name(), AccountFolder.INBOX_FOLDER).isEmpty(),
                "The returned collection must be empty!");
    }

    @Test
    public void testReceiveMailAddsMailToTheInboxFolder() {
        Outlook outlook = getSampleOutlook();
        Account sender = getSampleMail().sender();
        outlook.addNewAccount(sender.name(), sender.emailAddress());
        outlook.receiveMail(SAMPLE_ACCOUNT.name(), SAMPLE_MAIL_METADATA, "Some info about the exam.");

        Object[] mails = outlook.getMailsFromFolder(SAMPLE_ACCOUNT.name(), AccountFolder.INBOX_FOLDER).toArray();

        assertEquals(1, outlook.getMailsFromFolder(SAMPLE_ACCOUNT.name(), AccountFolder.INBOX_FOLDER).size(),
                "The mail must be saved to the inbox folder");
        assertEquals(getSampleMail(), Array.get(mails, 0),
                "A correct mail must be saved!");
    }

    @Test
    public void testSendMailThrowsWhenTheAccountNameIsNull() {
        Outlook outlook = new Outlook();
        assertThrows(IllegalArgumentException.class,
                () -> outlook.sendMail(null, SAMPLE_MAIL_METADATA, "some text"),
                "sendMail must throw IllegalArgumentException when the accountName is null!");
    }

    @Test
    public void testSendMailThrowsWhenTheMailMetadataIsNull() {
        Outlook outlook = new Outlook();
        assertThrows(IllegalArgumentException.class,
                () -> outlook.sendMail("acc", null, "some text"),
                "sendMail must throw IllegalArgumentException when the mailMetadata is null!");
    }

    @Test
    public void testSendMailThrowsWhenTheMailContentIsNull() {
        Outlook outlook = new Outlook();
        assertThrows(IllegalArgumentException.class,
                () -> outlook.sendMail("acc", SAMPLE_MAIL_METADATA, null),
                "sendMail must throw IllegalArgumentException when the mailContent is null!");
    }

    @Test
    public void testSendMailThrowsWhenTheAccountNameDoesNotExist() {
        Outlook outlook = new Outlook();
        assertThrows(AccountNotFoundException.class,
                () -> outlook.sendMail("acc", SAMPLE_MAIL_METADATA, "some content"),
                "sendMail must throw AccountNotFoundException when the account name is not existing!");
    }

    @Test
    public void testSendMailSendsCorrectMessageWhenTheSenderFiledIsMissingFormTheMailMetadata() {
        String metadata = """
            subject: Hello, MJT!
            recipients: pesho@gmail.com, account1.mail,
            received: 2022-12-08 14:14""";

        Outlook outlook = getSampleOutlook();
        Account sender = getSampleMail().sender();
        outlook.addNewAccount(sender.name(), sender.emailAddress());

        outlook.sendMail(sender.name(), metadata, "Some info about the exam.");

        Object[] mails = outlook.getMailsFromFolder(SAMPLE_ACCOUNT.name(), AccountFolder.INBOX_FOLDER).toArray();

        assertEquals(1, outlook.getMailsFromFolder(SAMPLE_ACCOUNT.name(), AccountFolder.INBOX_FOLDER).size(),
                "The mail must be saved to the inbox folder");
        assertEquals(sender, ((Mail) Array.get(mails, 0)).sender(),
                "The sender should be filled automatically when missing in metadata");
    }

    @Test
    public void testSendMailSendsTheMailToAllRecipients() {
        Outlook outlook = new Outlook();

        Account recipient1 = new Account("gosho@gmail.com", "gosho");
        Account recipient2 = new Account("pesho@gmail.com", "pesho");
        Account sender = getSampleMail().sender();

        outlook.addNewAccount(sender.name(), sender.emailAddress());
        outlook.addNewAccount(recipient1.name(), recipient1.emailAddress());
        outlook.addNewAccount(recipient2.name(), recipient2.emailAddress());

        outlook.sendMail(sender.name(), SAMPLE_MAIL_METADATA, "Some info about the exam.");

        assertEquals(1, outlook.getMailsFromFolder(recipient1.name(), AccountFolder.INBOX_FOLDER).size(),
                "The mail is expected to be saved in the inbox folder for recipient 1");
        assertEquals(1, outlook.getMailsFromFolder(recipient2.name(), AccountFolder.INBOX_FOLDER).size(),
                "The mail is expected to be saved in the inbox folder for recipient 2");
        assertEquals(1, outlook.getMailsFromFolder(sender.name(), AccountFolder.SENT_FOLDER).size(),
                "The mail is expected to be saved in the sent folder for the sender");
    }

    @Test
    public void testAddRuleMovesMailsFromInboxFolder() {
        Outlook outlook = getSampleOutlook();
        Account sender = getSampleMail().sender();
        outlook.addNewAccount(sender.name(), sender.emailAddress());

        outlook.receiveMail(SAMPLE_ACCOUNT.name(), SAMPLE_MAIL_METADATA, "content1");
        outlook.receiveMail(SAMPLE_ACCOUNT.name(), SAMPLE_MAIL_METADATA, "content2");

        String folder = "/inbox/mjt";
        outlook.createFolder(SAMPLE_ACCOUNT.name(), folder);

        String ruleDefinition = "subject-or-body-includes: content2";
        outlook.addRule(SAMPLE_ACCOUNT.name(), folder, ruleDefinition, 5);
        Object[] mails = outlook.getMailsFromFolder(SAMPLE_ACCOUNT.name(), folder).toArray();

        assertEquals(1, outlook.getMailsFromFolder(SAMPLE_ACCOUNT.name(), AccountFolder.INBOX_FOLDER).size(),
                "Only 1 of the mails must stay in the /inbox folder!");
        assertEquals(1, outlook.getMailsFromFolder(SAMPLE_ACCOUNT.name(), folder).size(),
                "Only 1 of the mails must be moved to the folder from the rule definition!");
        assertEquals("content2", ((Mail)Array.get(mails, 0)).body(),
                "The mail which body is the same as in the rule definition should be moved!");
    }

    @Test
    public void testSendMailSendsTheMailToTheCorrectFolder() {
        Outlook outlook = new Outlook();

        Account recipient = new Account("gosho@gmail.com", "gosho");
        outlook.addNewAccount(recipient.name(), recipient.emailAddress());

        Account sender = getSampleMail().sender();
        outlook.addNewAccount(sender.name(), sender.emailAddress());

        String folder1 = "/inbox/mjt";
        outlook.createFolder(recipient.name(), folder1);

        String folder2 = "/inbox/mjt/izpit";
        outlook.createFolder(recipient.name(), folder2);

        String ruleDefinition1 = "subject-or-body-includes: content1";
        outlook.addRule(recipient.name(), folder1, ruleDefinition1, 5);

        String ruleDefinition2 = "subject-or-body-includes: content2";
        outlook.addRule(recipient.name(), folder2, ruleDefinition2, 5);

        outlook.sendMail(sender.name(), SAMPLE_MAIL_METADATA, "content2");

        assertEquals(1, outlook.getMailsFromFolder(recipient.name(), folder2).size(),
                "The mail must be send in the folder from the 2nd rule!");
    }

    @Test
    public void testSendMailAppliesTheRuleWithHigherPriority() {
        Outlook outlook = new Outlook();

        Account recipient = new Account("gosho@gmail.com", "gosho");
        outlook.addNewAccount(recipient.name(), recipient.emailAddress());

        Account sender = getSampleMail().sender();
        outlook.addNewAccount(sender.name(), sender.emailAddress());

        String folder1 = "/inbox/mjt";
        outlook.createFolder(recipient.name(), folder1);

        String folder2 = "/inbox/mjt/izpit";
        outlook.createFolder(recipient.name(), folder2);

        outlook.addRule(recipient.name(), folder1, "subject-or-body-includes: same content", 7);
        outlook.addRule(recipient.name(), folder2, "subject-or-body-includes: same content", 5);

        outlook.sendMail(sender.name(), SAMPLE_MAIL_METADATA, "same content");

        assertEquals(1, outlook.getMailsFromFolder(recipient.name(), folder1).size(),
                "Rule 1 must be apply as it has higher priority!");
    }
}
