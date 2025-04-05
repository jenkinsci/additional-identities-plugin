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
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

/**
 * Tests for the {@link AdditionalIdentities} class.
 */
public class AdditionalIdentitiesTest {

    @Test
    public void testConstructorAndGetters() {
        List<AdditionalIdentity> identityList = new ArrayList<>();
        identityList.add(new AdditionalIdentity("user1", "ldap"));
        identityList.add(new AdditionalIdentity("user2", "github"));

        AdditionalIdentities identities = new AdditionalIdentities(identityList);

        assertEquals(
                "getIdentities() should return the list passed to the constructor",
                identityList,
                identities.getIdentities());
        assertEquals(
                "Identities list should have the correct size",
                2,
                identities.getIdentities().size());
    }

    @Test
    public void testDescriptor() {
        AdditionalIdentities.DescriptorImpl descriptor = new AdditionalIdentities.DescriptorImpl();
        assertEquals(
                "Descriptor should have correct display name",
                "Additional user identities",
                descriptor.getDisplayName());
        assertNull("newInstance should return null", descriptor.newInstance((User) null));
    }
}
