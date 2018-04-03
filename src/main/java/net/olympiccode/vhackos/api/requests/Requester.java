package net.olympiccode.vhackos.api.requests;

import net.olympiccode.vhackos.api.entities.impl.vHackOSAPIImpl;
import net.olympiccode.vhackos.api.vHackOSAPI;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

public class Requester {
    private static final Logger LOG = LoggerFactory.getLogger("Requester");
    private final vHackOSAPIImpl api;
    private final OkHttpClient httpClient;
    private long lastRequest = 0;
    private volatile boolean retryOnTimeout = false;
    private int triesLeft = 3;
    private int success = 0;

    public Requester(vHackOSAPI api) {

        this.api = (vHackOSAPIImpl) api;
        this.httpClient = this.api.getHttpClientBuilder().build();
    }

    public static InputStream getBody(okhttp3.Response response) throws IOException {
        String encoding = response.header("content-encoding", "");
        if (Objects.equals(encoding, "gzip"))
            return new GZIPInputStream(Objects.requireNonNull(response.body()).byteStream());
        return Objects.requireNonNull(response.body()).byteStream();
    }

    public vHackOSAPIImpl getAPI() {
        return api;
    }

    public OkHttpClient getHttpClient() {
        return this.httpClient;
    }
    boolean first = true;
    public Response getResponse(Route.CompiledRoute route) {
        if (lastRequest >= System.currentTimeMillis() - 1000) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
               // e.printStackTrace();
            }
        }


        lastRequest = System.currentTimeMillis();
        Request request = new Request.Builder()
                .url(route.getCompiledRoute())
                .addHeader("user-agent", "Dalvik/1.6.0 (Linux; U; Android 4.4.4; SM-N935F Build/KTU84P)")
                .addHeader("Accept-Encoding", "gzip").build();
        final Response[] response = new Response[1];
        okhttp3.Response r;
        try {
            r = httpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        if (r.isSuccessful()) {
            response[0] = new Response(r);
        } else {
            throw new RuntimeException("Failed to get response from vHackOS " + r.code() + " {" + route.getCompiledRoute() + "}");
        }
        try {
            JSONObject object = response[0].getJSON();

            switch (object.getString("result")) {
                case "2":
                    if (!route.getCompiledRoute().contains("remotelog") && !route.getCompiledRoute().contains("bruteforce")) {
                        api.setStatus(vHackOSAPI.Status.FAILED_TO_LOGIN);
                        throw new LoginException("Invalid username or password");
                    }
                    break;
                case "36":
                    if (first) {
                        first = false;
                        throw new Exception("Invalid token");
                    } else {
                        throw new LoginException("Invalid token");
                    }
                case "10":
                    throw new RuntimeException("The server returned error 10, this might mean the bot is outdated or a bug ocurred.");
                case "1":
                    throw new RuntimeException("Your vhack account has been banned");
            }
        } catch (RuntimeException | LoginException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (final Exception e) {
            throw new IllegalStateException("An error occurred while processing rest request", e);
        } finally {
            r.close();
        }
        r.close();
        success++;
        if (success >= 2) {
            triesLeft = 3;
        }
        if (api.isDebugResponses()) LOG.info(response[0].getJSON().toString());
        return response[0];
    }
}
