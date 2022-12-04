package bg.sofia.uni.fmi.mjt.markdown;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownConverter implements MarkdownConverterAPI {

    private final Pattern bold = Pattern.compile("\\*\\*(.+)\\*\\*");
    private final Pattern italic = Pattern.compile("\\*(.+)\\*");
    private final Pattern code = Pattern.compile("`(.+)`");

    public MarkdownConverter() {
    }

    @Override
    public void convertMarkdown(Reader source, Writer output) {
        try (BufferedReader reader = new BufferedReader(source);
             BufferedWriter writer = new BufferedWriter(output)) {

            writer.write("<html>" + System.lineSeparator() + "<body>" + System.lineSeparator());
            String line;
            while ((line = reader.readLine()) != null) {
                line = convert(line);
                writer.write(line + System.lineSeparator());
            }
            writer.write("</body>" + System.lineSeparator() + "</html>" + System.lineSeparator());
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while reading", e);
        }
    }

    @Override
    public void convertMarkdown(Path from, Path to) {
        try {
            Reader reader = Files.newBufferedReader(from);
            Writer writer = Files.newBufferedWriter(to);

            convertMarkdown(reader, writer);
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while handling the files", e);
        }
    }

    @Override
    public void convertAllMarkdownFiles(Path sourceDir, Path targetDir) {
        if (!Files.exists(targetDir)) {
            return;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourceDir)) {
            for (Path path : stream) {
                String fileName = path.getFileName().toString();
                int dotIndex = fileName.lastIndexOf('.');
                if (dotIndex > 0 && fileName.substring(dotIndex).equals(".md")) {
                    String htmlFileName = fileName.substring(0, dotIndex) + ".html";
                    Path htmlFile = Path.of(targetDir.toString(), htmlFileName);
                    convertMarkdown(path, htmlFile);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while handling the files", e);
        }
    }

    public String convert(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        int i = 0;
        while (input.charAt(i) == '#') {
            i++;
        }

        if (i > 0) {
            input = "<h" + i + ">" + input.substring(i + 1) + "</h" + i + ">";
        }

        Matcher matcher = bold.matcher(input);
        input = matcher.replaceFirst("<strong>$1</strong>");

        matcher = italic.matcher(input);
        input = matcher.replaceFirst("<em>$1</em>");

        matcher = code.matcher(input);
        input = matcher.replaceFirst("<code>$1</code>");

        return input;
    }
}
