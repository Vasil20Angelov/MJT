package bg.sofia.uni.fmi.mjt.mail;

import bg.sofia.uni.fmi.mjt.mail.exceptions.FolderAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.FolderNotFoundException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.InvalidPathException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.RuleAlreadyDefinedException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccountFolderTest {

    @Test
    public void testAddFolderThrowsWhenTheGivenPathDoesNotStartFromTheInboxFolder() {
        AccountFolder accountFolder = new AccountFolder(new Account("ac1", "ac"));

        assertThrows(InvalidPathException.class, () -> accountFolder.addFolder("/other/folder"),
                "addFolder must throw InvalidPathException " +
                        "when the given folder path does not start with /inbox");
    }

    @Test
    public void testAddFolderThrowsWhenTheGivenPathIsTheSameAsExistingFolder() {
        AccountFolder accountFolder = new AccountFolder(new Account("ac1", "ac"));

        assertThrows(FolderAlreadyExistsException.class, () -> accountFolder.addFolder("/inbox"),
                "addFolder must throw FolderAlreadyExistsException " +
                        "when the given folder path is the same as already existing one");
    }

    @Test
    public void testAddFolderThrowsWhenTheGivenPathDoesNotContainPathSeparator() {
        AccountFolder accountFolder = new AccountFolder(new Account("ac1", "ac"));

        assertThrows(InvalidPathException.class, () -> accountFolder.addFolder("/inboxs"),
                "addFolder must throw InvalidPathException " +
                        "when the given folder path does not have path separator after /inbox");
    }

    @Test
    public void testAddFolderThrowsWhenTheGivenPathDoesNotHaveAnyCharsBetweenTwoPathSeparators() {
        AccountFolder accountFolder = new AccountFolder(new Account("ac1", "ac"));

        assertThrows(InvalidPathException.class, () -> accountFolder.addFolder("/inbox//"),
                "addFolder must throw InvalidPathException " +
                        "when the given folder path does not contain any chars between 2 path separators");
    }

    @Test
    public void testAddFolderThrowsWhenTheGivenPathEndsWithPathSeparators() {
        AccountFolder accountFolder = new AccountFolder(new Account("ac1", "ac"));

        assertThrows(InvalidPathException.class, () -> accountFolder.addFolder("/inbox/"),
                "addFolder must throw InvalidPathException " +
                        "when the given folder path ends with a path separator");
    }

    @Test
    public void testAddFolderThrowsWhenTheGivenPathContainsMissingIntermediateFolders() {
        AccountFolder accountFolder = new AccountFolder(new Account("ac1", "ac"));

        assertThrows(InvalidPathException.class, () -> accountFolder.addFolder("/inbox/mjt/izpit"),
                "addFolder must throw InvalidPathException " +
                        "when the given folder path contains intermediate folders that do not exist");
    }

    @Test
    public void testAddFolderInsertsNewFolderWhenTheGivenPathIsValid() {
        AccountFolder accountFolder = new AccountFolder(new Account("ac1", "ac"));
        accountFolder.addFolder("/inbox/mjt");

        assertTrue(accountFolder.getFolderNames().contains("/inbox/mjt"),
                "addFolder must correctly add a new folder when the given path satisfies all conditions");
    }

    @Test
    public void testGetMailsThrowsWhenTheGivenFolderDoesNotExists() {
        AccountFolder accountFolder = new AccountFolder(new Account("ac1", "ac"));

        assertThrows(FolderNotFoundException.class, () -> accountFolder.getMails("/inbox/mjt"),
                "getMails must throw FolderNotFoundException " +
                        "when the given folder path does not exist");
    }

    @Test
    public void testAddMailSendsTheMailToTheSentFolderWhenTheSenderIsTheSameAsTheAccountHolder() {
        Account holder = new Account("ac1", "ac");
        AccountFolder accountFolder = new AccountFolder(holder);

        Mail mail = new Mail(holder, null, null, null, null);
        accountFolder.addMail(mail);

        assertTrue(accountFolder.getMails(AccountFolder.SENT_FOLDER).contains(mail),
                "addMail must send the mails to SENT_FOLDER when the recipient is the same as the sender");
    }

    @Test
    public void testAddMailSendsTheMailToTheInboxFolderWhenThereAreNotExistingRules() {
        Account holder = new Account("ac1", "ac");
        AccountFolder accountFolder = new AccountFolder(holder);

        Account other = new Account("other.gmail", "other");
        Mail mail = new Mail(other, null, null, null, null);
        accountFolder.addMail(mail);

        assertTrue(accountFolder.getMails(AccountFolder.INBOX_FOLDER).contains(mail),
                "addMail must send the mails to INBOX_FOLDER when there are not existing matching rules");
    }

    @Test
    public void testAddMailSendsTheMailToFolderWhenTheMailMatchesAnyExistingRule() {
        Account holder = new Account("ac1", "ac");
        AccountFolder accountFolder = new AccountFolder(holder);

        accountFolder.addFolder("/inbox/FMI");
        accountFolder.addFolder("/inbox/FMI_HW");

        accountFolder.addRule("subject-includes: mjt", "/inbox/FMI", 4);
        accountFolder.addRule("subject-includes: HW", "/inbox/FMI_HW", 4);

        Account other = new Account("other.gmail", "other");
        Mail mail = new Mail(other, null, "mjt", null, null);
        accountFolder.addMail(mail);

        assertTrue(accountFolder.getMails("/inbox/FMI").contains(mail),
                "addMail must send the mails to a folder according to a matching rule");
    }

    @Test
    public void testAddRuleThrowsWhenTheGivenFolderDoesNotExists() {
        AccountFolder accountFolder = new AccountFolder(new Account("ac1", "ac"));

        assertThrows(FolderNotFoundException.class,
                () -> accountFolder.addRule(null, "/inbox/mjt", 4),
                "addRule must throw FolderNotFoundException " +
                        "when the given folder path does not exist");
    }

    @Test
    public void testAddRuleAddsNewRule() {
        AccountFolder accountFolder = new AccountFolder(new Account("ac1", "ac"));
        Rule rule = new Rule("", "/inbox", 4);

        accountFolder.addRule("", "/inbox", 4);

        assertTrue(accountFolder.getRules().contains(rule),
                "addRule must add a new rule when it is a valid one");
    }

    @Test
    public void testAddRuleDoesNothingWhenItFindsConflictingRule() {
        AccountFolder accountFolder = new AccountFolder(new Account("ac1", "ac"));
        accountFolder.addFolder("/inbox/fmi");

        Rule rule1 = new Rule("subject-includes: mjt", "/inbox", 4);

        accountFolder.addRule("subject-includes: mjt", "/inbox", 4);
        accountFolder.addRule("subject-includes: mjt", "/inbox/fmi", 4);

        assertEquals(1, accountFolder.getRules().size(),
                "addRule must not include conflicting rules");

        assertTrue(accountFolder.getRules().contains(rule1),
                "addRule must not change already added rules");
    }

    @Test
    public void testAddRuleThrowsWhenTryingToAddRuleDefinitionThatAlreadyExists() {
        AccountFolder accountFolder = new AccountFolder(new Account("ac1", "ac"));

        String ruleDefinition = """
                subject-includes: mjt
                subject-includes: izpit""";

        assertThrows(RuleAlreadyDefinedException.class,
                () -> accountFolder.addRule(ruleDefinition, "/inbox", 4),
                "addRule must throw RuleAlreadyDefinedException when trying to add rule " +
                        "which rule definition contains duplicate conditions");
    }

    @Test
    public void testAddRuleMovesMailsFromInbox() {
        AccountFolder accountFolder = new AccountFolder(new Account("ac1", "ac"));
        String folderPath = "/inbox/Homeworks";
        accountFolder.addFolder(folderPath);

        Account sender = new Account("sender.gmail", "sender");
        Mail mail = new Mail(sender, null, "mjt", "HW1", null);
        accountFolder.addMail(mail);

        String ruleDefinition = """
                subject-includes: mjt
                subject-or-body-includes: HW1
                """;
        accountFolder.addRule(ruleDefinition, folderPath, 4);

        assertTrue(accountFolder.getMails(folderPath).contains(mail),
                "addRule must move mails from inbox folder that match the rule conditions!");
    }

    @Test
    public void testAddRuleDoesNotMoveMailsFromInboxWhenTheyDoNotMatchRuleConditions() {
        AccountFolder accountFolder = new AccountFolder(new Account("ac1", "ac"));
        String folderPath = "/inbox/Homeworks";
        accountFolder.addFolder(folderPath);

        Account sender = new Account("sender.gmail", "sender");
        Mail mail = new Mail(sender, null, "mjt", "Project", null);
        accountFolder.addMail(mail);

        String ruleDefinition = """
                subject-includes: mjt
                subject-or-body-includes: HW1
                """;
        accountFolder.addRule(ruleDefinition, folderPath, 4);

        assertTrue(accountFolder.getMails(AccountFolder.INBOX_FOLDER).contains(mail),
                "addRule must not move mails from inbox folder that does not match the rule conditions!");
    }
}
