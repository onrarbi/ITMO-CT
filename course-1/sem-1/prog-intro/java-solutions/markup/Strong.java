package markup;

import java.util.List;

public class Strong extends MarkdownString implements ParagraphElement {
    public Strong(List<ParagraphElement> elements) {
        super(elements, "__", "strong");
    }
}