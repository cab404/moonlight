package com.cab404.moonlight.util.tests;

import com.cab404.moonlight.framework.AccessProfile;
import com.cab404.moonlight.util.SU;

import java.util.List;

/**
 * Simple test launcher.
 *
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
			System.out.println(SU.fillSpaces(test.title() + "", 50, 1, SU.Gravity.LEFT) + " [  OK  ] ");
			return true;
		} catch (Throwable e) {
			System.out.println(SU.fillSpaces(test.title() + "", 50, 1, SU.Gravity.LEFT) + " [ fail ] ");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Launches tests and finishes with System.exit(). Exit code will be number of failed tests.
	 */
	public void launch(List<Class<? extends Test>> test_classes) {
		int i = 0;

		try {
			long timeMs = System.currentTimeMillis();

			for (Class<? extends Test> test : test_classes) {
				if (test(test.getConstructor().newInstance()))
					i++;
			}

			System.out.println(i + "/" + test_classes.size() + " passed in " + (System.currentTimeMillis() - timeMs) / 1000 + " seconds.");

		} catch (Throwable e) {
			throw new RuntimeException("Cannot create tests!", e);
		}

		System.exit(test_classes.size() - i);
	}
}
