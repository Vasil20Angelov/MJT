import bg.sofia.uni.fmi.mjt.mail.Mail;
import bg.sofia.uni.fmi.mjt.mail.Outlook;

public class Main {
    public static void main(String[] args) {
        String s = """ 
                 sender: testy@gmail.com
                 subject: Hello, MJT
                 recipients: pesho@gmail.com, gosho@gmail.com,
                 received: 2022-12-08 14:14""";


        Outlook o = new Outlook();
        o.addNewAccount("testy", "testy@gmail.com");
        o.createFolder("testy", "/inbox/f");
        o.createFolder("testy", "/inbox/mjt");
        o.createFolder("testy", "/inbox/f/s");

        String rule = """
                subject-includes: MJT
                subject-or-body-includes: MJT
                """;

        String rule2 = """
                subject-includes: MJT
                subject-or-body-includes: MJT
                """;

        o.addRule("testy", "/inbox/mjt", rule, 5);
        o.addRule("testy", "/inbox/f", rule2, 8);
        o.receiveMail("testy", s, "asdgfs");

    }
}
