package com.cloudbees.jenkins.plugins;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AdditionalIdentityTest {

    @Test
    @DisplayName("Should create an AdditionalIdentity with the correct values")
    void testConstructor() {
        // Given
        var id = "user123";
        var realm = "github";

        // When
        var identity = new AdditionalIdentity(id, realm);

        // Then
        assertNotNull(identity);
        assertEquals(id, identity.getId());
        assertEquals(realm, identity.getRealm());
    }

    @Test
    @DisplayName("Descriptor should have correct display name")
    void testDescriptor() {
        // Given
        var descriptor = new AdditionalIdentity.DescriptorImpl();

        // When
        var displayName = descriptor.getDisplayName();

        // Then
        assertEquals("Additional identity", displayName);
    }
}
