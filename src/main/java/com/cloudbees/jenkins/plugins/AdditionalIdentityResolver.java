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

import hudson.Extension;
import hudson.model.User;

import java.util.Map;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
@Extension
public class AdditionalIdentityResolver extends User.CanonicalIdResolver {


    @Override
    public String resolveCanonicalId(String id, Map<String, ?> context) {

        String realm = (String) context.get(User.CanonicalIdResolver.REALM);

        for (User user : User.getAll()) {
            AdditionalIdentities identities = user.getProperty(AdditionalIdentities.class);
            if (identities == null) continue;
            for (AdditionalItentity itentity : identities.getIdentities()) {
                if (itentity.id.equals(id)) {
                    if (realm != null && itentity.realm != null
                        && !realm.contains(itentity.realm)) {
                        // realm don't match
                        continue;
                    }
                    return user.getId();
                }
            }
        }
        return null;
    }
}
