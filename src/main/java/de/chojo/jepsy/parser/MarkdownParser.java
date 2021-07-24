package de.chojo.jepsy.parser;

public class MarkdownParser {
    public static String htmlToMarkdow(String html) {
        var result = surroundGroup(html, "<code>(.+?)</code>", "`");
        result = replaceTag(result, "code", "```");
        result = replaceTag(result, "b", "**");
        result = replaceTag(result, "strong", "**");
        result = replaceTag(result, "i", "*");
        result = replaceTag(result, "em", "*");
        result = replaceTag(result, "mark", "*");
        result = replaceTag(result, "del", "--");
        result = result.replaceAll("<a href=\"(.+?)\">(.+?)</a>", "[$2]($1)");
        result = result.replaceAll("<li>", "- ");
        result = result.replaceAll("</p>", "\n");

        result = replaceTag(result, ".+?", "");

        return result.replaceAll(" +?", " ");
    }

    private static String surroundGroup(String source, String regex, String replace) {
        return source.replaceAll(regex, replace + "$1" + replace);
    }

    private static String replaceTag(String source, String tag, String replace) {
        return source.replaceAll("</?" + tag + ">", replace);
    }

    public static String url(String text, String url) {
        return String.format("[%s](%s)", text, url);
    }
}
