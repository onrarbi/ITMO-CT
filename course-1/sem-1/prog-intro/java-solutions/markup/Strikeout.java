package markup;

import java.util.List;

public class Strikeout extends MarkdownString implements ParagraphElement {
    public Strikeout(List<ParagraphElement> elements) {
        super(elements, "~", "s");
    }
}