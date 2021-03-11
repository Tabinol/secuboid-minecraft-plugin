/*
./scripts/mariadb-install-db --basedir=/home/michel/develop/secuboid-minecraft-plugin/automation/target/work/mariadb --auth-root-authentication-method=normal
./bin/mariadbd --basedir=/home/michel/develop/secuboid-minecraft-plugin/automation/target/work/mariadb
./bin/mariadb -u root


*/

import { Exec } from './exec.js'

import constants from './constants.js'

export class MariadbExec extends Exec {

    constructor(execList, mariadbDir) {
        super(execList, 'mariadbd')
        this.mariadbDir = mariadbDir
    }

    init() {
        const installDbProd = new Exec(this.execList, 'mariadb-install-db')
        installDbProd.spawnServer('./scripts/mariadb-install-db', [
            '--basedir=.',
            '--auth-root-authentication-method=normal'
        ], { cwd: this.mariadbDir })
        installDbProd.waitUntilExit()
    }

    start() {
        this.spawnServer('./bin/mariadbd', ['--basedir=.'], { cwd: this.mariadbDir })
        this.waitFor('ready for connections.')
    }

    createDatabase() {
        const mariadbClient = new Exec(this.execList, 'mariadb-client')
        mariadbClient.spawnServer('./bin/mariadb', [
            '-u',
            'root'
        ], { cwd: this.mariadbDir })
        mariadbClient.send("CREATE USER IF NOT EXISTS 'secuboid'@'localhost' IDENTIFIED '12345';")
        mariadbClient.send("DROP DATABASE IF NOT EXISTS secuboid;")
        mariadbClient.send("CREATE DATABASE secuboid;")
        mariadbClient.send("GRANT ALL ON secuboid.* TO 'secuboid'@'localhost';")
        mariadbClient.waitUntilExit()
    }
}