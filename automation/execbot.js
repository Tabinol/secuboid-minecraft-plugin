import { Exec } from './exec.js'

const DEFAULT_TIMEOUT = 300000 // 5 minutes

export class ExecBot extends Exec {

    constructor(javaExec, playerName) {
        super(javaExec.execList, playerName)
        this.javaExec = javaExec
        this.playerName = playerName
    }

    start() {
        this.spawnServer('node', ['./mineflayerbot.js', this.playerName])
    }

    startAndWaitSpawn() {
        this.start()
        this.waitFor('"event":"spawn"')
        this.waitForChunksToLoad()
    }

    chat(message, waitForMessage = null) {
        this.send({ command: 'chat', args: { message: message } })
        if (waitForMessage != null) {
            this.waitFor(waitForMessage)
        }
    }

    tp(x, y, z) {
        this.javaExec.send(`minecraft:tp ${this.playerName} ${x} ${y} ${z}`)
        this.javaExec.waitFor(`Teleported ${this.playerName}`)
        this.waitForChunksToLoad()
    }

    waitForChunksToLoad() {
        this.send({ command: 'waitForChunksToLoad' })
        this.waitFor('"event":"waitForChunksToLoadDone"')
    }

    moveAndWaitReached(x, z) {
        this.send({ command: 'move', args: { x: x, z: z } })
        this.waitFor('"event":"goal_reached"')
    }

    lookAtAndWaitDone(x, y, z, force = false) {
        this.send({ command: 'lookAt', args: { x: x, y: y, z: z, force: force } })
        this.waitFor('"event":"lookAtDone"')
    }

    creativeSetInventorySlotAndWaitDone(itemName, count, slot) {
        this.send({ command: 'creativeSetInventorySlot', args: { itemName: itemName, count: count, slot: slot } })
        this.waitFor('"event":"creativeSetInventorySlotDone"')
    }

    placeBlockAndWaitDone(x, y, z, vx, vy, vz) {
        this.lookAtAndWaitDone(x, y, z)
        this.send({ command: 'placeBlock', args: { vx: vx, vy: vy, vz: vz } })
        this.waitFor('"event":"placeBlockDone"')
    }

    activateBlockAndWaitDone(x, y, z) {
        this.lookAtAndWaitDone(x, y, z)
        this.send({ command: 'activateBlock', args: { x: x, y: y, z: z } })
        this.waitFor('"event":"activateBlockDone"')
    }

    quitAndKill() {
        this.send({ command: 'quit' })
        this.javaExec.waitFor('left the game')
        this.killAndWait()
    }
}