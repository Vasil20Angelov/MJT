package bg.sofia.uni.fmi.mjt.mail;

import bg.sofia.uni.fmi.mjt.mail.exceptions.RuleAlreadyDefinedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;


public class Rule {
    public static final int PRIORITY_LOWER_BOUND = 0;
    public static final int PRIORITY_UPPER_BOUND = 10;
    private static final String PUNCTUATION_REGEX = "[\\p{IsPunctuation}\\s]+";

    private List<String> subjects;
    private List<String> subjectsOrBody;
    private List<String> recipients;
    private String senderEmail;
    private final String folderPath;
    private int priority;

    public Rule(List<String> subjects, List<String> subjectsOrBody, List<String> recipients,
                String senderEmail, String folderPath, int priority) {
        this.subjects = subjects;
        this.subjectsOrBody = subjectsOrBody;
        this.recipients = recipients;
        this.senderEmail = senderEmail;
        this.folderPath = folderPath;
        setPriority(priority);
    }

    public Rule(String ruleDefinition, String folderPath, int priorityValue) {

        setPriority(priorityValue);
        this.folderPath = folderPath;

        try (BufferedReader bufferedReader = new BufferedReader(new StringReader(ruleDefinition))) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                int endIndexOfProperty = line.indexOf(':');
                String property = line.substring(0, endIndexOfProperty);
                String propertyContent = line.substring(endIndexOfProperty + 1).strip();

                switch (property) {
                    case "subject-includes" ->
                            subjects = defineCondition(subjects, propertyContent, false);
                    case "subject-or-body-includes" ->
                            subjectsOrBody = defineCondition(subjectsOrBody, propertyContent, false);
                    case "recipients-includes" ->
                            recipients = defineCondition(recipients, propertyContent, true);
                    case "from" -> {
                        if (senderEmail != null) {
                            throw new RuleAlreadyDefinedException("Trying to add already defined condition!");
                        }
                        senderEmail = propertyContent;
                    }
                }
            }
        }
        catch (IOException ex) {
            throw new IllegalStateException("Error appeared while parsing the rule!");
        }

        initializeNullFields();
    }

    private void initializeNullFields() {
        if (subjects == null) {
            subjects = new ArrayList<>();
        }

        if (subjectsOrBody == null) {
            subjectsOrBody = new ArrayList<>();
        }

        if (recipients == null) {
            recipients = new ArrayList<>();
        }

        if (senderEmail == null) {
            senderEmail = "";
        }
    }


    public int getPriority() {
        return priority;
    }

    private void setPriority(int value) {
        if (value < PRIORITY_LOWER_BOUND || value > PRIORITY_UPPER_BOUND) {
            throw new IllegalArgumentException("Priority must be in the interval [0;10]!");
        }

        priority = value;
    }

    public String getFolderPath() {
        return folderPath;
    }

    private List<String> defineCondition(List<String> condition, String line, boolean parsingEmailList) {

        if (condition != null) {
            throw new RuleAlreadyDefinedException("Trying to add already defined condition!");
        }

        return splitDataIntoList(line, parsingEmailList);
    }

    private List<String> splitDataIntoList(String line, boolean parsingEmailList) {
        if (line == null) {
            return new ArrayList<>();
        }

        List<String> content;
        if (parsingEmailList) {
            content = new ArrayList<>(List.of(line.split(",")));
        }
        else {
            content = new ArrayList<>(List.of(line.split(PUNCTUATION_REGEX)));
        }

        content.replaceAll(String::strip);

        return content;
    }

    public boolean canBeAppliedTo(Mail mail) {
        String sender = mail.sender().emailAddress();
        if (!senderEmail.equals("") && !sender.equals(senderEmail)) {
            return false;
        }

        Set<String> mailWords = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        mailWords.addAll(splitDataIntoList(mail.subject(), false));
        if (mailDoesNotContainAllRuleKeywords(mailWords, subjects)) {
            return false;
        }

        mailWords.addAll(splitDataIntoList(mail.body(), false));
        if (mailDoesNotContainAllRuleKeywords(mailWords, subjectsOrBody)) {
            return false;
        }

        if (recipients.size() == 0) {
            return true;
        }

        for (String recipient : mail.recipients()) {
            if (recipients.contains(recipient)) {
                return true;
            }
        }

        return false;
    }

    public boolean isConflictRule(Rule other) {
        if (folderPath.equals(other.folderPath) || priority != other.priority) {
            return false;
        }

        return senderEmail.equals(other.senderEmail) && sameLists(subjects, other.subjects)
                && sameLists(subjectsOrBody, other.subjectsOrBody) && sameLists(recipients, other.recipients);
    }

    private boolean sameLists(List<String> list1, List<String> list2) {
        return list1.size() == list2.size() && containsAll(list1, list2);
    }

    private boolean containsAll(List<String> list1, List<String> list2) {
        for (String element1 : list2) {
            boolean isContained = false;
            for (String element2 : list1) {
                if (element1.equalsIgnoreCase(element2)) {
                    isContained = true;
                    break;
                }
            }

            if (!isContained) {
                return false;
            }
        }

        return true;
    }

    private boolean mailDoesNotContainAllRuleKeywords(Set<String> wordsInMail, List<String> ruleKeywords) {
        for (String ruleKeyword : ruleKeywords) {
            if (!wordsInMail.contains(ruleKeyword)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rule rule = (Rule) o;
        return priority == rule.priority && Objects.equals(subjects, rule.subjects)
                && Objects.equals(subjectsOrBody, rule.subjectsOrBody) && Objects.equals(recipients, rule.recipients)
                && Objects.equals(senderEmail, rule.senderEmail) && Objects.equals(folderPath, rule.folderPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subjects, subjectsOrBody, recipients, senderEmail, folderPath, priority);
    }
}
