package bg.sofia.uni.fmi.mjt.markdown;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MarkdownConverterTest {

    static final MarkdownConverter markdownConverter = new MarkdownConverter();

    static final String input = String.join(System.lineSeparator(),
                                        "# Black Friday",
                                        "Mega **Promos** are waiting",
                                        "Only *today* !") + System.lineSeparator();

    static final String output = String.join(System.lineSeparator(),
                                        "<html>",
                                        "<body>",
                                        "<h1>Black Friday</h1>",
                                        "Mega <strong>Promos</strong> are waiting",
                                        "Only <em>today</em> !",
                                        "</body>",
                                        "</html>") + System.lineSeparator();

    @TempDir
    Path tempDir;

    @Test
    public void testConvertReturnsEmptyStringWhenInputIsNull() {
        assertEquals("", markdownConverter.convert(null),
                "Passing null as input should return an empty string!");
    }

    @Test
    public void testConvertReturnsTheSameStringWhenThereAreNotAnyMarkdownSymbols() {
        String input = "Black Friday";
        assertEquals(input, markdownConverter.convert(input),
                "Output should be the same as the input!");
    }

    @Test
    public void testConvertCorrectlyTransformsHeadline() {
        String input = "##### Black Friday";
        String expected = "<h5>Black Friday</h5>";
        assertEquals(expected, markdownConverter.convert(input),
                "The output should get correct headline (h5)!");
    }

    @Test
    public void testConvertCorrectlyTransformsItalicText() {
        String input = "*Black Friday*";
        String expected = "<em>Black Friday</em>";
        assertEquals(expected, markdownConverter.convert(input),
                "The output should be italic!");
    }

    @Test
    public void testConvertCorrectlyTransformsBoldText() {
        String input = "**Black Friday**";
        String expected = "<strong>Black Friday</strong>";
        assertEquals(expected, markdownConverter.convert(input),
                "The output should be bold!");
    }

    @Test
    public void testConvertCorrectlyTransformsCodeText() {
        String input = "`code`";
        String expected = "<code>code</code>";
        assertEquals(expected, markdownConverter.convert(input),
                "The output should be in code style!");
    }

    @Test
    public void testConvertCorrectlyTransformsTextWithMixedMarkdownSymbols() {
        String input = "## This**is** *mixed* `code`. And this is not.";
        String expected = "<h2>This<strong>is</strong> <em>mixed</em> <code>code</code>. And this is not.</h2>";
        assertEquals(expected, markdownConverter.convert(input),
                "The output should correctly transform the input when it has more than 1 md symbols!");
    }

    @Test
    public void testConvertMarkdownCorrectlyTransformsMarkdownInputToHTML() {
        try (StringReader reader = new StringReader(input);
            StringWriter writer = new StringWriter();
            StringWriter expected = new StringWriter();) {

        expected.write(output);
        markdownConverter.convertMarkdown(reader, writer);
        assertEquals(expected.toString(), writer.toString(),"The input should be correctly converted");

        }  catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testConvertMarkdownCorrectlyTransformsMarkdownFileToHtmlFile() {

        Path sourcePath = tempDir.resolve("tempFile.md");
        Path destPath = tempDir.resolve("tempFile.html");
        Path expectedPath = tempDir.resolve("expected.html");

        try (var mdWriter = Files.newBufferedWriter(sourcePath);
             var htmlWriter = Files.newBufferedWriter(expectedPath);) {

            mdWriter.write(input);
            mdWriter.flush();
            htmlWriter.write(output);
            htmlWriter.flush();

            markdownConverter.convertMarkdown(sourcePath, destPath);

            assertTrue(Files.exists(destPath), "The HTML file should be created!");
            assertTrue(Files.mismatch(expectedPath, destPath) == -1,
                    "The generated html file is not correct!");

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    public void testConvertMarkdownCorrectlyTransformsAllMarkdownFileFromGivenDirToHtmlFiles() {

        Path nonMdPath = tempDir.resolve("noMD");
        Path sourcePath = tempDir.resolve("tempFile.md");
        Path destPath = tempDir.resolve("tempFile.html");
        Path expectedPath = tempDir.resolve("expected.html");


        try (var mdWriter = Files.newBufferedWriter(sourcePath);
             var htmlWriter = Files.newBufferedWriter(expectedPath);) {

            mdWriter.write(input);
            mdWriter.flush();
            htmlWriter.write(output);
            htmlWriter.flush();

            Files.createFile(nonMdPath);

            markdownConverter.convertAllMarkdownFiles(tempDir, tempDir);
            File workingDir = tempDir.toFile();
            int numberOfFiles = workingDir.list().length;

            assertEquals(4, numberOfFiles, "Unexpected number of created files");
            assertTrue(Files.exists(destPath), "The HTML file should be created!");
            assertEquals(-1, Files.mismatch(expectedPath, destPath),
                    "The generated html file is not correct!");

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
