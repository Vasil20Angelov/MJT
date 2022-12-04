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

    private Map<String, Account> accounts = new HashMap<>();

    public Outlook() { }

    @Override
    public Account addNewAccount(String accountName, String email) {
        ValidateString(accountName, email);
        ValidateAccountDoesNotExists(accountName);

        Account newAccount = new Account(email, accountName);
        accounts.put(accountName, newAccount);

        return newAccount;
    }

    @Override
    public void createFolder(String accountName, String path) {

    }

    @Override
    public void addRule(String accountName, String folderPath, String ruleDefinition, int priority) {

    }

    @Override
    public void receiveMail(String accountName, String mailMetadata, String mailContent) {
        // TODO: FolderNotFoundException
        ValidateString(accountName, mailMetadata, mailContent);
        ValidateAccountExists(accountName);


    }

    @Override
    public Collection<Mail> getMailsFromFolder(String account, String folderPath) {
        return null;
    }

    @Override
    public void sendMail(String accountName, String mailMetadata, String mailContent) {

    }

    public Mail CreateMail(String metadata, String content) {
        String line;
        String subject = "";
        Account account = new Account("1", "2");
        Set<String> recipients = new HashSet<>();
        LocalDateTime received = LocalDateTime.now();
        DateTimeFormatter formatterLocalDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        try (StringReader stringReader = new StringReader(metadata);
            BufferedReader bufferedReader = new BufferedReader(stringReader)) {

            while ((line = bufferedReader.readLine()) != null) {
                int endIndexOfProperty = line.indexOf(':');
                String property = line.substring(0, endIndexOfProperty);
                String propertyContent = line.substring(endIndexOfProperty + 1).stripLeading();
                switch (property) {
                    case "sender":
                        break;
                    case "subject":
                        subject = propertyContent;
                        break;
                    case "recipients":
                        propertyContent.strip();
                        recipients.addAll(List.of(propertyContent.split(",")));
                        break;
                    case "received":
                        received = LocalDateTime.parse(propertyContent, formatterLocalDate);
                        break;
                }
            }
        }
        catch (IOException ex) {
            throw new IllegalStateException("Error appeared while parsing the mail!");
        }

        return new Mail(account, recipients, subject, content, received);
    }

    private void ValidateString(String... params) {
        for (String string : params) {
            if (string == null || string.isBlank()) {
                throw new IllegalArgumentException("Invalid parameter given");
            }
        }
    }

    private void ValidateAccountDoesNotExists(String accountName) {
        if (accounts.containsKey(accountName)) {
            throw new AccountAlreadyExistsException();
        }
    }

    private void ValidateAccountExists(String accountName) {
        if (!accounts.containsKey(accountName)) {
            throw new AccountNotFoundException();
        }
    }
}
