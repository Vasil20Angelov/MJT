package bg.sofia.uni.fmi.mjt.mail;

import bg.sofia.uni.fmi.mjt.mail.exceptions.AccountAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.AccountNotFoundException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Outlook implements MailClient {

    private Map<String, Account> accountsByName = new HashMap<>();
    private Map<String, Account> accountsByEmail = new HashMap<>();
    private Map<Account, AccountFolder> accountsFolder = new HashMap<>();

    public Outlook() { }

    @Override
    public Account addNewAccount(String accountName, String email) {
        validateStrings(accountName, email);
        validateAccountDoesNotExists(accountsByName, accountName);
        validateAccountDoesNotExists(accountsByEmail, email);

        Account newAccount = new Account(email, accountName);
        accountsByName.put(accountName, newAccount);
        accountsByEmail.put(email, newAccount);
        accountsFolder.put(newAccount, new AccountFolder());

        return newAccount;
    }

    @Override
    public void createFolder(String accountName, String path) {
        validateStrings(accountName, path);
        validateAccountExistsByName(accountName);

        Account account = accountsByName.get(accountName);
        accountsFolder.get(account).addFolder(path);
    }

    @Override
    public void addRule(String accountName, String folderPath, String ruleDefinition, int priority) {
        validateStrings(accountName, folderPath, ruleDefinition);
        validateAccountExistsByName(accountName);
        Account account = accountsByName.get(accountName);

        accountsFolder.get(account).addRule(ruleDefinition, folderPath, priority);
    }

    @Override
    public void receiveMail(String accountName, String mailMetadata, String mailContent) {
        validateStrings(accountName, mailMetadata, mailContent);
        validateAccountExistsByName(accountName);
        Account receiver = accountsByName.get(accountName);
        Mail mail = Mail.of(accountsByEmail, mailMetadata, mailContent);

        accountsFolder.get(receiver).addMail(mail);
    }

    @Override
    public Collection<Mail> getMailsFromFolder(String account, String folderPath) {
        validateStrings(account, folderPath);
        validateAccountExistsByName(account);
        Account selectedAccount = accountsByName.get(account);

        return accountsFolder.get(selectedAccount).getMails(folderPath);
    }

    @Override
    public void sendMail(String accountName, String mailMetadata, String mailContent) {
        validateStrings(accountName, mailMetadata, mailContent);
        mailMetadata = fillSenderField(accountName, mailMetadata);
        List<String> recipients = getRecipientsFromMetadata(mailMetadata);
        mailMetadata = fillSenderField(accountName, mailMetadata);

        for (String receiverEmail : recipients) {
            if (accountsByName.containsKey(receiverEmail)) {
                String receiverName = accountsByEmail.get(receiverEmail).name();
                receiveMail(receiverName, mailMetadata, mailContent);
            }
        }
    }

    private List<String> getRecipientsFromMetadata(String metadata) {
        List<String> recipients = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new StringReader(metadata))) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("recipients:")) {
                    line = line.replaceFirst("recipients:", "");
                    recipients.addAll(List.of(line.split(",")));
                    recipients.replaceAll(String::strip);
                }
            }
        }
        catch (IOException ex) {
            throw new IllegalStateException("Error appeared while parsing the mail!");
        }

        return recipients;
    }

    private String fillSenderField(String senderName, String metaData) {

        if (metaData.contains("sender:")) {
            return metaData;
        }

        return metaData.concat(System.lineSeparator() + "sender: " + accountsByName.get(senderName).emailAddress());
    }

    private void validateStrings(String... params) {
        for (String string : params) {
            if (string == null || string.isBlank()) {
                throw new IllegalArgumentException("Invalid parameter given");
            }
        }
    }

    private void validateAccountDoesNotExists(Map<String, Account> accounts, String key) {
        if (accounts.containsKey(key)) {
            throw new AccountAlreadyExistsException();
        }
    }

    private void validateAccountExistsByName(String accountName) {
        if (!accountsByName.containsKey(accountName)) {
            throw new AccountNotFoundException(accountName + " has not been found!");
        }
    }
}
