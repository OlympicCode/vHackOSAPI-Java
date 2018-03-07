package net.olympiccode.vhackos.api;

import net.olympiccode.vhackos.api.entities.impl.vHackOSAPIImpl;
import net.olympiccode.vhackos.api.utils.Checks;
import okhttp3.OkHttpClient;

import javax.security.auth.login.LoginException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class vHackOSAPIBuilder {
    private final List<Object> listeners;

    private OkHttpClient.Builder httpClientBuilder = null;
    private int maxReconnectDelay = 900;
    private boolean autoReconnect = true;
    private String username;
    private String password;

    public vHackOSAPIBuilder()
    {
        listeners = new LinkedList<>();
    }


    public vHackOSAPIBuilder setUsername(String username)
    {
        this.username = username;
        return this;
    }


    public vHackOSAPIBuilder setPassword(String password)
    {
        this.password = password;
        return this;
    }

    public vHackOSAPIBuilder setHttpClientBuilder(OkHttpClient.Builder builder)
    {
        this.httpClientBuilder = builder;
        return this;
    }


    public vHackOSAPIBuilder addEventListener(Object... listeners)
    {
        Collections.addAll(this.listeners, listeners);
        return this;
    }


    public vHackOSAPIBuilder removeEventListener(Object... listeners)
    {
        this.listeners.removeAll(Arrays.asList(listeners));
        return this;
    }


    public vHackOSAPI buildAsync() throws LoginException, IllegalArgumentException
    {
        OkHttpClient.Builder httpClientBuilder = this.httpClientBuilder == null ? new OkHttpClient.Builder() : this.httpClientBuilder;
        vHackOSAPIImpl api = new vHackOSAPIImpl(httpClientBuilder, autoReconnect, maxReconnectDelay, 5);

        listeners.forEach(api::addEventListener);
        api.setStatus(vHackOSAPI.Status.INITIALIZED);
        api.login(username, password);
        return api;
    }


    private vHackOSAPI buildBlocking(vHackOSAPI.Status status) throws LoginException, IllegalArgumentException, InterruptedException
    {
        Checks.notNull(status, "Status");
        Checks.check(status.isInit(), "Cannot await the status %s as it is not part of the login cycle!", status);
        vHackOSAPI api = buildAsync();
        while (!api.getStatus().isInit()
                || api.getStatus().ordinal() < status.ordinal())
        {
            if (api.getStatus() == vHackOSAPI.Status.SHUTDOWN)
                throw new IllegalStateException("vHackOSAPI was unable to finish starting up!");
            Thread.sleep(50);
        }

        return api;
    }


    public vHackOSAPI buildBlocking() throws LoginException, IllegalArgumentException, InterruptedException
    {
        return buildBlocking(vHackOSAPI.Status.CONNECTED);
    }
}
