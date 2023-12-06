package makovacs.dnd.data.dnd.users

/**
 * Represents a user from the [AuthRepository].
 *
 * @param id The unique ID for this user.
 * @param email The user's email address.
 */
data class User(
    // id is necessary since:
    //  - emails might be reused after an account is deleted
    //  - emails aren't always unique (ex. same email might be shared by google and facebook)
    //  - some accounts might not even have emails in the future
    val id: String,
    val email: String
)
