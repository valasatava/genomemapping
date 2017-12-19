package org.rcsb.genomemapping.controller;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Yana Valasatava on 12/18/17.
 */
public class TestCoordinatesController {

    @Test
    public void testConvertGenomicToProteinCoordinateForwardFirstBase() {

        String orientation = "+";

        int genPos = 13;

        int genStart = 1;
        int genEnd = 45;

        int seqStart = 1;

        int posExpected = 5;
        int posTest = CoordinatesController.convertGenomicToProteinCoordinate(orientation, seqStart, genStart, genPos);
        assertEquals(posExpected, posTest);
    }

    @Test
    public void testConvertGenomicToProteinCoordinateForwardMidleBase() {

        String orientation = "+";

        int genPos = 14;

        int genStart = 1;
        int genEnd = 45;

        int seqStart = 1;

        int posExpected = 5;
        int posTest = CoordinatesController.convertGenomicToProteinCoordinate(orientation, seqStart, genStart, genPos);
        assertEquals(posExpected, posTest);
    }

    @Test
    public void testConvertGenomicToProteinCoordinateForwardLastBase() {

        String orientation = "+";

        int genPos = 15;

        int genStart = 1;
        int genEnd = 45;

        int seqStart = 1;

        int posExpected = 5;
        int posTest = CoordinatesController.convertGenomicToProteinCoordinate(orientation, seqStart, genStart, genPos);
        assertEquals(posExpected, posTest);
    }

    @Test
    public void testConvertGenomicToProteinCoordinateReverseFirstBase() {

        String orientation = "-";

        int genPos = 33;

        int genStart = 45;
        int genEnd = 1;

        int seqStart = 1;

        int posExpected = 5;
        int posTest = CoordinatesController.convertGenomicToProteinCoordinate(orientation, seqStart, genStart, genPos);
        assertEquals(posExpected, posTest);
    }

    @Test
    public void testConvertGenomicToProteinCoordinateReverseMidleBase() {

        String orientation = "-";

        int genPos = 32;

        int genStart = 45;
        int genEnd = 1;

        int seqStart = 1;

        int posExpected = 5;
        int posTest = CoordinatesController.convertGenomicToProteinCoordinate(orientation, seqStart, genStart, genPos);
        assertEquals(posExpected, posTest);
    }

    @Test
    public void testConvertGenomicToProteinCoordinateReverseLastBase() {

        String orientation = "-";

        int genPos = 31;

        int genStart = 45;
        int genEnd = 1;

        int seqStart = 1;

        int posExpected = 5;
        int posTest = CoordinatesController.convertGenomicToProteinCoordinate(orientation, seqStart, genStart, genPos);
        assertEquals(posExpected, posTest);
    }
}