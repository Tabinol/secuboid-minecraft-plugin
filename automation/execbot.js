import { Exec } from './exec.js'

const DEFAULT_TIMEOUT = 300000 // 5 minutes

export class ExecBot extends Exec {

    constructor(execList, playerName) {
        super(execList, playerName)
        this.playerName = playerName
    }

    start() {
        this.spawnServer('node', ['./mineflayerbot.js', this.playerName])
    }
}