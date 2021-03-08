import { spawn } from 'child_process'
import { loopWhile } from 'deasync'
import { exit } from 'process'
import { setTimeout } from 'timers'

const DEFAULT_TIMEOUT = 300000 // 5 minutes

export class Exec {

    constructor(serverName) {
        this.serverName = serverName
        this.isRunning = false
        this.prod = null
        this.expected = null
    }

    spawnServer(command, args, options = {}) {
        this.proc = spawn(command, args, options)
        this.isRunning = true

        this.proc.stdout.on('data', (data) => {
            const output = removeLineBreaks(data)
            console.log(this.serverName + '> ' + output)
            if ((this.expected instanceof RegExp && output.match(this.expected)) || output.includes(expected)) {
                this.expected = null
            }
        })

        this.proc.stderr.on('data', (data) => {
            const output = removeLineBreaks(data)
            console.error(this.serverName + '.err> ' + output)
        })

        this.proc.on('exit', (code) => {
            this.isRunning = false
            if (code === 0) {
                console.error(this.serverName + ' exit code=' + code)
            } else {
                console.log(this.serverName + ' stopped')
            }
        })
    }

    waitFor(expected, timeoutTime = DEFAULT_TIMEOUT) {
        console.log(this.serverName + ' wait for: ' + expected)
        this.expected = expected
        const timeout = doTimeout(timeoutTime)
        loopWhile(() => this.expected == null)
        timeout.clearTimeout()
    }

    send(command) {
        console.log(this.serverName + '< ' + command)
        this.proc.stdin.write(command + '\r')
    }

    waitUntilExit(timeoutTime = DEFAULT_TIMEOUT) {
        console.log(this.serverName + ' waiting for exit...')
        const timeout = doTimeout(timeoutTime)
        loopWhile(() => !isDone)
        timeout.clearTimeout()
    }

    doTimeout(timeoutTime) {
        return setTimeout(() => {
            console.error(this.serverName + " waiting timeout!")
            exit(1)
        })
    }
}
