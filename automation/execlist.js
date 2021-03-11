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
        for (let i = 0; i < this.execs.length; i++) {
            this.execs[i].proc.kill()
            this.execs[i].waitUntilExit()
        }
    }

    add(exec) {
        this.execs.push(exec)
    }

    remove(exec) {
        this.execs.splice(this.execs.indexOf(exec), 1)
    }
}