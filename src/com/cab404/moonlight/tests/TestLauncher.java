package com.cab404.moonlight.tests;

import com.cab404.moonlight.util.SU;
import com.cab404.moonlight.util.U;
import com.cab404.moonlight.util.modular.AccessProfile;

import java.util.List;

/**
 * @author cab404
 */
public class TestLauncher {
    private AccessProfile profile;

    public TestLauncher(AccessProfile profile) {
        this.profile = profile;
    }

    public boolean test(Test test) {
        try {
            test.test(profile);
            U.v(SU.fillSpaces(test.title() + "", 50, 1, SU.FillType.LEFT) + " [  OK  ] ");
            return true;
        } catch (Throwable e) {
            U.v(SU.fillSpaces(test.title() + "", 50, 1, SU.FillType.LEFT) + " [ fail ] ");
            U.w(e);
            return false;
        }
    }

    public void launch(List<Class<? extends Test>> test_classes) {
        try {
            int i = 0;

            for (Class<? extends Test> test : test_classes) {
                if (test(test.getConstructor().newInstance()))
                    i++;
            }
            U.v(i + "/" + test_classes.size() + " passed.");

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
