package com.jetpackduba.gitnuro.domain

import com.jetpackduba.gitnuro.domain.models.ConflictBlock
import com.jetpackduba.gitnuro.domain.models.ConflictChoice
import com.jetpackduba.gitnuro.domain.models.ConflictedFile

/**
 * Parses/serializes git conflict markers.
 *
 * Handles both the default 2-way markers and the diff3 3-way form:
 *
 *     <<<<<<< ours
 *     our lines
 *     ||||||| base        (diff3 only — the base section is dropped)
 *     base lines
 *     =======
 *     their lines
 *     >>>>>>> theirs
 */
object ConflictParser {
    private const val OURS_START = "<<<<<<<"
    private const val BASE_START = "|||||||"
    private const val SEPARATOR = "======="
    private const val THEIRS_END = ">>>>>>>"

    fun parse(filePath: String, content: String): ConflictedFile {
        val lines = content.split("\n")
        val blocks = mutableListOf<ConflictBlock>()
        val unchanged = mutableListOf<String>()

        var i = 0
        while (i < lines.size) {
            val line = lines[i]
            if (line.startsWith(OURS_START)) {
                if (unchanged.isNotEmpty()) {
                    blocks.add(ConflictBlock.Unchanged(unchanged.toList()))
                    unchanged.clear()
                }

                val ours = mutableListOf<String>()
                val theirs = mutableListOf<String>()
                i++

                // Collect "ours" until base marker or separator
                while (i < lines.size &&
                    !lines[i].startsWith(BASE_START) &&
                    !lines[i].startsWith(SEPARATOR)
                ) {
                    ours.add(lines[i]); i++
                }

                // Skip the base section entirely (diff3), up to the separator
                if (i < lines.size && lines[i].startsWith(BASE_START)) {
                    while (i < lines.size && !lines[i].startsWith(SEPARATOR)) i++
                }

                // Skip the separator itself
                if (i < lines.size && lines[i].startsWith(SEPARATOR)) i++

                // Collect "theirs" until the closing marker
                while (i < lines.size && !lines[i].startsWith(THEIRS_END)) {
                    theirs.add(lines[i]); i++
                }

                // Skip the closing marker
                if (i < lines.size && lines[i].startsWith(THEIRS_END)) i++

                blocks.add(ConflictBlock.Conflict(ours, theirs))
            } else {
                unchanged.add(line)
                i++
            }
        }

        if (unchanged.isNotEmpty()) {
            blocks.add(ConflictBlock.Unchanged(unchanged.toList()))
        }

        return ConflictedFile(filePath, blocks)
    }

    /**
     * Rebuilds file content from the user's per-conflict choices. [choices] maps the
     * index of each [ConflictBlock.Conflict] (in order of appearance) to the chosen side.
     * A conflict with no entry defaults to keeping "ours".
     */
    fun resolve(file: ConflictedFile, choices: Map<Int, ConflictChoice>): String {
        val out = mutableListOf<String>()
        var conflictIndex = 0

        for (block in file.blocks) {
            when (block) {
                is ConflictBlock.Unchanged -> out.addAll(block.lines)
                is ConflictBlock.Conflict -> {
                    when (choices[conflictIndex] ?: ConflictChoice.OURS) {
                        ConflictChoice.OURS -> out.addAll(block.ours)
                        ConflictChoice.THEIRS -> out.addAll(block.theirs)
                        ConflictChoice.OURS_THEN_THEIRS -> {
                            out.addAll(block.ours); out.addAll(block.theirs)
                        }
                        ConflictChoice.THEIRS_THEN_OURS -> {
                            out.addAll(block.theirs); out.addAll(block.ours)
                        }
                    }
                    conflictIndex++
                }
            }
        }

        return out.joinToString("\n")
    }

    fun hasConflictMarkers(content: String): Boolean =
        content.lineSequence().any { it.startsWith(OURS_START) }
}
