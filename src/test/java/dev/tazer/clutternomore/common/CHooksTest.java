package dev.tazer.clutternomore.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CHooksTest {
    @Test
    void suppressesOnlyNonLowerMultiBlockParts() {
        assertFalse(MultiBlockLootPart.isNonLower("half", "lower"));
        assertTrue(MultiBlockLootPart.isNonLower("half", "upper"));

        assertFalse(MultiBlockLootPart.isNonLower("third", "lower"));
        assertTrue(MultiBlockLootPart.isNonLower("third", "middle"));
        assertTrue(MultiBlockLootPart.isNonLower("third", "upper"));
    }

    @Test
    void ignoresUnrelatedPropertiesAndValues() {
        assertFalse(MultiBlockLootPart.isNonLower("facing", "upper"));
        assertFalse(MultiBlockLootPart.isNonLower("half", "top"));
    }
}
