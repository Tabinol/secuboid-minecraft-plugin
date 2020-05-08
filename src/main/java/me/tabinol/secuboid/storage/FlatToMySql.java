package me.tabinol.secuboid.storage;

import java.io.File;
import java.util.logging.Logger;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.inventories.PlayerInventoryCache;
import me.tabinol.secuboid.playerscache.PlayerCacheEntry;
import me.tabinol.secuboid.storage.flat.ApprovesFlat;
import me.tabinol.secuboid.storage.flat.InventoriesFlat;
import me.tabinol.secuboid.storage.flat.LandsFlat;
import me.tabinol.secuboid.storage.flat.PlayersCacheFlat;

public final class FlatToMySql {

    private static final String NAME_BACKUP_FLAT = "backupflat";
    private static final String NAME_LANDS = "lands";
    private static final String NAME_APPROVE_LIST = "approvelist.yml";
    private static final String NAME_PLAYERS_CACHE = "playerscache.conf";
    private final static String NAME_INVENTORIES = "inventories";

    private final Secuboid secuboid;

    private final Logger log;

    public FlatToMySql(final Secuboid secuboid) {
        this.secuboid = secuboid;

        log = secuboid.getLogger();
    }

    public void landConversion() {
        final File landsFile = new File(secuboid.getDataFolder(), NAME_LANDS);
        if (!landsFile.isDirectory()) {
            return;
        }

        // load lands
        log.info("Starting flat to MySQL lands conversion...");

        final LandsFlat landsFlat = new LandsFlat(secuboid);
        landsFlat.loadLands();

        moveToBackup(landsFile, NAME_LANDS);
        log.info("conversion done...");
    }

    public void approveConversion() {
        final File approvesFile = new File(secuboid.getDataFolder(), NAME_APPROVE_LIST);
        if (!approvesFile.exists()) {
            return;
        }

        // load approves
        log.info("Starting flat to MySQL approves conversion...");

        final ApprovesFlat approvesFlat = new ApprovesFlat(secuboid);
        approvesFlat.loadApproves();

        moveToBackup(approvesFile, NAME_APPROVE_LIST);
        log.info("conversion done...");
    }

    public void playersCacheConversion() {
        final File playersCacheFile = new File(secuboid.getDataFolder(), NAME_PLAYERS_CACHE);
        if (!playersCacheFile.exists()) {
            return;
        }

        // load approves
        log.info("Starting flat to MySQL players cache conversion...");

        final PlayersCacheFlat playersCacheFlat = new PlayersCacheFlat(secuboid);
        playersCacheFlat.loadPlayersCache();

        moveToBackup(playersCacheFile, NAME_PLAYERS_CACHE);
        log.info("conversion done...");
    }

    public void inventoriesConversion() {
        final File inventoriesFile = new File(secuboid.getDataFolder(), NAME_INVENTORIES);
        if (!inventoriesFile.isDirectory()) {
            return;
        }

        // load approves
        log.info("Starting flat to MySQL inventories conversion...");

        final InventoriesFlat inventoriesFlat = new InventoriesFlat(secuboid);
        inventoriesFlat.loadInventories();

        // convert all players
        for (final PlayerCacheEntry playerCacheEntry : secuboid.getPlayersCache().getPlayerCacheEntries()) {
            final PlayerInventoryCache playerInventoryCache = new PlayerInventoryCache(playerCacheEntry.getUUID(),
                    playerCacheEntry.getName());
            inventoriesFlat.loadInventoriesPlayer(playerInventoryCache);
        }

        moveToBackup(inventoriesFile, NAME_INVENTORIES);
        log.info("conversion done...");
    }

    private void moveToBackup(final File fileToMove, final String fileName) {
        final File backupFile = new File(secuboid.getDataFolder(), NAME_BACKUP_FLAT);
        if (!backupFile.isDirectory()) {
            backupFile.mkdir();
        }
        fileToMove.renameTo(new File(backupFile, fileName));
    }
}