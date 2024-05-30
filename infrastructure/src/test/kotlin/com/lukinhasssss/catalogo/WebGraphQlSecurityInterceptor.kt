package com.lukinhasssss.catalogo

import org.springframework.graphql.server.WebGraphQlInterceptor
import org.springframework.graphql.server.WebGraphQlRequest
import org.springframework.graphql.server.WebGraphQlResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolder
import reactor.core.publisher.Mono

class WebGraphQlSecurityInterceptor(
    private val authorities: MutableList<SimpleGrantedAuthority> = mutableListOf()
) : WebGraphQlInterceptor {

    override fun intercept(request: WebGraphQlRequest, chain: WebGraphQlInterceptor.Chain): Mono<WebGraphQlResponse> {
        if (authorities.isEmpty()) { return chain.next(request) }

        val user = UsernamePasswordAuthenticationToken.authenticated("JohnDoe", "123456", authorities)
        val context = SecurityContextHolder.getContext()
        context.authentication = user

        return chain.next(request).contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)))
    }

    fun setAuthorities(
        vararg authorities: String
    ) {
        this.authorities.clear()

        if (authorities.isNotEmpty()) {
            this.authorities.addAll(authorities.map { SimpleGrantedAuthority(it) })
        }
    }
}
