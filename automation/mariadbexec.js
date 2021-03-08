/*
./scripts/mariadb-install-db --basedir=/home/michel/develop/secuboid-minecraft-plugin/automation/target/work/mariadb --auth-root-authentication-method=normal
./bin/mariadbd --basedir=/home/michel/develop/secuboid-minecraft-plugin/automation/target/work/mariadb
./bin/mariadb -u root


*/

import { Exec } from './exec.js'

import constants from './constants.js'

export class MariadbExec extends Exec {

    constructor(mariadbDir) {
        super('mariadb')
        this.mariadbDir = mariadbDir
    }

    init() {
        const installDbProd = new Exec('mariadb-install-db')
        installDbProd.spawnServer(this.mariadbDir + '/scripts/mariadb-install-db', [
            '--basedir=' + this.mariadbDir,
            '--auth-root-authentication-method=normal'
        ], { cwd: this.mariadbDir })
        installDbProd.waitUntilExit()
    }

    start() {

        const mariadbdExec = this.jreDir + '/bin/mariadbd'

        spawnServer(mariadbdExec, ['--basedir=' + this.mariadbDir], { cwd: this.mariadbDir })
    }
}