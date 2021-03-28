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
    }

    moveAndWaitReached(x, z) { // Bug!
        this.send({ command: 'move', args: { x: x, z: z } })
        this.waitFor('"event":"goal_reached"')
    }

    quitAndKill() {
        this.send({ command: 'quit' })
        this.javaExec.waitFor('left the game')
        this.killAndWait()
    }
}