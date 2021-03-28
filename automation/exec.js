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
        this.breakMessages = []
        this.expected = null
        this.messageQueue = []
        this.error = false
        this.toKill = false
        this.isExit = false
    }

    spawnServer(command, args, options = {}) {
        this.expected = null
        this.messageQueue = []
        this.error = false
        this.toKill = false
        this.isExit = false

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
            this.isExit = true
            this.execList.remove(this)
            if (!this.toKill && code != null && code !== 0) {
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

                if (!this.isError) {
                    for (let j in this.breakMessages) {
                        if (output.includes(this.breakMessages[j])) {
                            this.error = true
                            setTimeout(() => {
                                console.error(this.serverName + ": An error is detected!")
                                this.execList.killAll()
                                exit(1)
                            }, 1000)
                        }
                    }
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
        console.log(this.serverName + ' waits for: ' + expected)
        this.expected = expected
        const timeout = this.doTimeout(timeoutTime)
        loopWhile(() => this.expected != null)
        clearTimeout(timeout)
    }

    send(command) {
        let output
        if (command instanceof Object) {
            output = JSON.stringify(command)
        } else {
            output = command
        }
        console.log(this.serverName + '< ' + output)
        this.messageQueue = []
        this.proc.stdin.write(output + '\n')
    }

    killAndWait(timeoutTime = DEFAULT_TIMEOUT) {
        if (this.toKill) {
            return
        }

        this.toKill = true
        this.proc.kill()
        this.waitUntilExit(timeoutTime)
    }

    waitUntilExit(timeoutTime = DEFAULT_TIMEOUT) {
        console.log(this.serverName + ' waiting for exit...')
        this.messageQueue = []
        const timeout = this.doTimeout(timeoutTime)
        loopWhile(() => !this.isExit)
        clearTimeout(timeout)
    }
}
