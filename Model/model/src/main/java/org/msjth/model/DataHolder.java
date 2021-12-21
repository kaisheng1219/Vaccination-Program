package org.msjth.model;

public class DataHolder {
    private static Object dataInstance;

    public static Object getDataInstance() {
        return dataInstance;
    }

    public static void setDataInstance(Object object) {
        dataInstance = object;
    }

    public static void clearDataInstance() {
        dataInstance = null;
    }
}
