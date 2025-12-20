package com.ecommerce.adminapp.auth

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object AuthManager {
    data class TokenState(
        val uid: String? = null,
        val email: String? = null,
        val token: String? = null,
        val expirationTimestamp: Long? = null
    )

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _tokenState = MutableStateFlow(TokenState())
    val tokenState: StateFlow<TokenState> = _tokenState

    init {
        auth.addAuthStateListener { fa ->
            val user = fa.currentUser
            if (user == null) {
                _tokenState.value = TokenState()
            } else {
                user.getIdToken(false).addOnSuccessListener { res ->
                    _tokenState.value = TokenState(
                        uid = user.uid,
                        email = user.email,
                        token = res.token,
                        expirationTimestamp = res.expirationTimestamp
                    )
                }.addOnFailureListener {
                    _tokenState.value = TokenState(uid = user.uid, email = user.email)
                }
            }
        }

        auth.addIdTokenListener(FirebaseAuth.IdTokenListener { fa ->
            val user = fa.currentUser
            if (user == null) {
                _tokenState.value = TokenState()
            } else {
                user.getIdToken(false).addOnSuccessListener { res ->
                    _tokenState.value = TokenState(
                        uid = user.uid,
                        email = user.email,
                        token = res.token,
                        expirationTimestamp = res.expirationTimestamp
                    )
                }.addOnFailureListener {
                    _tokenState.value = TokenState(uid = user.uid, email = user.email)
                }
            }
        })
    }

    fun getFreshIdToken(onResult: (String?) -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            onResult(null)
            return
        }
        user.getIdToken(true).addOnSuccessListener { res ->
            _tokenState.value = _tokenState.value.copy(
                token = res.token,
                expirationTimestamp = res.expirationTimestamp
            )
            onResult(res.token)
        }.addOnFailureListener { onResult(null) }
    }
}
