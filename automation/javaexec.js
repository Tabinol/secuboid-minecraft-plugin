import { Exec } from './exec.js'

import constants from './constants.js'

export class JavaExec extends Exec {

    constructor(execList, jreDir, spigotFile) {
        super(execList, 'server')
        this.jreDir = jreDir
        this.spigotFile = spigotFile
        this.breakMessages.push('at me.tabinol.secuboid.Secuboid')
    }

    start() {
        let javaExec = this.jreDir + '/bin/java'

        let javaArgs
        if (process.env.JAVA_ARGS != null) {
            javaArgs = Array.from(process.env.JAVA_ARGS)
        } else {
            javaArgs = constants.javaArgsDefault
        }

        this.spawnServer(javaExec, javaArgs.concat(['-jar', this.spigotFile, '--nogui']), {
            cwd: constants.workDir,
            env: {
                JAVA_HOME: this.jreDir
            }
        })
    }

    startAndWaitForDone() {
        this.start()
        this.waitFor('[Server thread/INFO]: Done')
    }

    stopAndWaitForDone() {
        this.send('stop')
        this.waitUntilExit()
    }
}