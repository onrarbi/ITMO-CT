import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public class NewScanner implements AutoCloseable {
    private static final int BUFFER_SIZE = 8192;

    private final Reader reader;
    private final char[] buffer = new char[BUFFER_SIZE];

    private int position = 0;
    private int size = 0;
    private boolean closed = false;

    private Predicate<Character> delimiter = Character::isWhitespace;

    public NewScanner(Reader reader) {
        this.reader = reader;
    }

    public NewScanner(InputStream input) {
        this(new InputStreamReader(input, StandardCharsets.UTF_8));
    }

    public NewScanner(String input) {
        this(new StringReader(input));
    }

    public void setDelimiter(Predicate<Character> delimiter) {
        this.delimiter = delimiter;
    }

    private boolean fillBuffer() {
        if (closed || size == -1) {
            return false;
        }

        if (position >= size) {
            try {
                size = reader.read(buffer);
                position = 0;
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        return size != -1;
    }

    private int peek() {
        if (!fillBuffer()) {
            return -1;
        }

        return buffer[position];
    }

    private int read() {
        int c = peek();

        if (c != -1) {
            position++;
        }

        return c;
    }

    private void skipDelimiters(boolean inLine) {
        while (true) {
            int c = peek();

            if (c == -1 || inLine && c == '\n' || !delimiter.test((char) c)) {
                break;
            }

            read();
        }
    }

    private boolean isTokenEnd(int c, boolean inLine) {
        return c == -1 || inLine && c == '\n' || delimiter.test((char) c);
    }

    private String nextToken(boolean inLine) {
        skipDelimiters(inLine);

        int c = peek();
        if (c == -1 || inLine && c == '\n') {
            throw new NoSuchElementException("No next token");
        }

        StringBuilder token = new StringBuilder();

        while (true) {
            c = peek();

            if (isTokenEnd(c, inLine)) {
                break;
            }

            token.append((char) read());
        }

        return token.toString();
    }

    public boolean hasNext() {
        skipDelimiters(false);
        return peek() != -1;
    }

    public String next() {
        return nextToken(false);
    }

    public boolean hasNextIntInLine() {
        skipDelimiters(true);

        int c = peek();
        return c != -1 && c != '\n';
    }

    public int nextIntInLine() {
        return Integer.parseInt(nextToken(true));
    }

    public boolean hasNextLine() {
        return peek() != -1;
    }

    public String nextLine() {
        if (!hasNextLine()) {
            throw new NoSuchElementException("No next line");
        }

        StringBuilder line = new StringBuilder();

        while (true) {
            int c = read();

            if (c == -1 || c == '\n') {
                break;
            }

            if (c != '\r') {
                line.append((char) c);
            }
        }

        return line.toString();
    }

    @Override
    public void close() {
        try {
            if (!closed) {
                reader.close();
                closed = true;
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}