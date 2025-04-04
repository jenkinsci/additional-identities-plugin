/*
 * The MIT License
 *
 *  Copyright (c) 2012, CloudBees, Inc.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package com.cloudbees.jenkins.plugins;

import static org.junit.Assert.*;

import hudson.model.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

/**
 * Comprehensive integration tests for the Additional Identities plugin functionality.
 *
 * These tests verify that:
 * 1. Users can have multiple identities across different realms
 * 2. Users can be correctly identified by any of their registered identities
 * 3. The resolver respects realm constraints when provided
 */
public class AdditionalIdentitiesIntegrationTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    private AdditionalIdentityResolver resolver;
    private User alice;
    private User bob;

    @Before
    public void setUp() throws Exception {
        resolver = new AdditionalIdentityResolver();

        // Create test users
        alice = User.getById("alice", true);
        bob = User.getById("bob", true);

        // Configure Alice with GitHub and LDAP identities
        List<AdditionalIdentity> aliceIdentities = new ArrayList<>();
        aliceIdentities.add(new AdditionalIdentity("alice.smith", "github"));
        aliceIdentities.add(new AdditionalIdentity("asmith", "ldap"));
        alice.addProperty(new AdditionalIdentities(aliceIdentities));

        // Configure Bob with Bitbucket and JIRA identities
        List<AdditionalIdentity> bobIdentities = new ArrayList<>();
        bobIdentities.add(new AdditionalIdentity("bob.jones", "bitbucket"));
        bobIdentities.add(new AdditionalIdentity("bjones", "jira"));
        bob.addProperty(new AdditionalIdentities(bobIdentities));
    }

    @Test
    public void testMultipleUsersWithIdentities() throws IOException {
        // Test GitHub identity for Alice
        Map<String, Object> githubContext = new HashMap<>();
        githubContext.put(User.CanonicalIdResolver.REALM, "github");
        String resolved = resolver.resolveCanonicalId("alice.smith", githubContext);
        assertEquals("Alice should be identified by her GitHub identity", alice.getId(), resolved);

        // Test LDAP identity for Alice
        Map<String, Object> ldapContext = new HashMap<>();
        ldapContext.put(User.CanonicalIdResolver.REALM, "ldap");
        resolved = resolver.resolveCanonicalId("asmith", ldapContext);
        assertEquals("Alice should be identified by her LDAP identity", alice.getId(), resolved);

        // Test Bitbucket identity for Bob
        Map<String, Object> bitbucketContext = new HashMap<>();
        bitbucketContext.put(User.CanonicalIdResolver.REALM, "bitbucket");
        resolved = resolver.resolveCanonicalId("bob.jones", bitbucketContext);
        assertEquals("Bob should be identified by his Bitbucket identity", bob.getId(), resolved);
    }

    @Test
    public void testResolvingWithoutRealm() throws IOException {
        // All identities should resolve without a realm constraint
        assertEquals(
                "Should resolve Alice by GitHub ID without realm",
                alice.getId(),
                resolver.resolveCanonicalId("alice.smith", null));
        assertEquals(
                "Should resolve Alice by LDAP ID without realm",
                alice.getId(),
                resolver.resolveCanonicalId("asmith", null));
        assertEquals(
                "Should resolve Bob by Bitbucket ID without realm",
                bob.getId(),
                resolver.resolveCanonicalId("bob.jones", null));
        assertEquals(
                "Should resolve Bob by JIRA ID without realm",
                bob.getId(),
                resolver.resolveCanonicalId("bjones", null));
    }

    @Test
    public void testRealmConstraints() throws IOException {
        // Try to resolve Alice's GitHub identity with LDAP realm - should fail
        Map<String, Object> ldapContext = new HashMap<>();
        ldapContext.put(User.CanonicalIdResolver.REALM, "ldap");
        String resolved = resolver.resolveCanonicalId("alice.smith", ldapContext);
        assertNull("Alice's GitHub identity should not resolve in LDAP realm", resolved);

        // Try to resolve Bob's JIRA identity with GitHub realm - should fail
        Map<String, Object> githubContext = new HashMap<>();
        githubContext.put(User.CanonicalIdResolver.REALM, "github");
        resolved = resolver.resolveCanonicalId("bjones", githubContext);
        assertNull("Bob's JIRA identity should not resolve in GitHub realm", resolved);
    }

    @Test
    public void testNonExistentIdentities() throws IOException {
        // Try to resolve identities that don't exist
        assertNull("Non-existent identity should not resolve", resolver.resolveCanonicalId("nonexistent", null));

        Map<String, Object> githubContext = new HashMap<>();
        githubContext.put(User.CanonicalIdResolver.REALM, "github");
        assertNull(
                "Non-existent identity should not resolve even with realm",
                resolver.resolveCanonicalId("nonexistent", githubContext));
    }
}
