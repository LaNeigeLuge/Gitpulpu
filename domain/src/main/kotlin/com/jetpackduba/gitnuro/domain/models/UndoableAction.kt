package com.jetpackduba.gitnuro.domain.models

/**
 * A recently-performed action that can be reversed with one click — the "safety net".
 * [summary] is shown to the user (e.g. on an Undo affordance).
 */
sealed interface UndoableAction {
    val summary: String

    /** Was on [previousRef] (a ref name like refs/heads/main, or a commit hash if detached). */
    data class Checkout(val previousRef: String, override val summary: String) : UndoableAction

    /** [branch] pointed at [previousHash] before it was reset. */
    data class ResetBranch(val branch: String, val previousHash: String, override val summary: String) : UndoableAction

    /** [branch] pointed at [hash] before it was deleted. */
    data class DeleteBranch(val branch: String, val hash: String, override val summary: String) : UndoableAction
}
