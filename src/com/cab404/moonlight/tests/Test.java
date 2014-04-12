package com.cab404.moonlight.tests;

import com.cab404.moonlight.util.modular.AccessProfile;

/**
 * @author cab404
 */
public abstract class Test {

    public Test() {}

    public void assertEquals(Object a, Object b) {
        if (a == null ? b == null : a.equals(b))
            return;
        throw new AssertionError("'" + String.valueOf(a) + "' != '" + String.valueOf(b) + "'");
    }

    public abstract void test(AccessProfile profile);
    public CharSequence title() {
        return this.getClass().getSimpleName();
    }
}
