package net.olympiccode.vhackos.api.entities;

import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Collectors;


public enum AppType {
            Notepad("Notepad", 0),
            Antivirus ("Antivirus", 1),
            Firewall ("Firewall", 2),
            Spam ("Spam", 3),
            BruteForce ("Bruteforce", 4),
            BankingProtection ("Banking Protection", 5),
            SDK ("Software Development Kit", 6),
            Community ("Community", 7),
            Missions ("Missions", 8),
            Leaderboards ("Leaderboards", 9),
            IPSP ("IP-Spoofing", 10),
            NCMiner ("NCMiner", 11),
            Crew("Crew", 12),
            Server("Server", 13),
            MalwareKit ("Malware Kit", 14),
            Jobs ("Jobs", 15);
    @Getter
    private String name;
    @Getter
    private int id;

    AppType(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public static AppType byId(int id) {
        return Arrays.stream(AppType.values()).filter(appType -> appType.getId() == id).collect(Collectors.toList()).get(0);
    }
    
}
