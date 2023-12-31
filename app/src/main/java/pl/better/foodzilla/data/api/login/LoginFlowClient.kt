package pl.better.foodzilla.data.api.login

import com.apollographql.apollo3.ApolloClient
import pl.better.foodzilla.LoginMutation
import pl.better.foodzilla.RegisterMutation
import pl.better.foodzilla.EditCustomerMutation
import pl.better.foodzilla.ResetPasswordMutation
import pl.better.foodzilla.ResetPasswordRequestMutation
import pl.better.foodzilla.data.mappers.login.toCustomer
import pl.better.foodzilla.data.mappers.login.toLogin
import pl.better.foodzilla.data.models.login.Customer
import pl.better.foodzilla.data.models.login.Login
import pl.better.foodzilla.utils.exception.GraphQLErrorResponseException
import javax.inject.Inject
import kotlin.streams.toList

class LoginFlowClient @Inject constructor(
    private val apolloClient: ApolloClient
) {

    suspend fun login(login: String, password: String): Login? {
        val response = apolloClient
            .mutation(LoginMutation(login, password))
            .execute()
        if (response.data?.login == null && response.errors != null) {
            throw GraphQLErrorResponseException(response.errors!!.stream().map { it.message }.toList())
        }
        return response
            .data
            ?.login
            ?.toLogin()
    }

    suspend fun register(firstname: String, lastname: String, login: String, password: String, email: String): Customer? {
        val response = apolloClient
            .mutation(RegisterMutation(firstname, lastname, login, password, email))
            .execute()
        if (response.data?.createCustomer == null && response.errors != null) {
            throw GraphQLErrorResponseException(response.errors!!.stream().map { it.message }.toList())
        }
        return response
            .data
            ?.createCustomer
            ?.toCustomer()
    }

    suspend fun editCustomer(firstname: String, lastname: String, username: String, password: String, email: String): Customer? {
        val response = apolloClient
            .mutation(EditCustomerMutation(firstname, lastname, username, password, email))
            .execute()
        if (response.data?.editCustomer == null && response.errors != null) {
            throw GraphQLErrorResponseException(response.errors!!.stream().map { it.message }.toList())
        }
        return response
            .data
            ?.editCustomer
            ?.toCustomer()
    }

    suspend fun sendResetPasswordEmail(email: String): Boolean {
        val response = apolloClient
            .mutation(ResetPasswordRequestMutation(email))
            .execute()
        if (response.data?.requestPasswordResetEmail == null && response.errors != null) {
            throw GraphQLErrorResponseException(response.errors!!.stream().map { it.message }.toList())
        }
        return response
            .data
            ?.requestPasswordResetEmail
            ?: false
    }

    suspend fun resetPassword(email: String, token: String, password: String): Boolean {
        val response = apolloClient
            .mutation(ResetPasswordMutation(email, password, token))
            .execute()
        if (response.data?.resetPassword == null && response.errors != null) {
            throw GraphQLErrorResponseException(response.errors!!.stream().map { it.message }.toList())
        }
        return response
            .data
            ?.resetPassword
            ?: false
    }
}
