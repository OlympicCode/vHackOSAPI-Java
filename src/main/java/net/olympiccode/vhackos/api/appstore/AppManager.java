package net.olympiccode.vhackos.api.appstore;

import net.olympiccode.vhackos.api.entities.AppType;

import java.util.List;

public interface AppManager {
App getApp(AppType type);
List<App> getApps();
}
