import { spawn } from 'child_process'
import EventEmitter from 'events'
import { exit } from 'process'

import { fileExists } from './utils.js'

import constants from './constants.js'

export class JavaExec {

    constructor(spigotFile) {
        this.spigotFile = spigotFile
    }

    spawnServer() {
        let javaExec
        if (process.platform === 'win32') {
            javaExec = process.env.JAVA_HOME + '\\bin\\java.exe'
        } else {
            javaExec = process.env.JAVA_HOME + '/bin/java'
        }
        if (!fileExists(javaExec)) {
            console.log("JAVA_HOME net set or wrong!")
            exit(1)
        }

        let javaArgs
        if (process.env.JAVA_ARGS != null) {
            javaArgs = Array.from(process.env.JAVA_ARGS)
        } else {
            javaArgs = constants.javaArgsDefault
        }

        const procEmitter = new EventEmitter();
        const proc = spawn(javaExec, javaArgs.concat(['-jar', this.spigotFile]), { cwd: constants.workDir })

        proc.stdout.on('data', (data) => {
            console.log('server: ' + data)
            procEmitter.emit('data', data)
        })

        proc.stderr.on('data', (data) => {
            console.error('server: ' + data)
            procEmitter.emit('error', data)
        })

        proc.on('exit', (code) => {
            if (code === 0) {
                console.error('server exit code=' + code)
            } else {
                console.log('Server stopped')
            }
            procEmitter.emit('exit', code)
        })

        return procEmitter
    }
}