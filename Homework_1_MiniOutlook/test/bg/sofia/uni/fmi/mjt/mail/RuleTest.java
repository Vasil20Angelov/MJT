package bg.sofia.uni.fmi.mjt.mail;

import bg.sofia.uni.fmi.mjt.mail.exceptions.RuleAlreadyDefinedException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class RuleTest {

    private static final String sampleRuleDefinition = """
                recipients-includes: pesho@gmail.com, gosho@gmail.com,
                subject-includes: mjt, izpit, 2022
                subject-or-body-includes: izpit, HW
                from: stoyo@fmi.bg
                """;

    private static final String sampleFolderPath = "/inbox/mjt";

    private static Rule getSampleRule() {
        List<String> subjects = new ArrayList<>(List.of("mjt", "izpit", "2022"));
        List<String> subjectsOrBody = new ArrayList<>(List.of("izpit", "HW"));
        List<String> recipients = new ArrayList<>(List.of("pesho@gmail.com", "gosho@gmail.com"));
        String senderEmail = "stoyo@fmi.bg";
        int priority = 8;

        return new Rule(subjects, subjectsOrBody, recipients, senderEmail, sampleFolderPath, priority);
    }

    @Test
    public void testCreationOfRule() {
        int priority = 8;

        Rule result = new Rule(sampleRuleDefinition, sampleFolderPath, priority);

        assertEquals(getSampleRule(), result, "Wrong parsing of ruleDefinition!");
    }

    @Test
    public void testCreationOfRuleThrowsWhenThePriorityIsHigherThanTheUpperBound() {
        int priority = 11;
        assertThrows(IllegalArgumentException.class, () -> new Rule(sampleRuleDefinition, sampleFolderPath, priority),
                "Expected IllegalArgumentException to be thrown " +
                        "as the given priority is higher than it should be!");
    }

    @Test
    public void testCreationOfRuleThrowsWhenThePriorityIsLowerThanTheUpperBound() {
        int priority = -1;
        assertThrows(IllegalArgumentException.class, () -> new Rule(sampleRuleDefinition, sampleFolderPath, priority),
                "Expected IllegalArgumentException to be thrown " +
                        "as the given priority is lower than it should be!");
    }

    @Test
    public void testCreationOfRuleThrowsWhenTheRuleDefinitionContainsAlreadyDefinedConditions() {
        String newRuleDef = sampleRuleDefinition + "subject-includes: HW1";
        assertThrows(RuleAlreadyDefinedException.class, () -> new Rule(newRuleDef, sampleFolderPath, 5),
                "Expected RuleAlreadyDefinedException to be thrown " +
                        "as there are 2 rows with the same condition!");
    }

    @Test
    public void testCanBeAppliedToReturnsTrueWhenTheGivenMailMatchesTheRuleConditions() {
        Account sender = new Account("stoyo@fmi.bg", "stoyo");
        Set<String> recipients = new HashSet<>(List.of("gosho@gmail.com"));
        String subject = "izpit, 2022, izpit2, mjt";
        String body = "Some text about HW.";
        Mail mail = new Mail(sender, recipients, subject, body, LocalDateTime.now());

        assertTrue(getSampleRule().canBeAppliedTo(mail),
                "The rule must apply to the mail as all conditions match!");
    }

    @Test
    public void testCanBeAppliedToReturnsFalseWhenTheGivenMailSenderDoesNotMatchRuleConditions() {
        Account sender = new Account("stoyo2@fmi.bg", "stoyo2");
        Set<String> recipients = new HashSet<>(List.of("gosho@gmail.com"));
        String subject = "izpit, 2022, izpit2, mjt";
        String body = "Some text about HW.";
        Mail mail = new Mail(sender, recipients, subject, body, LocalDateTime.now());

        assertFalse(getSampleRule().canBeAppliedTo(mail),
                "The rule must not apply to the mail as the sender differs!");
    }

    @Test
    public void testCanBeAppliedToReturnsFalseWhenTheGivenMailRecipientsDoNotMatchRuleConditions() {
        Account sender = new Account("stoyo@fmi.bg", "stoyo");
        Set<String> recipients = new HashSet<>(List.of("ivan@gmail.com"));
        String subject = "izpit, 2022, izpit2, mjt";
        String body = "Some text about HW.";
        Mail mail = new Mail(sender, recipients, subject, body, LocalDateTime.now());

        assertFalse(getSampleRule().canBeAppliedTo(mail),
                "The rule must not apply to the mail as the recipients list differ!");
    }

    @Test
    public void testCanBeAppliedToReturnsFalseWhenTheGivenMailSubjectsDoNotMatchRuleConditions() {
        Account sender = new Account("stoyo@fmi.bg", "stoyo");
        Set<String> recipients = new HashSet<>(List.of("pesho@gmail.com"));
        String subject = "izpit, neshto, mjt";
        String body = "Some text about HW.";
        Mail mail = new Mail(sender, recipients, subject, body, LocalDateTime.now());

        assertFalse(getSampleRule().canBeAppliedTo(mail),
                "The rule must not apply to the mail as the subject does not include all required keywords!");
    }

    @Test
    public void testCanBeAppliedToReturnsFalseWhenTheGivenMailBodyDoNotMatchRuleConditions() {
        Account sender = new Account("stoyo@fmi.bg", "stoyo");
        Set<String> recipients = new HashSet<>(List.of("pesho@gmail.com"));
        String subject = "izpit, neshto, mjt";
        String body = "Some text about mjt course.";
        Mail mail = new Mail(sender, recipients, subject, body, LocalDateTime.now());

        assertFalse(getSampleRule().canBeAppliedTo(mail),
                "The rule must not apply to the mail as the body does not include all required keywords!");
    }

    @Test
    public void testIsConflictRuleReturnsFalseWhenTheRulesHaveDifferentPriorities() {
        List<String> subjects = new ArrayList<>(List.of("mjt", "izpit", "2022"));
        List<String> subjectsOrBody = new ArrayList<>(List.of("izpit", "HW"));
        List<String> recipients = new ArrayList<>(List.of("pesho@gmail.com", "gosho@gmail.com"));
        String senderEmail = "stoyo@fmi.bg";
        int priority = 6;

        Rule rule = new Rule(subjects, subjectsOrBody, recipients, senderEmail, "/Other/Path", priority);

        assertFalse(rule.isConflictRule(getSampleRule()),
                "There must not be a conflict between the rules because their priority differs!");
    }

    @Test
    public void testIsConflictRuleReturnsFalseWhenTheRulesHaveTheSameDirectionFolder() {
        List<String> subjects = new ArrayList<>(List.of("mjt", "izpit", "2022"));
        List<String> subjectsOrBody = new ArrayList<>(List.of("izpit", "HW"));
        List<String> recipients = new ArrayList<>(List.of("pesho@gmail.com", "gosho@gmail.com"));
        String senderEmail = "stoyo@fmi.bg";
        int priority = 8;

        Rule rule = new Rule(subjects, subjectsOrBody, recipients, senderEmail, sampleFolderPath, priority);

        assertFalse(rule.isConflictRule(getSampleRule()),
                "There must not be a conflict between the rules because their direction folders differ!");
    }

    @Test
    public void testIsConflictRuleReturnsFalseWhenTheRulesHaveSomeDifferentRuleConditions() {
        List<String> subjects = new ArrayList<>(List.of("mjt", "izpit", "2022"));
        List<String> subjectsOrBody = new ArrayList<>(List.of("izpit", "HOMEWORK INSTEAD OF HW"));
        List<String> recipients = new ArrayList<>(List.of("pesho@gmail.com", "gosho@gmail.com"));
        String senderEmail = "stoyo@fmi.bg";
        int priority = 8;

        Rule rule = new Rule(subjects, subjectsOrBody, recipients, senderEmail, sampleFolderPath, priority);

        assertFalse(rule.isConflictRule(getSampleRule()),
                "There must not be a conflict between the rules because the subjectsOrBody condition differs!");
    }

    @Test
    public void testIsConflictRuleReturnsTrueWhenTheRulesHaveDifferentDirectionFoldersButTheSameRuleDefinitions() {
        List<String> subjects = new ArrayList<>(List.of("mjt", "izpit", "2022"));
        List<String> subjectsOrBody = new ArrayList<>(List.of("izpit", "HW"));
        List<String> recipients = new ArrayList<>(List.of("pesho@gmail.com", "gosho@gmail.com"));
        String senderEmail = "stoyo@fmi.bg";
        int priority = 8;

        Rule rule = new Rule(subjects, subjectsOrBody, recipients, senderEmail, "/Other/Folder", priority);

        assertTrue(rule.isConflictRule(getSampleRule()),
                "There must be a conflict between the rules because " +
                        "their rule conditions and priority are the same but the direction folders are different!");
    }
}
