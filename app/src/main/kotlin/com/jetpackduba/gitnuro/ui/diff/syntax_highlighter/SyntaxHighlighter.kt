package com.jetpackduba.gitnuro.ui.diff.syntax_highlighter

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import com.jetpackduba.gitnuro.domain.extensions.removeLineDelimiters
import com.jetpackduba.gitnuro.theme.diffAnnotation
import com.jetpackduba.gitnuro.theme.diffComment
import com.jetpackduba.gitnuro.theme.diffKeyword

/**
 * Tokenizer result: a word and its start position in the original string.
 */
private data class Token(val word: String, val start: Int)

/**
 * Regex that splits code into word-boundary tokens.
 * Matches identifiers, numbers, operators, and individual punctuation.
 * Preserves positions so syntax spans map correctly to the source string.
 */
private val TOKEN_PATTERN = Regex("""[a-zA-Z_]\w*|0[xXbBoO][\da-fA-F_]+|\d[\d_.]*\w*|"(?:[^"\\]|\\.)*"|'(?:[^'\\]|\\.)*'|//.*|#.*|/\*.*?\*/|[^\s]""")

abstract class SyntaxHighlighter {
    private val keywords: List<String> by lazy {
        loadKeywords()
    }

    private val keywordSet: Set<String> by lazy {
        keywords.toHashSet()
    }

    /**
     * Tokenizes a line of code into word-boundary tokens with positions.
     * Unlike split(" "), this correctly isolates keywords from punctuation:
     * "if(x)" → [Token("if",0), Token("(",2), Token("x",3), Token(")",4)]
     */
    private fun tokenize(text: String): List<Token> {
        return TOKEN_PATTERN.findAll(text).map { Token(it.value, it.range.first) }.toList()
    }

    fun syntaxHighlight(
        annotatedString: AnnotatedString,
        commentColor: Color,
        keywordColor: Color,
        annotationColor: Color,
    ): AnnotatedString {
        val cleanText = annotatedString.text

        val builder = AnnotatedString.Builder()
        builder.append(cleanText)

        for (spanStyleRange in annotatedString.spanStyles) {
            builder.addStyle(spanStyleRange.item, spanStyleRange.start, spanStyleRange.end)
        }

        if (isComment(cleanText.trimStart())) {
            builder.addStyle(
                style = SpanStyle(color = commentColor),
                start = 0,
                end = cleanText.count(),
            )
        } else {
            val tokens = tokenize(cleanText)

            for (token in tokens) {
                val start = token.start
                val end = start + token.word.length

                if (keywordSet.contains(token.word)) {
                    builder.addStyle(
                        style = SpanStyle(color = keywordColor),
                        start = start,
                        end = end,
                    )
                } else if (isAnnotation(token.word)) {
                    builder.addStyle(
                        style = SpanStyle(color = annotationColor),
                        start = start,
                        end = end,
                    )
                } else if (isStringLiteral(token.word)) {
                    builder.addStyle(
                        style = SpanStyle(color = commentColor),
                        start = start,
                        end = end,
                    )
                } else if (isNumber(token.word)) {
                    builder.addStyle(
                        style = SpanStyle(color = annotationColor),
                        start = start,
                        end = end,
                    )
                }
            }
        }

        return builder.toAnnotatedString()
    }

    @Composable
    fun syntaxHighlight(text: String): AnnotatedString {
        val cleanText = text.replace(
            "\t",
            "    "
        ).removeLineDelimiters()

        return if (isComment(cleanText.trimStart())) {
            AnnotatedString(cleanText, spanStyle = SpanStyle(color = MaterialTheme.colors.diffComment))
        } else {
            val tokens = tokenize(cleanText)

            val builder = AnnotatedString.Builder()
            var lastEnd = 0

            for (token in tokens) {
                // Append any whitespace/gap between tokens
                if (token.start > lastEnd) {
                    builder.append(cleanText.substring(lastEnd, token.start))
                }

                when {
                    keywordSet.contains(token.word) -> {
                        builder.append(
                            AnnotatedString(
                                token.word,
                                spanStyle = SpanStyle(color = MaterialTheme.colors.diffKeyword)
                            )
                        )
                    }

                    isAnnotation(token.word) -> {
                        builder.append(
                            AnnotatedString(
                                token.word,
                                spanStyle = SpanStyle(color = MaterialTheme.colors.diffAnnotation)
                            )
                        )
                    }

                    isStringLiteral(token.word) -> {
                        builder.append(
                            AnnotatedString(
                                token.word,
                                spanStyle = SpanStyle(color = MaterialTheme.colors.diffComment)
                            )
                        )
                    }

                    isNumber(token.word) -> {
                        builder.append(
                            AnnotatedString(
                                token.word,
                                spanStyle = SpanStyle(color = MaterialTheme.colors.diffAnnotation)
                            )
                        )
                    }

                    else -> {
                        builder.append(token.word)
                    }
                }

                lastEnd = token.start + token.word.length
            }

            // Append any trailing text
            if (lastEnd < cleanText.length) {
                builder.append(cleanText.substring(lastEnd))
            }

            builder.toAnnotatedString()
        }
    }

    abstract fun isAnnotation(word: String): Boolean
    abstract fun isComment(line: String): Boolean
    abstract fun loadKeywords(): List<String>

    open fun isStringLiteral(word: String): Boolean {
        return (word.startsWith("\"") && word.endsWith("\"")) ||
                (word.startsWith("'") && word.endsWith("'"))
    }

    open fun isNumber(word: String): Boolean {
        if (word.isEmpty()) return false
        val first = word[0]
        return first.isDigit() || (first == '.' && word.length > 1 && word[1].isDigit())
    }
}

fun getSyntaxHighlighterFromExtension(extension: String?): SyntaxHighlighter {
    val matchingHighlightLanguage = HighlightLanguagesSupported.entries.firstOrNull { language ->
        language.extensions.contains(extension)
    }

    return matchingHighlightLanguage?.highlighter?.invoke() ?: DefaultSyntaxHighlighter()
}

private enum class HighlightLanguagesSupported(val extensions: List<String>, val highlighter: () -> SyntaxHighlighter) {
    Kotlin(listOf("kt", "kts"), { KotlinSyntaxHighlighter() }),
    Rust(listOf("rs"), { RustSyntaxHighlighter() }),
    TypeScript(
        listOf("js", "jsx", "ts", "tsx", "vue", "astro", "svelte"),
        { TypeScriptSyntaxHighlighter() }
    ), // JS & various frameworks files also included
    Python(listOf("py"), { PythonSyntaxHighlighter() }),
    Java(listOf("java"), { JavaSyntaxHighlighter() }),
    CSharp(listOf("cs"), { CSharpSyntaxHighlighter() }),
    Cpp(listOf("c", "cpp", "c", "h", "hh", "hpp"), { CppSyntaxHighlighter() }), // C files also included
    PHP(listOf("php"), { PhpSyntaxHighlighter() }),
    SQL(listOf("sql"), { SQLSyntaxHighlighter() }),
    Ruby(listOf("rb"), { RubySyntaxHighlighter() }),
    Go(listOf("go"), { GoSyntaxHighlighter() }),
    Dart(listOf("dart"), { DartSyntaxHighlighter() }),
    Swift(listOf("swift"), { SwiftSyntaxHighlighter() }),
    Scala(listOf("scala", "sc"), { ScalaSyntaxHighlighter() }),
    Lua(listOf("lua"), { LuaSyntaxHighlighter() }),
    ObjectiveC(listOf("m", "mm"), { ObjectiveCSyntaxHighlighter() }),
    Bash(listOf("sh", "bash", "zsh"), { BashSyntaxHighlighter() }),
    Yaml(listOf("yml", "yaml"), { YamlSyntaxHighlighter() }),
    Toml(listOf("toml"), { TomlSyntaxHighlighter() }),
    Json(listOf("json"), { JsonSyntaxHighlighter() }),
    Xml(listOf("xml", "html", "htm", "xhtml", "svg"), { XmlSyntaxHighlighter() }),
    Css(listOf("css", "scss", "sass", "less"), { CssSyntaxHighlighter() }),
    Markdown(listOf("md", "markdown"), { MarkdownSyntaxHighlighter() }),
    Dockerfile(listOf("dockerfile"), { DockerfileSyntaxHighlighter() }),
    Terraform(listOf("tf", "tfvars"), { TerraformSyntaxHighlighter() }),
}
