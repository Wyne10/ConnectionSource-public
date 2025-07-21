package org.bigcraft.connection.api;

public final class CSApi {
    private static ConnectionProvider provider;

    public static void setProvider(ConnectionProvider provider) {
        CSApi.provider = provider;
    }

    public static ConnectionProvider getProvider() {
        return provider;
    }
}
