package org.rcsb.genomemapping.utils;

/**
 * Created by Yana Valasatava on 12/12/17.
 */
public class BooleanQueryParam {

    private static final BooleanQueryParam FALSE = new BooleanQueryParam(false);
    private static final BooleanQueryParam TRUE = new BooleanQueryParam(true);
    private boolean value;

    private BooleanQueryParam(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return this.value;
    }

    public static BooleanQueryParam valueOf(String value) {

        if (value == null || value.isEmpty())
            return BooleanQueryParam.FALSE;

        switch (value.toLowerCase()) {
            case "true":
            case "yes":
            case "y": {
                return BooleanQueryParam.TRUE;
            }
            default: {
                return BooleanQueryParam.FALSE;
            }
        }
    }
}