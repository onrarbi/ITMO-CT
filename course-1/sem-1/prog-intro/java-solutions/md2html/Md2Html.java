package md2html;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Md2Html {
    private enum Markup {
        STRONG_A("**", "strong"),
        STRONG_B("__", "strong"),
        STRIKEOUT("--", "s"),
        EMPHASIS_A("*", "em"),
        EMPHASIS_B("_", "em"),
        CODE("`", "code"),
        VARIABLE("%", "var");

        private final String markdown;
        private final String html;

        Markup(String markdown, String html) {
            this.markdown = markdown;
            this.html = html;
        }

        private int length() {
            return markdown.length();
        }

        private String openTag() {
            return "<" + html + ">";
        }

        private String closeTag() {
            return "</" + html + ">";
        }

        private static Markup findAt(String text, int index, int right) {
            for (Markup markup : values()) {
                if (index + markup.length() <= right && text.startsWith(markup.markdown, index)) {
                    return markup;
                }
            }

            return null;
        }
    }

    private static void appendHtmlChar(StringBuilder html, char c) {
        switch (c) {
            case '<' -> html.append("&lt;");
            case '>' -> html.append("&gt;");
            case '&' -> html.append("&amp;");
            default -> html.append(c);
        }
    }

    private static int findClosing(String text, int from, int right, Markup markup) {
        for (int i = from; i + markup.length() <= right; i++) {
            if (text.charAt(i) == '\\') {
                i++;
                continue;
            }

            Markup current = Markup.findAt(text, i, right);

            if (current == markup)
            {
                return i;
            }

            if (current != null) {
                i += current.length() - 1;
            }
        }

        return -1;
    }

    private static void parseInline(String text, int left, int right, StringBuilder html) {
        int i = left;

        while (i < right) {
            char c = text.charAt(i);

            if (c == '\\' && i + 1 < right) {
                appendHtmlChar(html, text.charAt(i + 1));
                i += 2;
                continue;
            }

            Markup markup = Markup.findAt(text, i, right);

            if (markup != null) {
                int close = findClosing(text, i + markup.length(), right, markup);

                if (close != -1) {
                    html.append(markup.openTag());

                    parseInline(text, i + markup.length(), close, html);

                    html.append(markup.closeTag());
                    i = close + markup.length();
                    continue;
                }
            }

            appendHtmlChar(html, c);
            i++;
        }
    }

    private static int getHeaderLevel(String block) {
        int level = 0;

        while (level < block.length() && level < 6 && block.charAt(level) == '#') {
            level++;
        }

        if (level > 0 && level < block.length() && block.charAt(level) == ' ') {
            return level;
        }

        return 0;
    }

    private static int getHeaderContentStart(String block, int headerLevel) {
        int pos = headerLevel;

        while (pos < block.length() && block.charAt(pos) == ' ') {
            pos++;
        }

        return pos;
    }

    private static void processBlock(String block, StringBuilder html) {
        int headerLevel = getHeaderLevel(block);

        if (headerLevel == 0) {
            html.append("<p>");
            parseInline(block, 0, block.length(), html);
            html.append("</p>").append(System.lineSeparator());
        } else {
            html.append("<h").append(headerLevel).append(">");
            parseInline(block, getHeaderContentStart(block, headerLevel), block.length(), html);
            html.append("</h").append(headerLevel).append(">").append(System.lineSeparator());
        }
    }

    private static void processCurrentBlock(StringBuilder block, StringBuilder html) {
        if (!block.isEmpty()) {
            processBlock(block.toString(), html);
            block.setLength(0);
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            return;
        }

        StringBuilder html = new StringBuilder();
        StringBuilder block = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), StandardCharsets.UTF_8))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    processCurrentBlock(block, html);
                } else {
                    if (!block.isEmpty()) {
                        block.append(System.lineSeparator());
                    }

                    block.append(line);
                }
            }

            processCurrentBlock(block, html);
        } catch (IOException e) {
            System.err.println("Input error: " + e.getMessage());
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), StandardCharsets.UTF_8))) {
            writer.write(html.toString());
        } catch (IOException e) {
            System.err.println("Output error: " + e.getMessage());
        }
    }
}