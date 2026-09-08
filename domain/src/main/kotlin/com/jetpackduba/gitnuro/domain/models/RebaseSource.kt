package com.jetpackduba.gitnuro.domain.models

/**
 * Who is being rebased onto whom, read from the in-progress rebase state.
 *
 * [branchName] is the branch that will be rewritten (null while HEAD is detached),
 * [ontoName] the branch/tag the commits are replayed on when a ref points at [ontoHash],
 * otherwise the abbreviated hash.
 */
data class RebaseSource(
    val branchName: String?,
    val ontoName: String,
    val ontoHash: String,
)
