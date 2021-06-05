import { Exec } from './exec.js'
import { createInterface } from 'readline'

import constants from './constants.js'

export class JavaExec extends Exec {

    constructor(execList, jreDir, spigotFile) {
        super(execList, 'server')
        this.jreDir = jreDir
        this.spigotFile = spigotFile
        this.breakMessages.push('at me.tabinol.secuboid.')
        this.rl = null
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

        this.rl = createInterface({ input: process.stdin, output: process.stdout })
        this.rl.on('line', (line) => {
            this.send(line)
        })
    }

    startAndWaitForDone() {
        this.start()
        this.waitFor('[Server thread/INFO]: Done')
    }

    stopAndWaitForDone() {
        if (this.rl != null) {
            this.rl.close()
            this.rl = null
        }
        this.send('stop')
        this.waitUntilExit()
    }
}