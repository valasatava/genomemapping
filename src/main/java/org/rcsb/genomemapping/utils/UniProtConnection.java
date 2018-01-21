package org.rcsb.genomemapping.utils;

/**
 * Created by Yana Valasatava on 11/16/17.
 */
public class UniProtConnection {

    private static String server = "ftp.uniprot.org";
    private static int port = 21;
    private static String user = "anonymous";
    private static String pass = "anonymous@141.161.180.197";

    public static String getServer() {
        return server;
    }

    public static int getPort() {
        return port;
    }

    public static String getUser() {
        return user;
    }

    public static String getPass() {
        return pass;
    }
}
