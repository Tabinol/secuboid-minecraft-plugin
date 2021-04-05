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
        player01.tp(0, 4, 0)
        player01.chat('/minecraft:setworldspawn', 'commands.setworldspawn.success')
        player01.quitAndKill()

        console.log('=>Create a land')
        player01.startAndWaitSpawn()
        player01.chat('/sd select', 'You are now in Select Mode.')
        player01.moveAndWaitReached(10, 10)
        player01.chat('/sd create land01', 'You have created your land.')
        player01.moveAndWaitReached(5, 5)
        player01.chat('/sd info', 'land01')
        player01.quitAndKill()

        // Restart
        javaExec.stopAndWaitForDone()
        javaExec.startAndWaitForDone()
        player01.startAndWaitSpawn()
        player01.chat('/gm 1', 'Set game mode')

        console.log('=>Check if the land is saved')
        player01.chat('/sd info', 'land01')

        console.log('=>test EAT_CAKE')
        player01.creativeSetInventorySlotAndWaitDone('cake', 1, 36)
        player01.placeBlockAndWaitDone(7.5, 3.5, 7.5, 0, 1, 0)
        player01.chat('/sd select land01', 'land01')
        player01.chat('/sd perm add everybody EAT_CAKE false', 'you have set the permission')
        player01.chat('/sd cancel', 'Your selection is cancelled.')
        player01.chat('/sd am', 'you are no longer in admin mode.')
        player01.activateBlockAndWaitDone(7.5, 4.5, 7.5)
        player01.waitFor('you don\'t have the permission')

        player01.quitAndKill()

        // Down
        javaExec.stopAndWaitForDone()
        if (isDatabase) {
            mariadbExec.killAndWait()
        }
    }
}