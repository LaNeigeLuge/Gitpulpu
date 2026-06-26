package com.jetpackduba.gitnuro.avatarproviders

/**
 * Avatar provider that fetches profile images from GitLab.
 *
 * GitLab uses Gravatar as its default avatar backend with identicon fallback.
 * The API endpoint accepts SHA256-hashed emails directly.
 *
 * For self-hosted GitLab instances, users should configure a custom provider.
 */
class GitLabAvatarProvider : AvatarProvider {
    override fun getAvatarUrl(hashedEmail: String): String {
        return "https://gitlab.com/uploads/-/system/user/avatar/email/${hashedEmail}?s=60&d=identicon"
    }

    override fun getAvatarUrl(rawEmail: String, hashedEmail: String): String {
        // GitLab's avatar endpoint also supports Gravatar fallback via identicon
        // Using Gravatar as the backend since GitLab mirrors it
        return "https://www.gravatar.com/avatar/${hashedEmail}?s=60&d=identicon"
    }
}
