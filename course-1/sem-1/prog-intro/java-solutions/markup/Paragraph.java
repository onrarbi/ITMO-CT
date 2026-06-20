package markup;

import java.util.List;

public class Paragraph extends MarkdownString {
    public Paragraph(List<ParagraphElement> elements) {
        super(elements, "", "p");
    }
}