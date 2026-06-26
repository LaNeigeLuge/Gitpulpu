package com.jetpackduba.gitnuro.avatarproviders

interface AvatarProvider {
    fun getAvatarUrl(hashedEmail: String): String?

    /**
     * Resolve an avatar URL from the raw email address.
     * Providers that can extract user info from the email format
     * (e.g., GitHub noreply addresses) should override this.
     *
     * By default, delegates to the hash-based method.
     */
    fun getAvatarUrl(rawEmail: String, hashedEmail: String): String? {
        return getAvatarUrl(hashedEmail)
    }
}
