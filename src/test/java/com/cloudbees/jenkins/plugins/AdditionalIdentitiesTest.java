package com.cloudbees.jenkins.plugins;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

import hudson.model.User;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AdditionalIdentitiesTest {

    @Test
    @DisplayName("Should create AdditionalIdentities with the correct list of identities")
    void testConstructor() {
        // Given
        var identity1 = new AdditionalIdentity("user123", "github");
        var identity2 = new AdditionalIdentity("user456", "gitlab");
        var identities = Arrays.asList(identity1, identity2);

        // When
        var additionalIdentities = new AdditionalIdentities(identities);

        // Then
        assertNotNull(additionalIdentities);
        assertNotNull(additionalIdentities.getIdentities());
        assertEquals(2, additionalIdentities.getIdentities().size());
        assertEquals(identity1, additionalIdentities.getIdentities().get(0));
        assertEquals(identity2, additionalIdentities.getIdentities().get(1));
    }

    @Test
    @DisplayName("Should handle empty list of identities")
    void testConstructorWithEmptyList() {
        // Given
        List<AdditionalIdentity> identities = new ArrayList<>();

        // When
        var additionalIdentities = new AdditionalIdentities(identities);

        // Then
        assertNotNull(additionalIdentities);
        assertNotNull(additionalIdentities.getIdentities());
        assertEquals(0, additionalIdentities.getIdentities().size());
    }

    @Test
    @DisplayName("Descriptor should have correct display name and return null for new instance")
    void testDescriptor() {
        // Given
        var descriptor = new AdditionalIdentities.DescriptorImpl();
        var user = mock(User.class);

        // When
        var displayName = descriptor.getDisplayName();
        var newInstance = descriptor.newInstance(user);

        // Then
        assertEquals("Additional user identities", displayName);
        assertNull(newInstance);
    }
}
