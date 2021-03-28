import { MariadbExec } from './mariadbexec.js'
import { JavaExec } from './javaexec.js'
import { ExecBot } from './execbot.js'

export class AutoTest {

    constructor(execList, mariadbDir, jreRelativeDir, spigotFile) {
        this.execList = execList
        this.mariadbDir = mariadbDir
        this.jreRelativeDir = jreRelativeDir
        this.spigotFile = spigotFile
    }

    run(isDatabase) {
        const mariadbExec = new MariadbExec(this.execList, this.mariadbDir)

        // Start
        if (isDatabase) {
            mariadbExec.init()
            mariadbExec.start()
            mariadbExec.createDatabase()
        }
        const javaExec = new JavaExec(this.execList, this.jreRelativeDir, this.spigotFile)
        javaExec.startAndWaitForDone()

        // Player (op)
        const player01 = new ExecBot(javaExec, 'player01')
        player01.startAndWaitSpawn()
        javaExec.send('op player01')
        javaExec.waitFor('Made player01 a server operator')
        player01.quitAndKill()

        // Create land
        player01.startAndWaitSpawn()
        player01.send({ command: 'chat', args: { message: '/tppos 0 1 0' } })
        player01.waitFor('"event":"forcedMove"')
        player01.send({ command: 'chat', args: { message: '/sd select' } })
        player01.waitFor('You are now in Select Mode.')
        player01.send({ command: 'chat', args: { message: '/tppos 10 1 10' } })
        player01.waitFor('"event":"forcedMove"')
        player01.send({ command: 'chat', args: { message: '/sd create land01' } })
        player01.waitFor('You have created your land.')
        player01.send({ command: 'chat', args: { message: '/tppos 5 1 5' } })
        player01.waitFor('"event":"forcedMove"')
        player01.send({ command: 'chat', args: { message: '/sd info' } })
        player01.waitFor('land01')
        player01.quitAndKill()

        // Restart
        javaExec.stopAndWaitForDone()
        javaExec.startAndWaitForDone()

        // Check land saved
        player01.startAndWaitSpawn()
        player01.send({ command: 'chat', args: { message: '/sd info' } })
        player01.waitFor('land01')
        player01.quitAndKill()

        // Down
        javaExec.stopAndWaitForDone()
        if (isDatabase) {
            mariadbExec.killAndWait()
        }
    }
}