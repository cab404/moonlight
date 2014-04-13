package com.cab404.moonlight.tests;

import com.cab404.moonlight.framework.AccessProfile;
import com.cab404.moonlight.util.SU;

/**
 * Простой тест запросов.
 *
 * @author cab404
 */
public abstract class Test {

    public Test() {}

    protected void assertNonNull(String tag, Object a) {

    }

    protected <T extends Comparable<T>> void assertLessOrEquals(String tag, T a, T b) {
        if (a == null ? b == null : a.compareTo(b) <= 0)
            return;
        throw new AssertionError(tag + ": '" + String.valueOf(a) + "' > '" + String.valueOf(b) + "'");
    }

    protected <T extends Comparable<T>> void assertLess(String tag, T a, T b) {
        if (a == null ? b == null : a.compareTo(b) < 0)
            return;
        throw new AssertionError(tag + ": '" + String.valueOf(a) + "' >= '" + String.valueOf(b) + "'");
    }

    protected void assertEquals(String tag, Object a, Object b) {
        if (a == null ? b == null : a.equals(b))
            return;
        throw new AssertionError(tag + ": '" + String.valueOf(a) + "' != '" + String.valueOf(b) + "'");
    }

    public abstract void test(AccessProfile profile);
    public CharSequence title() {
        return SU.camelCaseToStr(this.getClass().getSimpleName());
    }
}
