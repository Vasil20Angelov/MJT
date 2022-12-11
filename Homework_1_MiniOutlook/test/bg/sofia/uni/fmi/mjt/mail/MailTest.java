package bg.sofia.uni.fmi.mjt.mail;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MailTest {

    @Test
    public void testCreationOfMailWithGivenMetadata() {
        String metadata = """ 
                        sender: testy@gmail.com
                        subject: Hello, MJT
                        recipients: pesho@gmail.com, gosho@gmail.com, 
                        received: 2022-12-08 14:14""";

        String content = "some content";
        LocalDateTime receivedAt = LocalDateTime.of(2022, 12, 8, 14, 14);

        Account sender = new Account("testy@gmail.com", "testy");
        Map<String, Account> accounts = new HashMap<>();
        accounts.put("testy@gmail.com", sender);

        Set<String> recipients = new HashSet<>() {
            {
                add("pesho@gmail.com");
                add("gosho@gmail.com");
            }
        };

        Mail result = Mail.of(accounts, metadata, content);
        Mail expected = new Mail(sender, recipients, "Hello, MJT", content, receivedAt);

        assertEquals(expected, result, "Wrong parsing of metadata!");
    }
}
