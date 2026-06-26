package com.jetpackduba.gitnuro.ui.diff.syntax_highlighter

class YamlSyntaxHighlighter : SyntaxHighlighter() {
    override fun loadKeywords(): List<String> = listOf("true", "false", "null", "yes", "no", "on", "off")
    override fun isAnnotation(word: String): Boolean = word.endsWith(":")
    override fun isComment(line: String): Boolean = line.startsWith("#")
}

class TomlSyntaxHighlighter : SyntaxHighlighter() {
    override fun loadKeywords(): List<String> = listOf("true", "false")
    override fun isAnnotation(word: String): Boolean = word.startsWith("[") || word.endsWith("]")
    override fun isComment(line: String): Boolean = line.startsWith("#")
}

class JsonSyntaxHighlighter : SyntaxHighlighter() {
    override fun loadKeywords(): List<String> = listOf("true", "false", "null")
    override fun isAnnotation(word: String): Boolean = false
    override fun isComment(line: String): Boolean = false
}

class XmlSyntaxHighlighter : SyntaxHighlighter() {
    override fun loadKeywords(): List<String> = listOf(
        "html", "head", "body", "div", "span", "a", "p", "h1", "h2", "h3", "h4", "h5", "h6",
        "ul", "ol", "li", "table", "tr", "td", "th", "form", "input", "button", "select",
        "img", "link", "meta", "script", "style", "section", "header", "footer", "nav", "main",
        "svg", "path", "rect", "circle", "line", "g", "defs", "use", "xmlns"
    )
    override fun isAnnotation(word: String): Boolean =
        word.startsWith("<") || word.startsWith("</") || word == "/>" || word == ">"
    override fun isComment(line: String): Boolean = line.startsWith("<!--")
}

class CssSyntaxHighlighter : SyntaxHighlighter() {
    override fun loadKeywords(): List<String> = listOf(
        "import", "media", "keyframes", "font-face", "charset", "supports", "layer",
        "none", "auto", "inherit", "initial", "unset", "revert",
        "flex", "grid", "block", "inline", "absolute", "relative", "fixed", "sticky",
        "solid", "dashed", "dotted", "hidden", "visible", "scroll", "wrap",
        "center", "left", "right", "top", "bottom", "start", "end",
        "bold", "normal", "italic", "uppercase", "lowercase", "capitalize",
        "transparent", "currentColor", "important"
    )
    override fun isAnnotation(word: String): Boolean =
        word.startsWith("@") || word.startsWith("$") || word.startsWith("--")
    override fun isComment(line: String): Boolean = line.startsWith("//") || line.startsWith("/*")
}

class MarkdownSyntaxHighlighter : SyntaxHighlighter() {
    override fun loadKeywords(): List<String> = emptyList()
    override fun isAnnotation(word: String): Boolean =
        word.startsWith("#") || word.startsWith("*") || word.startsWith("-") || word.startsWith(">")
    override fun isComment(line: String): Boolean = line.startsWith("<!--")
}

class DockerfileSyntaxHighlighter : SyntaxHighlighter() {
    override fun loadKeywords(): List<String> = listOf(
        "FROM", "RUN", "CMD", "LABEL", "MAINTAINER", "EXPOSE", "ENV", "ADD", "COPY",
        "ENTRYPOINT", "VOLUME", "USER", "WORKDIR", "ARG", "ONBUILD", "STOPSIGNAL",
        "HEALTHCHECK", "SHELL", "AS"
    )
    override fun isAnnotation(word: String): Boolean = false
    override fun isComment(line: String): Boolean = line.startsWith("#")
}

class TerraformSyntaxHighlighter : SyntaxHighlighter() {
    override fun loadKeywords(): List<String> = listOf(
        "resource", "data", "variable", "output", "locals", "module", "provider", "terraform",
        "backend", "required_providers", "required_version",
        "for_each", "count", "depends_on", "lifecycle", "provisioner", "connection",
        "dynamic", "content", "for", "in", "if", "each",
        "true", "false", "null", "string", "number", "bool", "list", "map", "set", "object", "any",
        "source", "version", "type", "default", "description", "sensitive", "validation"
    )
    override fun isAnnotation(word: String): Boolean = word.startsWith("var.") || word.startsWith("local.")
    override fun isComment(line: String): Boolean = line.startsWith("#") || line.startsWith("//")
}
