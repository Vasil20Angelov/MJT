package bg.sofia.uni.fmi.mjt.mail;

import bg.sofia.uni.fmi.mjt.mail.exceptions.FolderAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.FolderNotFoundException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.InvalidPathException;

import java.util.*;

public class AccountFolder {

    private static final char SEPARATOR = '/';
    private static final String INBOX_FOLDER = SEPARATOR + "inbox";
    private static final String SENT_FOLDER = SEPARATOR + "sent";
    private Map<String, List<Mail>> folders;
    private Set<Rule> rules = new TreeSet<>(Comparator.comparing(Rule::getPriority).reversed());

    public AccountFolder() {
        folders = new HashMap<>() {
            {
                put(INBOX_FOLDER, new LinkedList<>());
                put(SENT_FOLDER, new LinkedList<>());
            }
        };
    }

    public void addFolder(String folderPath) {

        if (!folderPath.startsWith(INBOX_FOLDER)) {
            throw new InvalidPathException("The path must start from " + INBOX_FOLDER);
        }

        int lastSlash = folderPath.lastIndexOf(SEPARATOR);
        if (lastSlash < INBOX_FOLDER.length()) {
            throw new InvalidPathException("Invalid path!");
        }

        if (!folders.containsKey(folderPath.substring(0, lastSlash))) {
            throw new InvalidPathException("Missing intermediate folders!");
        }

        if (folders.containsKey(folderPath)) {
            throw new FolderAlreadyExistsException("The folder was already added in this account!");
        }

        folders.put(folderPath, new ArrayList<>());
    }

    public void addMail(Mail mail) {

        for (Rule rule : rules) {
            if (rule.ruleCanBeAppliedTo(mail)) {
                folders.get(rule.getFolderPath()).add(mail);
                return;
            }
        }

        folders.get(INBOX_FOLDER).add(mail);
    }

    public void addToSent(Mail mail) {
        folders.get(SENT_FOLDER).add(mail);
    }

    public Collection<Mail> getMails(String folderPath) {
        if (!folders.containsKey(folderPath)) {
            throw new FolderNotFoundException("No such folder into this account!");
        }

        return folders.get(folderPath);
    }

    public void addRule(String ruleDefinition, String folderPath, int priority) {
        if (!folders.containsKey(folderPath)) {
            throw new FolderNotFoundException("Cannot define a rule that sends mails to not existing folder!");
        }

        Rule newRule = new Rule(ruleDefinition, folderPath, priority);
        for (Rule rule : rules) {
            if (newRule.isConflictRule(rule)) {
                return;
            }
        }

        rules.add(newRule);
    }
}
