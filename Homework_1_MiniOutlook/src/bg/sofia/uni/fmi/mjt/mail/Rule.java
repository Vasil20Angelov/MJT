package bg.sofia.uni.fmi.mjt.mail;

import bg.sofia.uni.fmi.mjt.mail.exceptions.RuleAlreadyDefinedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class Rule {
    public static final int PRIORITY_LOWER_BOUND = 0;
    public static final int PRIORITY_UPPER_BOUND = 10;

    private List<String> subjects;
    private List<String> subjectsOrBody;
    private List<String> recipients;
    private String senderEmail;
    private String folderPath;
    private int priority;

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
                            subjects = defineCondition(subjects, propertyContent);
                    case "subject-or-body-includes" ->
                            subjectsOrBody = defineCondition(subjectsOrBody, propertyContent);
                    case "recipients-includes" ->
                            recipients = defineCondition(recipients, propertyContent);
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

    private List<String> defineCondition(List<String> condition, String line) {

        if (condition != null) {
            throw new RuleAlreadyDefinedException("Trying to add already defined condition!");
        }

        return splitDataIntoList(line);
    }

    private List<String> splitDataIntoList(String line) {
        List<String> content = new ArrayList<>(List.of(line.split(",")));
        content.replaceAll(String::strip);

        return content;
    }

    public boolean ruleCanBeAppliedTo(Mail mail) {
        String sender = mail.sender().emailAddress();
        if (senderEmail != null && !sender.equals(senderEmail)) {
            return false;
        }

        List<String> mailWords = splitDataIntoList(mail.subject());
        if (!containsAll(mailWords, subjects)) {
            return false;
        }

        mailWords.addAll(splitDataIntoList(mail.body()));

        if (!containsAll(mailWords, subjects)) {
            return false;
        }

        if (recipients == null || recipients.size() == 0) {
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

        return senderEmail.equals(other.senderEmail) || sameLists(subjects, other.subjects)
                || sameLists(subjectsOrBody, other.subjectsOrBody) || sameLists(recipients, other.recipients);
    }

    private boolean sameLists(List<String> list1, List<String> list2) {
        if (!(list1 != null && list2 != null) || list1.size() != list2.size()) {
            return false;
        }

        return containsAll(list1, list2);
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
}
