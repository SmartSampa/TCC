package com.intellij.publictransportapi;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

/**
 * Created by ruan0408 on 21/02/2016.
 */
public class CorridorTest {

    @BeforeClass
    public static void setUp() throws Exception {
        PublicTransportAPITest.setUp();
    }

    @Test
    public void testGetAllStops() throws Exception {
        List<Stop> allStops = PublicTransportAPITest.campoLimpo.getAllStops();
        Assert.assertEquals(33, allStops.size());
    }
}