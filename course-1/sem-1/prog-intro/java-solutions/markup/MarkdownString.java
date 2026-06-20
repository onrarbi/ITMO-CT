package markup;

import java.util.List;

public abstract class MarkdownString implements Markdown {
    private final List<ParagraphElement> elements;
    private final String markdownTag;
    private final String htmlTag;

    public MarkdownString(List<ParagraphElement> elements, String markdownTag, String htmlTag) {
        this.elements = elements;
        this.markdownTag = markdownTag;
        this.htmlTag = htmlTag;
    }

    @Override
    public void toMarkdown(StringBuilder sb) {
        sb.append(markdownTag);

        for (ParagraphElement element : elements) {
            element.toMarkdown(sb);
        }

        sb.append(markdownTag);
    }

    @Override
    public void toHtml(StringBuilder sb) {
        sb.append("<").append(htmlTag).append(">");

        for (ParagraphElement element : elements) {
            element.toHtml(sb);
        }

        sb.append("</").append(htmlTag).append(">");
    }
}