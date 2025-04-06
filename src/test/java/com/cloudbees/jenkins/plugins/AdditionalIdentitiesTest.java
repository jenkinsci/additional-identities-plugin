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

import static org.junit.jupiter.api.Assertions.*;

import hudson.model.User;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link AdditionalIdentities} class.
 */
public class AdditionalIdentitiesTest {

    @Test
    public void testConstructorAndGetters() {
        var identityList = new ArrayList<AdditionalIdentity>();
        identityList.add(new AdditionalIdentity("user1", "ldap"));
        identityList.add(new AdditionalIdentity("user2", "github"));

        var identities = new AdditionalIdentities(identityList);

        assertEquals(
                identityList,
                identities.getIdentities(),
                "getIdentities() should return the list passed to the constructor");
        assertEquals(2, identities.getIdentities().size(), "Identities list should have the correct size");
    }

    @Test
    public void testDescriptor() {
        var descriptor = new AdditionalIdentities.DescriptorImpl();
        assertEquals(
                "Additional user identities",
                descriptor.getDisplayName(),
                "Descriptor should have correct display name");
        assertNull(descriptor.newInstance((User) null), "newInstance should return null");
    }
}
