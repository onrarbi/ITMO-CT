package markup;

import java.util.List;

public class Emphasis extends MarkdownString implements ParagraphElement {
    public Emphasis(List<ParagraphElement> elements) {
        super(elements, "*", "em");
    }
}