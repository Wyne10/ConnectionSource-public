package org.bigcraft.connection.api;

/**
 * Static access point for the ConnectionSource plugin's {@link ConnectionProvider}.
 * <p>
 * The provider is set once by the plugin during {@code onEnable} and is not
 * synchronized; consumers should treat {@link #getProvider()} as unusable
 * (possibly {@code null}) until after ConnectionSource has finished enabling.
 */
public final class CSApi {
    private static ConnectionProvider provider;

    public static void setProvider(ConnectionProvider provider) {
        CSApi.provider = provider;
    }

    /**
     * @return the active {@link ConnectionProvider}, or {@code null} if ConnectionSource has not enabled yet
     */
    public static ConnectionProvider getProvider() {
        return provider;
    }
}
