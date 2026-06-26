package com.jetpackduba.gitnuro.avatarproviders

/**
 * Avatar provider that fetches profile images from GitHub.
 *
 * For GitHub noreply emails ({id}+{username}@users.noreply.github.com),
 * it extracts the user ID directly for a guaranteed avatar hit.
 *
 * For all other emails, falls back to Gravatar with an "identicon" default
 * so every contributor gets a unique geometric avatar even without a
 * Gravatar profile — no more plain letter initials.
 */
class GitHubAvatarProvider : AvatarProvider {
    private val noreplyPattern = Regex("""^(\d+)\+.+@users\.noreply\.github\.com$""")

    override fun getAvatarUrl(hashedEmail: String): String {
        return "https://www.gravatar.com/avatar/${hashedEmail}?s=60&d=identicon"
    }

    override fun getAvatarUrl(rawEmail: String, hashedEmail: String): String {
        val match = noreplyPattern.matchEntire(rawEmail)
        return if (match != null) {
            val userId = match.groupValues[1]
            "https://avatars.githubusercontent.com/u/${userId}?s=60"
        } else {
            getAvatarUrl(hashedEmail)
        }
    }
}
