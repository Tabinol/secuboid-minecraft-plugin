export class ExecList {

    constructor() {
        this.execs = []
    }

    catchSig() {
        process.once('SIGINT', (code) => {
            console.log('SIGINT received...')
            this.killAll()
        })
        process.once('SIGTERM', (code) => {
            console.log('SIGTERM received...')
            this.killAll()
        })
    }

    killAll() {
        while (this.execs.length > 0) {
            this.execs[0].proc.kill()
            this.execs[0].waitUntilExit()
        }
    }

    add(exec) {
        this.execs.push(exec)
    }

    remove(exec) {
        this.execs.splice(this.execs.indexOf(exec), 1)
    }
}