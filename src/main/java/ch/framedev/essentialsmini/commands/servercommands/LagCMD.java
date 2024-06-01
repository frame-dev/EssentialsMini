package ch.framedev.essentialsmini.commands.servercommands;

import ch.framedev.essentialsmini.main.Main;
import ch.framedev.essentialsmini.abstracts.CommandBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.lang.management.ManagementFactory;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

/**
 * This Plugin was Created by FrameDev
 * Package : de.framedev.essentialsmin.commands
 * Date: 22.11.2020
 * Project: EssentialsMini
 * Copyrighted by FrameDev
 */
public class LagCMD extends CommandBase {

    private final Main plugin;

    /**
     * @param plugin
     */
    public LagCMD(Main plugin) {
        super(plugin);
        this.plugin = plugin;
        setup("lag", this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission(plugin.getPermissionName() + "lag")) {
            final double tps = plugin.getSpigotTimer().getAverageTPS();
            final ChatColor color;
            if (tps >= 18.0) {
                color = ChatColor.GREEN;
            } else if (tps >= 15.0) {
                color = ChatColor.YELLOW;
            } else {
                color = ChatColor.RED;
            }

            sender.sendMessage("§auptime §6" + formatDateDiff(ManagementFactory.getRuntimeMXBean().getStartTime()));
            sender.sendMessage("§atps " + "" + color + tps);
            sender.sendMessage("§agcmax §6" + Runtime.getRuntime().maxMemory() / 1024 / 1024);
            sender.sendMessage("§agctotal §6" + Runtime.getRuntime().totalMemory() / 1024 / 1024);
            sender.sendMessage("§agcfree §6" + Runtime.getRuntime().freeMemory() / 1024 / 1024);

            final List<World> worlds = plugin.getServer().getWorlds();
            for (final World w : worlds) {
                String worldType = "World";
                switch (w.getEnvironment()) {
                    case NETHER:
                        worldType = "Nether";
                        break;
                    case THE_END:
                        worldType = "The End";
                        break;
                    default:
                        break;
                }

                int tileEntities = 0;

                try {
                    for (final Chunk chunk : w.getLoadedChunks()) {
                        tileEntities += chunk.getTileEntities().length;
                    }
                } catch (final java.lang.ClassCastException ex) {
                    Bukkit.getLogger().log(Level.SEVERE, "Corrupted chunk data on world " + w, ex);
                }

                sender.sendMessage("§aWorldType = §6" + worldType + ", §aWorld Name = §6" + w.getName() + ", §aChunks Loaded = §6" + w.getLoadedChunks().length + ", §aEnities Loaded = §6" + tileEntities);
            }
        } else {
            sender.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
        }
        return super.onCommand(sender, command, label, args);
    }

    public static String formatDateDiff(final Calendar fromDate, final Calendar toDate) {
        boolean future = false;
        if (toDate.equals(fromDate)) {
            return "now";
        }
        if (toDate.after(fromDate)) {
            future = true;
        }
        // Temporary 50ms time buffer added to avoid display truncation due to code execution delays
        toDate.add(Calendar.MILLISECOND, future ? 50 : -50);
        final StringBuilder sb = new StringBuilder();
        final int[] types = new int[]{Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND};
        final String[] names = new String[]{"year", "years", "month", "months", "day", "days", "hour", "hours", "minute", "minutes", "second", "seconds"};
        int accuracy = 0;
        for (int i = 0; i < types.length; i++) {
            if (accuracy > 2) {
                break;
            }
            final int diff = dateDiff(types[i], fromDate, toDate, future);
            if (diff > 0) {
                accuracy++;
                sb.append(" ").append(diff).append(" ").append(names[i * 2 + (diff > 1 ? 1 : 0)]);
            }
        }
        // Preserve correctness in the original date object by removing the extra buffer time
        toDate.add(Calendar.MILLISECOND, future ? -50 : 50);
        if (sb.length() == 0) {
            return "now";
        }
        return sb.toString().trim();
    }

    @SuppressWarnings("unused")
    private static final Pattern timePattern = Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*(?:s[a-z]*)?)?", Pattern.CASE_INSENSITIVE);
    private static final int maxYears = 100000;

    static int dateDiff(final int type, final Calendar fromDate, final Calendar toDate, final boolean future) {
        final int year = Calendar.YEAR;

        final int fromYear = fromDate.get(year);
        final int toYear = toDate.get(year);
        if (Math.abs(fromYear - toYear) > maxYears) {
            toDate.set(year, fromYear +
                    (future ? maxYears : -maxYears));
        }

        int diff = 0;
        long savedDate = fromDate.getTimeInMillis();
        while ((future && !fromDate.after(toDate)) || (!future && !fromDate.before(toDate))) {
            savedDate = fromDate.getTimeInMillis();
            fromDate.add(type, future ? 1 : -1);
            diff++;
        }
        diff--;
        fromDate.setTimeInMillis(savedDate);
        return diff;
    }

    public static String formatDateDiff(final long date) {
        final Calendar c = new GregorianCalendar();
        c.setTimeInMillis(date);
        final Calendar now = new GregorianCalendar();
        return formatDateDiff(now, c);
    }


    public static class SpigotTimer implements Runnable {

        public SpigotTimer() {
            history.add(20d);
        }

        private final LinkedList<Double> history = new LinkedList<>();
        @SuppressWarnings("unused")
        private final long maxTime = 10 * 1000000;
        private final long tickInterval = 50;
        private final transient long lastPoll = System.nanoTime();

        @SuppressWarnings("unused")
        @Override
        public void run() {
            final long startTime = System.nanoTime();
            final long currentTime = System.currentTimeMillis();
            long timeSpent = (startTime - lastPoll) / 1000;
            if (timeSpent == 0) {
                timeSpent = 1;
            }
            if (history.size() > 10) {
                history.remove();
            }
            final double tps = tickInterval * 1000000.0 / timeSpent;
            if (tps <= 21) {
                history.add(tps);
            }
        }

        public double getAverageTPS() {
            double avg = 0;
            for (final Double f : history) {
                if (f != null) {
                    avg += f;
                }
            }
            return avg / history.size();
        }
    }

}

