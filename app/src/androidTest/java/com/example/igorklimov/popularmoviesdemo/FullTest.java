package com.example.igorklimov.popularmoviesdemo;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class FullTest extends TestSuite {

    public static Test suite() {
        return new TestSuiteBuilder(FullTest.class).includeAllPackagesUnderHere().build();
    }

}