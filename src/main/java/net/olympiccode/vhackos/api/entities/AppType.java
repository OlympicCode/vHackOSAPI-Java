package net.olympiccode.vhackos.api.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
            MalwareKit ("Malware Kit", 11),
            Jobs ("Jobs", 12);

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
