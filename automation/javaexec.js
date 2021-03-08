import { Exec } from './exec.js'

import constants from './constants.js'

export class JavaExec extends Exec {

    constructor(jreDir, spigotFile) {
        super('server')
        this.jreDir = jreDir
        this.spigotFile = spigotFile
    }

    start() {
        let javaExec = this.jreDir + '/bin/java'

        let javaArgs
        if (process.env.JAVA_ARGS != null) {
            javaArgs = Array.from(process.env.JAVA_ARGS)
        } else {
            javaArgs = constants.javaArgsDefault
        }

        spawnServer(javaExec, javaArgs.concat(['-jar', this.spigotFile, '--nogui']), {
            cwd: constants.workDir,
            env: {
                JAVA_HOME: this.jreDir
            }
        })
    }
}