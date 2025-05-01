package com.cloudbees.jenkins.plugins;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import hudson.model.User;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class AdditionalIdentitiesIntegrationTest {

    private JenkinsRule jenkins;

    @BeforeEach
    void setUp(JenkinsRule jenkins) {
        this.jenkins = jenkins;
    }

    @Test
    @DisplayName("Integration test for adding and resolving additional identities")
    void testAddingAndResolvingIdentities() throws Exception {
        // Given
        var userId = "test-user";
        var identityId = "github-user";
        var realm = "github";

        // Create user
        var user = User.getById(userId, true);
        assertNotNull(user);

        // Add additional identity to the user
        var identity = new AdditionalIdentity(identityId, realm);
        var additionalIdentities = new AdditionalIdentities(Collections.singletonList(identity));
        user.addProperty(additionalIdentities);
        user.save();

        // When we try to resolve the identity
        var resolver = new AdditionalIdentityResolver();
        var resolvedId = resolver.resolveCanonicalId(identityId, null);

        // Then the resolved id should match the user id
        assertEquals(userId, resolvedId);
    }

    @Test
    @DisplayName("Integration test for resolving identity with realm check")
    void testResolvingIdentityWithRealm() throws Exception {
        // Given
        var userId = "test-user-realm";
        var identityId = "github-user-realm";
        var realm = "github";

        // Create user
        var user = User.getById(userId, true);
        assertNotNull(user);

        // Add additional identity to the user
        var identity = new AdditionalIdentity(identityId, realm);
        var additionalIdentities = new AdditionalIdentities(Collections.singletonList(identity));
        user.addProperty(additionalIdentities);
        user.save();

        // When we try to resolve the identity with matching realm
        var resolver = new AdditionalIdentityResolver();
        Map<String, String> context = new HashMap<>();
        context.put(User.CanonicalIdResolver.REALM, "jenkins-" + realm);
        var resolvedId = resolver.resolveCanonicalId(identityId, context);

        // Then the resolved id should match the user id
        assertEquals(userId, resolvedId);
    }

    @Test
    @DisplayName("Integration test for configuration via JCasC")
    void testConfigurationAsCode() throws Exception {
        // Given a user with additional identities
        var userId = "jcasc-user";
        var identityId = "jcasc-github-user";
        var realm = "github";

        // Create user
        var user = User.getById(userId, true);
        assertNotNull(user);

        // Add additional identity to the user
        var identity = new AdditionalIdentity(identityId, realm);
        var additionalIdentities = new AdditionalIdentities(Collections.singletonList(identity));
        user.addProperty(additionalIdentities);
        user.save();

        // When and Then
        var userFromJenkins = User.getById(userId, false);
        assertNotNull(userFromJenkins);

        var storedIdentities = userFromJenkins.getProperty(AdditionalIdentities.class);
        assertNotNull(storedIdentities);
        assertEquals(1, storedIdentities.getIdentities().size());
        assertEquals(identityId, storedIdentities.getIdentities().get(0).getId());
        assertEquals(realm, storedIdentities.getIdentities().get(0).getRealm());
    }
}
