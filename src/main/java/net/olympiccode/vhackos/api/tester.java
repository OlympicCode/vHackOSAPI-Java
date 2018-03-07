package net.olympiccode.vhackos.api;

import net.olympiccode.vhackos.api.entities.AppType;
import net.olympiccode.vhackos.api.entities.impl.vHackOSAPIImpl;
import net.olympiccode.vhackos.api.events.Event;
import net.olympiccode.vhackos.api.events.EventListener;
import net.olympiccode.vhackos.api.events.StatsUpdateEvent;
import net.olympiccode.vhackos.api.events.UpdateTaskFinishEvent;
import okhttp3.OkHttpClient;

import javax.security.auth.login.LoginException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class tester implements EventListener {

    public static void main(String[] args) {
        try {
            //Proxy proxyTest = new Proxy(Proxy.Type.HTTP,new InetSocketAddress("127.0.0.1", 8888));
           // OkHttpClient.Builder builder = new OkHttpClient.Builder().proxy(proxyTest);
            vHackOSAPI api = new vHackOSAPIBuilder()/*.setHttpClientBuilder(builder)*/.setUsername("OlympicCodeB").setPassword("12345678").buildAsync();
            api.addEventListener(new tester());
            Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> System.out.println(api.getTaskManager().getActiveTasks().stream().map(task -> task.getType() + ": " + task.getLevel() + " (" + task.getId() + ") - " + ((task.getEndTimestamp() - System.currentTimeMillis()) / 1000) ).collect(Collectors.joining("\n"))), 0, 5000, TimeUnit.MILLISECONDS);
            api.getAppManager().getApp(AppType.Spam).getAsUpdateable().fillTasks();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof StatsUpdateEvent) {
            StatsUpdateEvent e = (StatsUpdateEvent) event;
            System.out.println(e.getStats().getMoney());
            System.out.println(e.getStats().getLevel() + " - " + e.getStats().getLevelPorcentage());
        } else if (event instanceof UpdateTaskFinishEvent) {
            UpdateTaskFinishEvent e = (UpdateTaskFinishEvent) event;
            System.out.println("Finished " + e.getTask().getType() + " to level " + e.getTask().getLevel());
        }
    }
}
