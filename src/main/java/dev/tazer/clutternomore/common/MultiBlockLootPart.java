package dev.tazer.clutternomore.common;

final class MultiBlockLootPart {
    private MultiBlockLootPart() {}

    static boolean isNonLower(String property, String value) {
        return (property.equals("half") || property.equals("third")) && (value.equals("middle") || value.equals("upper"));
    }
}
