package com.gracelogic.platform.user.security;

import com.gracelogic.platform.user.model.Grant;

import java.util.Set;

public class SecurityUtils {
    public static boolean permissionGranted(Set<Grant> grants, String permissionName) {
        for (Grant grant : grants) {
            if (grant.getName().equalsIgnoreCase(permissionName)) {
                return true;
            }
        }
        return false;
    }

}
