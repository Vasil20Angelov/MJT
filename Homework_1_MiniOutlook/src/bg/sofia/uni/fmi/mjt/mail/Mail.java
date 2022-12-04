package bg.sofia.uni.fmi.mjt.mail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public record Mail(Account sender, Set<String> recipients, String subject, String body, LocalDateTime received) {

    public static Mail of(Map<String, Account> accounts, String metadata, String content) {
        String line;
        String subject = "";
        Account account = null;
        Set<String> recipients = new HashSet<>();
        LocalDateTime received = null;
        DateTimeFormatter formatterLocalDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        try (BufferedReader bufferedReader = new BufferedReader(new StringReader(metadata))) {

            while ((line = bufferedReader.readLine()) != null) {
                int endIndexOfProperty = line.indexOf(':');
                String property = line.substring(0, endIndexOfProperty);
                String propertyContent = line.substring(endIndexOfProperty + 1).strip();
                switch (property) {
                    case "sender" ->
                            account = new Account(propertyContent, accounts.get(propertyContent).name());
                    case "subject" -> subject = propertyContent;
                    case "recipients" -> recipients.addAll(List.of(propertyContent.split(",")));
                    case "received" -> received = LocalDateTime.parse(propertyContent, formatterLocalDate);
                }
            }
        }
        catch (IOException ex) {
            throw new IllegalStateException("Error appeared while parsing the mail!");
        }

        return new Mail(account, recipients, subject, content, received);
    }
}