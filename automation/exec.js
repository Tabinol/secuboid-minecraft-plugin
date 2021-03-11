import { spawn } from 'child_process'
import { loopWhile } from 'deasync'
import { exit } from 'process'
import { clearTimeout, setTimeout } from 'timers'

const DEFAULT_TIMEOUT = 300000 // 5 minutes

export class Exec {

    constructor(execList, serverName) {
        this.execList = execList
        this.serverName = serverName
        this.proc = null
        this.expected = null
        this.messageQueue = []
    }

    spawnServer(command, args, options = {}) {
        console.log(`Starting ${this.serverName}...`)
        this.proc = spawn(command, args, options)
        this.execList.add(this)

        this.proc.stdout.on('data', (data) => {
            this.showLines(data, false)
        })

        this.proc.stderr.on('data', (data) => {
            this.showLines(data, true)
        })

        this.proc.on('exit', (code) => {
            this.execList.remove(this)
            if (code !== 0) {
                console.error(this.serverName + ' exit code=' + code)
                this.execList.killAll()
                exit(1)
            } else {
                console.log(this.serverName + ' stopped')
            }
        })
    }

    showLines(data, isError) {
        const lines = data.toString().split(/\r\n|\n|\r/)
        for (let i = 0; i < lines.length; i++) {
            const output = lines[i]
            if (i !== lines.length - 1 || output !== '') {
                if (isError) {
                    console.error(this.serverName + '.err> ' + output)
                } else {
                    console.log(this.serverName + '> ' + output)
                }
                this.messageQueue.push(output)
                let message
                while (this.expected != null && (message = this.messageQueue.shift()) !== undefined) {
                    if ((this.expected instanceof RegExp && message.match(this.expected)) || message.toString().includes(this.expected)) {
                        this.expected = null
                    }
                }
            }
        }
    }

    doTimeout(timeoutTime) {
        return setTimeout(() => {
            console.error(this.serverName + " waiting timeout!")
            this.execList.killAll()
            exit(1)
        }, timeoutTime)
    }

    waitFor(expected, timeoutTime = DEFAULT_TIMEOUT) {
        console.log(this.serverName + ' wait for: ' + expected)
        this.expected = expected
        const timeout = this.doTimeout(timeoutTime)
        loopWhile(() => this.expected != null)
        clearTimeout(timeout)
    }

    send(command) {
        console.log(this.serverName + '< ' + command)
        this.messageQueue = []
        this.proc.stdin.write(command + '\r')
    }

    waitUntilExit(timeoutTime = DEFAULT_TIMEOUT) {
        console.log(this.serverName + ' waiting for exit...')
        this.messageQueue = []
        const timeout = this.doTimeout(timeoutTime)
        loopWhile(() => this.proc.exitCode == null)
        clearTimeout(timeout)
    }
}
