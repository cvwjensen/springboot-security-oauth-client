package com.example.springsecurityoauthclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Configuration
public class OAuthClientConfiguration {

    @Bean
        // Register the client registration with the authorization server
        // See the well-known config endpoint here http://localhost:8080/realms/springboot/.well-known/openid-configuration
    ReactiveClientRegistrationRepository clientRegistrations(
            @Value("${spring.security.oauth2.client.provider.external.token-uri}") String token_uri,
            @Value("${spring.security.oauth2.client.registration.external.client-id}") String client_id,
            @Value("${spring.security.oauth2.client.registration.external.client-secret}") String client_secret,
            @Value("${spring.security.oauth2.client.registration.external.scope}") List<String> scope,
            @Value("${spring.security.oauth2.client.registration.external.authorization-grant-type}") String authorizationGrantType

    ) {
        ClientRegistration registration = ClientRegistration
                .withRegistrationId("external") // References the 'external' configuration in application.properties
                .tokenUri(token_uri)
                .clientId(client_id)
                .clientSecret(client_secret)
                .scope(scope)
                .authorizationGrantType(new AuthorizationGrantType(authorizationGrantType))
                .build();
        return new InMemoryReactiveClientRegistrationRepository(registration);
    }

    @Bean
    WebClient webClient(ReactiveClientRegistrationRepository clientRegistrations) {
        // An OAuth2AuthorizedClientService that stores Authorized Client(s) in-memory.
        InMemoryReactiveOAuth2AuthorizedClientService clientService = new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrations);

        // An OAuth2AuthorizedClientManager that uses an OAuth2AuthorizedClientService to manage OAuth 2.0 Authorized Client(s).
        AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager authorizedClientManager = new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(clientRegistrations, clientService);

        // Provides an easy mechanism for using an OAuth2AuthorizedClient to make OAuth2 requests by including the token as a Bearer Token.
        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);

        // Set the default ClientRegistration.Id that will be used for requests.
        oauth.setDefaultClientRegistrationId("external");

        // Create a WebClient that is aware of the OAuth2 token and uses the 'external' client to enrich the request with an AccessToken
        return WebClient.builder()
                .filter(oauth)
                .build();

    }


}