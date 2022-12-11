package bg.sofia.uni.fmi.mjt.mail;

import bg.sofia.uni.fmi.mjt.mail.exceptions.FolderAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.FolderNotFoundException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.InvalidPathException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;


public class AccountFolder {

    public static final char SEPARATOR = '/';
    public static final String INBOX_FOLDER = SEPARATOR + "inbox";
    public static final String SENT_FOLDER = SEPARATOR + "sent";
    private final Account account;
    private final Map<String, List<Mail>> folders;
    private final PriorityQueue<Rule> rules = new PriorityQueue<>(Comparator.comparing(Rule::getPriority).reversed());

    public AccountFolder(Account account) {
        this.account = account;
        folders = new HashMap<>() {
            {
                put(INBOX_FOLDER, new ArrayList<>());
                put(SENT_FOLDER, new ArrayList<>());
            }
        };
    }

    public List<String> getFolderNames() {
        return List.copyOf(folders.keySet());
    }

    public List<Rule> getRules() {
        return List.copyOf(rules);
    }

    public void addFolder(String folderPath) {

        if (!folderPath.startsWith(INBOX_FOLDER)) {
            throw new InvalidPathException("The path must start from " + INBOX_FOLDER);
        }

        if (folders.containsKey(folderPath)) {
            throw new FolderAlreadyExistsException("The folder was already added in this account!");
        }

        if (folderPath.length() < INBOX_FOLDER.length() + 2
                || folderPath.charAt(INBOX_FOLDER.length()) != '/'
                || folderPath.contains("//")
                || folderPath.charAt(folderPath.length() - 1) == '/') {

            throw new InvalidPathException("Invalid path!");
        }

        int lastSlash = folderPath.lastIndexOf(SEPARATOR);
        if (!folders.containsKey(folderPath.substring(0, lastSlash))) {
            throw new InvalidPathException("Missing intermediate folders!");
        }

        folders.put(folderPath, new ArrayList<>());
    }

    public void addMail(Mail mail) {

        if (mail.sender().equals(account)) {
            folders.get(SENT_FOLDER).add(mail);
            return;
        }

        for (Rule rule : rules) {
            if (rule.canBeAppliedTo(mail)) {
                folders.get(rule.getFolderPath()).add(mail);
                return;
            }
        }

        folders.get(INBOX_FOLDER).add(mail);
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
        moveMailsFromInbox(newRule);
    }

    private void moveMailsFromInbox(Rule rule) {
        List<Mail> targetedFolder = folders.get(rule.getFolderPath());
        for (Iterator<Mail> i = folders.get(INBOX_FOLDER).iterator(); i.hasNext();) {
            Mail mail = i.next();
            if (rule.canBeAppliedTo(mail)) {
                i.remove();
                targetedFolder.add(mail);
            }
        }
    }
}
