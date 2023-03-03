package dev.kata

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.regex.Pattern
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RegisterUserShould {
    @Test
    fun `save a new user`() {
        val mockUserRepository = MockUserRepository()
        val usersController = UserController(mockUserRepository)

        usersController.save(UserDto("elpepe@gmail.com"))

        mockUserRepository.assertThatUserIsSaved()
    }

    @Test
    fun `not save an invalid user`() {
        val mockUserRepository = MockUserRepository()
        val usersController = UserController(mockUserRepository)

        assertThrows<Exception> {
            usersController.save(UserDto("elpepegmail.com"))
        }
        mockUserRepository.assertThatUserIsNotSaved()
    }
}

class UserController(val userService: UserRepository) {
    fun save(user: UserDto) {
        val domainUser = user.toDomainModel()
        userService.save(domainUser)
    }
}

interface UserRepository {
    fun save(user: User)
}


data class UserDto(
    val email: String
) {
    fun toDomainModel(): User {
        return User(Email.create(email))
    }
}

data class Email(
    val value: String
) {
    companion object {
        fun create(value: String): Email {
            val email = Email(value)
            email.validate()
            return email
        }

    }

    fun validate() {
         val isValid = Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(value).matches()
        if (!isValid)
            throw Exception()
    }
}

data class User(
    val email: Email
)
class MockUserRepository : UserRepository {
    var isSaved = false
    override fun save(user: User) {
        isSaved = true
    }

    fun assertThatUserIsSaved() {
        assertTrue { isSaved }
    }

    fun assertThatUserIsNotSaved(){
        assertFalse { isSaved }
    }
}

