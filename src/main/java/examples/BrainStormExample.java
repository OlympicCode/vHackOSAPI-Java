package examples;

import net.olympiccode.vhackos.api.appstore.App;
import net.olympiccode.vhackos.api.entities.AppType;
import net.olympiccode.vhackos.api.entities.BruteForceState;
import net.olympiccode.vhackos.api.entities.impl.TaskManagerImpl;
import net.olympiccode.vhackos.api.events.Event;
import net.olympiccode.vhackos.api.events.EventListener;
import net.olympiccode.vhackos.api.events.StatsUpdateEvent;
import net.olympiccode.vhackos.api.events.UpdateTaskFinishEvent;
import net.olympiccode.vhackos.api.exceptions.ExploitFailedException;
import net.olympiccode.vhackos.api.network.ExploitedTarget;
import net.olympiccode.vhackos.api.vHackOSAPI;
import net.olympiccode.vhackos.api.vHackOSAPIBuilder;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BrainStormExample implements EventListener {
    /*
    ATTENTION

    THIS IS THE CLASS I USE TO TEST THE API, I DECIDED TO KEEP IT HERE AS IT HAS MOST API FUNCTIONS AND CAN BE GOOD FOR EXAMPLES
     */
    public static void main(String[] args) {
        try {
            String l = null;
            try {
               l = Files.readAllLines(Paths.get("example.txt"), Charset.forName("UTF-8")).stream().collect(Collectors.joining());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (l == null || !l.contains("#")) {
                return;
            }
            String username = l.split("#")[0];
            String pass = l.split("#")[1];
            System.out.println(username + pass);
            // Proxy proxyTest = new Proxy(Proxy.Type.HTTP,new InetSocketAddress("127.0.0.1", 8888));
            //OkHttpClient.Builder builder = new OkHttpClient.Builder().proxy(proxyTest);
            vHackOSAPI api = new vHackOSAPIBuilder()/*.setHttpClientBuilder(builder)*/.setUsername(username).setPassword(pass).buildBlocking();
            api.addEventListener(new BrainStormExample());
           // Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> System.out.println(api.getTaskManager().getActiveTasks().stream().map(task -> task.getType() + ": " + task.getLevel() + " (" + task.getId() + ") - " + ((task.getEndTimestamp() - System.currentTimeMillis()) / 1000) ).collect(Collectors.joining("\n"))), 0, 5000, TimeUnit.MILLISECONDS);
          //  api.getAppManager().getApp(AppType.Spam).getAsUpdateable().fillTasks();
            int lvl = 10000000;
            App capp = null;
            System.out.println(api.getStats().getUsername());
            System.out.println(api.getAppManager().getApps());
            for (App app : api.getAppManager().getApps()) {
                if (!app.isInstalled() && app.getRequiredLevel() <= api.getStats().getLevel()) app.getAsInstallable().install();
                if (app.getLevel() != 0 && !app.isOneTime() && app.isInstalled() && app.getLevel() < lvl) {
                    lvl = app.getLevel();
                    capp = app;
                }
            }
            if (capp.getPrice() < api.getStats().getMoney()) {
                System.out.println("Starting " + capp.getType().getName());
                if (!capp.getAsUpdateable().fillTasks()) ((TaskManagerImpl) api.getTaskManager()).reloadTasks();
                if (api.getTaskManager().getBoosters() > 0) {
                    System.out.println("Boosted tasks");
                    api.getTaskManager().boostAll();
                }
                if (api.getStats().getNetcoins() > 1000) {
                    System.out.println("Finished tasks with netcoins");
                    api.getTaskManager().finishAll();
                }
            }
            Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
                System.out.println("Active tasks: " + api.getTaskManager().getActiveTasks().stream().map(task -> task.getId() + " " + task.getType().getName()).collect(Collectors.joining(", ")));
                    }, 0, 10000, TimeUnit.MILLISECONDS);
            Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
                api.getMiner().start();
                    }, 0, 60000 * 60, TimeUnit.MILLISECONDS);
            Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
                final long[] money = {0};
                api.getTaskManager().getActiveBrutes().forEach(bruteForce -> {
                    if (bruteForce.getState() == BruteForceState.SUCCESS) {
                        ExploitedTarget etarget = bruteForce.exploit();
                        ExploitedTarget.Banking banking = etarget.getBanking();

                        if (banking.isBruteForced()) {
                            long av = banking.getAvaliableMoney();
                            if (av > 0 && banking.withdraw()) System.out.println("Got " + av + " of " + banking.getTotal() + " (" + banking.getSavings() + "/" + banking.getMaxSavings() + ") " + (banking.getSavings() / 2 < banking.getMaxSavings())); else System.out.println("fail" + (banking.getSavings() / 2 < banking.getMaxSavings()));
                            money[0] = money[0] + av;
                            if (!(banking.getSavings() / 2 < banking.getMaxSavings()) && banking.getSavings() < 1000000 && !bruteForce.getUsername().toLowerCase().contains("atjon")) bruteForce.remove();
                        } else {
                            banking.startBruteForce();
                        }
                        etarget.setSystemLog("Checkium was here #OlympicCode");
                    }
                });
                api.getStats().getExploits();
                if (api.getStats().getExploits() > 0) {
                    int level = api.getAppManager().getApp(AppType.SDK).getLevel();
                    api.getNetworkManager().getTargets().forEach(target -> {
                        if (target.getFirewall() < level && api.getStats().getExploits() > 0) {
                            try {
                                ExploitedTarget etarget = target.exploit();
                                etarget.getBanking().startBruteForce();
                                etarget.setSystemLog("Checkium was here #OlympicCode");
                                System.out.println("Started brute @ " + etarget.getIp());
                            } catch (ExploitFailedException e) {
                                System.out.println(target.getIp() + ": " + e.getMessage());
                            }
                        }
                    });
                }
                }, 100, 60000, TimeUnit.MILLISECONDS);
            System.out.println(api.getTaskManager().getActiveBrutes().stream().map(bruteForce -> bruteForce.getUsername() + " " + bruteForce.getState()).collect(Collectors.joining(", ")));
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof StatsUpdateEvent) {
            StatsUpdateEvent e = (StatsUpdateEvent) event;
            System.out.println(e.getStats().getExploits() + " " + e.getStats().getMoney() + " " + e.getStats().getNetcoins() + " (" + e.getAPI().getMiner().isRunning() + ")");
            System.out.println(e.getStats().getLevel() + " - " + e.getStats().getLevelPorcentage());
        } else if (event instanceof UpdateTaskFinishEvent) {
            UpdateTaskFinishEvent e = (UpdateTaskFinishEvent) event;
            System.out.println(e.getAPI().getTaskManager().getActiveTasks().size() + "Finished " + e.getTask().getType() + " to level " + e.getTask().getLevel());
            if (e.getAPI().getTaskManager().getActiveTasks().size() == 0) {
                int lvl = 100;
                App capp = null;
                for (App app : e.getAPI().getAppManager().getApps()) {
                    if (app.getLevel() != 0 && !app.isOneTime() && app.isInstalled() && app.getLevel() < lvl) {
                        lvl = app.getLevel();
                        capp = app;
                    }
                }
                System.out.println("Starting " + capp.getType().getName() + " (" + capp.getType().getId() + ")");
                boolean a = capp.getAsUpdateable().fillTasks();
                if (!a) {
                    a = capp.getAsUpdateable().fillTasks();
                }
                if (a) {
                    if (e.getAPI().getTaskManager().getBoosters() > 0) {
                        System.out.println("Boosted tasks");
                        e.getAPI().getTaskManager().boostAll();
                    }
                    if (e.getAPI().getStats().getNetcoins() > 1000) {
                        System.out.println("Finished tasks with netcoins");
                        e.getAPI().getTaskManager().finishAll();
                    }
                }
            }
        }
    }
}
