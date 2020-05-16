package me.tabinol.secuboid.storage;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.exceptions.SecuboidRuntimeException;
import me.tabinol.secuboid.inventories.PlayerInventoryCache;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.playerscache.PlayerCacheEntry;
import me.tabinol.secuboid.storage.StorageThread.SaveActionEnum;
import me.tabinol.secuboid.storage.StorageThread.SaveOn;
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

    public boolean isConversionNeeded() {
        final File dataFolder = secuboid.getDataFolder();
        return new File(dataFolder, NAME_LANDS).isDirectory() || new File(dataFolder, NAME_APPROVE_LIST).isFile()
                || new File(dataFolder, NAME_PLAYERS_CACHE).isFile()
                || new File(dataFolder, NAME_INVENTORIES).isDirectory();
    }

    public void landConversion() {
        final File landsFile = new File(secuboid.getDataFolder(), NAME_LANDS);
        if (!landsFile.isDirectory()) {
            return;
        }

        // load lands
        log.info("Starting flat to MySQL lands conversion...");

        // Load the lands without parent first because the async process to MySQL saves
        // the parent UUID before the parent is added.
        final LandsFlat landsFlat = new LandsFlat(secuboid);
        final Map<Land, UUID> orphanToParentUUID = landsFlat.loadLandsOrphanToParentUUID();
        waitforSave();

        landsFlat.findLandParents(orphanToParentUUID);
        waitforSave();

        moveToBackup(landsFile, NAME_LANDS);
        log.info("Lands conversion done.");
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
        waitforSave();

        moveToBackup(approvesFile, NAME_APPROVE_LIST);
        log.info("Approves conversion done.");
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
        waitforSave();

        moveToBackup(playersCacheFile, NAME_PLAYERS_CACHE);
        log.info("Players cache conversion done.");
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
        waitforSave();

        moveToBackup(inventoriesFile, NAME_INVENTORIES);
        log.info("Inventories conversion done.");
    }

    private void moveToBackup(final File fileToMove, final String fileName) {
        final File backupFile = new File(secuboid.getDataFolder(), NAME_BACKUP_FLAT);
        if (!backupFile.isDirectory()) {
            backupFile.mkdir();
        }
        fileToMove.renameTo(new File(backupFile, fileName));
    }

    private void waitforSave() {
        // Send the wake up signal at the end of the queue
        final StorageThread storageThread = secuboid.getStorageThread();
        storageThread.addSaveAction(SaveActionEnum.THREAD_NOTIFY, SaveOn.DATABASE, Optional.empty());

        // Wait!
        final Object lock = storageThread.getLock();
        synchronized (lock) {
            try {
                lock.wait();
            } catch (final InterruptedException e) {
                throw new SecuboidRuntimeException("Interruption on Secuboid conversion");
            }
        }
    }
}