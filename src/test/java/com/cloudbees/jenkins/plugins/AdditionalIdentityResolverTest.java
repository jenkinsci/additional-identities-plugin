package com.cloudbees.jenkins.plugins;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hudson.model.User;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdditionalIdentityResolverTest {

    private AdditionalIdentityResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new AdditionalIdentityResolver();
    }

    @Test
    @DisplayName("Should return null when no users are found")
    void testNoUsersFound() {
        try (MockedStatic<User> userMock = Mockito.mockStatic(User.class)) {
            // Given
            userMock.when(User::getAll).thenReturn(Collections.emptyList());

            // When
            var result = resolver.resolveCanonicalId("test-id", null);

            // Then
            assertNull(result);
        }
    }

    @Test
    @DisplayName("Should return null when user has no additional identities")
    void testUserWithNoAdditionalIdentities() {
        try (MockedStatic<User> userMock = Mockito.mockStatic(User.class)) {
            // Given
            var user = mock(User.class);
            when(user.getProperty(AdditionalIdentities.class)).thenReturn(null);
            userMock.when(User::getAll).thenReturn(Collections.singletonList(user));

            // When
            var result = resolver.resolveCanonicalId("test-id", null);

            // Then
            assertNull(result);
        }
    }

    @Test
    @DisplayName("Should return user ID when identity matches without realm check")
    void testMatchingIdentity() {
        try (MockedStatic<User> userMock = Mockito.mockStatic(User.class)) {
            // Given
            var userId = "user123";
            var identityId = "github-user";

            var user = mock(User.class);
            var identity = new AdditionalIdentity(identityId, null);
            var identities = new AdditionalIdentities(Collections.singletonList(identity));

            when(user.getProperty(AdditionalIdentities.class)).thenReturn(identities);
            when(user.getId()).thenReturn(userId);
            userMock.when(User::getAll).thenReturn(Collections.singletonList(user));

            // When
            var result = resolver.resolveCanonicalId(identityId, null);

            // Then
            assertEquals(userId, result);
        }
    }

    @Test
    @DisplayName("Should not match when realms don't match")
    void testNonMatchingRealm() {
        try (MockedStatic<User> userMock = Mockito.mockStatic(User.class)) {
            // Given
            var userId = "user123";
            var identityId = "github-user";
            var identityRealm = "github";
            var contextRealm = "gitlab";

            var user = mock(User.class);
            var identity = new AdditionalIdentity(identityId, identityRealm);
            var identities = new AdditionalIdentities(Collections.singletonList(identity));

            // Use lenient() to avoid unnecessary stubbing exception
            lenient().when(user.getProperty(AdditionalIdentities.class)).thenReturn(identities);
            lenient().when(user.getId()).thenReturn(userId);
            userMock.when(User::getAll).thenReturn(Collections.singletonList(user));

            Map<String, String> context = new HashMap<>();
            context.put(User.CanonicalIdResolver.REALM, contextRealm);

            // When
            var result = resolver.resolveCanonicalId(identityId, context);

            // Then
            assertNull(result);
        }
    }

    @Test
    @DisplayName("Should match when realms match")
    void testMatchingRealm() {
        try (MockedStatic<User> userMock = Mockito.mockStatic(User.class)) {
            // Given
            var userId = "user123";
            var identityId = "github-user";
            var realm = "github";

            var user = mock(User.class);
            var identity = new AdditionalIdentity(identityId, realm);
            var identities = new AdditionalIdentities(Collections.singletonList(identity));

            when(user.getProperty(AdditionalIdentities.class)).thenReturn(identities);
            when(user.getId()).thenReturn(userId);
            userMock.when(User::getAll).thenReturn(Collections.singletonList(user));

            Map<String, String> context = new HashMap<>();
            context.put(User.CanonicalIdResolver.REALM, "contains-" + realm + "-realm");

            // When
            var result = resolver.resolveCanonicalId(identityId, context);

            // Then
            assertEquals(userId, result);
        }
    }
}
