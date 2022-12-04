import bg.sofia.uni.fmi.mjt.mail.Mail;
import bg.sofia.uni.fmi.mjt.mail.Outlook;

public class Main {
    public static void main(String[] args) {
        String s = """ 
                 sender: testy@gmail.com
                 subject: Hello, MJT!
                 recipients: pesho@gmail.com, gosho@gmail.com,
                 received: 2022-12-08 14:14""";


        Outlook o = new Outlook();
        Mail mail = o.CreateMail(s, "abv");

    }
}
