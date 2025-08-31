package com.backtobedrock.augmentedhardcore.runnables;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.logging.Level;

public class UpdateChecker extends BukkitRunnable {

    private final AugmentedHardcore plugin;
    private boolean outdated = false;
    private String newestVersion;

    public UpdateChecker(AugmentedHardcore plugin) {
        this.plugin = plugin;
    }

    public void start() {
        this.runTaskTimerAsynchronously(this.plugin, 0L, 12000L);
    }

    public void stop() {
        this.cancel();
    }

    public boolean isOutdated() {
        return outdated;
    }

    public String getNewestVersion() {
        return newestVersion;
    }

    public void check() {
        try {
            URLConnection connection = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + 71483).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            try (InputStream inputStream = connection.getInputStream();
                 Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8)) {
                if (scanner.hasNext()) {
                    this.newestVersion = scanner.next();
                    this.outdated = !this.plugin.getDescription().getVersion().equalsIgnoreCase(this.newestVersion);
                }
            }
        } catch (IOException exception) {
            this.plugin.getLogger().log(Level.WARNING, "Cannot look for updates: {0}", exception.getMessage());
        }
    }

    @Override
    public void run() {
        this.check();
    }
}
