package com.jetpackduba.gitnuro.domain.models

/**
 * A conflicting file parsed into ordered blocks. Unchanged blocks are shared context;
 * conflict blocks hold the "ours" and "theirs" variants the user must choose between.
 */
data class ConflictedFile(
    val filePath: String,
    val blocks: List<ConflictBlock>,
) {
    val conflictCount: Int get() = blocks.count { it is ConflictBlock.Conflict }
}

sealed interface ConflictBlock {
    /** Lines identical on both sides — kept verbatim. */
    data class Unchanged(val lines: List<String>) : ConflictBlock

    /** A conflicting region. [ours] is the current branch, [theirs] is the incoming one. */
    data class Conflict(
        val ours: List<String>,
        val theirs: List<String>,
    ) : ConflictBlock
}

/** Which side(s) of a single [ConflictBlock.Conflict] the user chose to keep. */
enum class ConflictChoice {
    OURS,
    THEIRS,
    OURS_THEN_THEIRS,
    THEIRS_THEN_OURS,
}
