import { copyFileSync, readFile, rmSync } from 'fs'
import { exit } from 'process'
import { Parser } from 'xml2js'
import { loopWhile } from 'deasync'
import { basename } from 'path'
import { arch, platform } from 'os'
import { copySync } from 'fs-extra'

import { downloadSync, fileExists, mkdirRecursive, unTarGzSync } from './utils.js'
import { ExecList } from './execlist.js'
import { AutoTest } from './autotests.js'

import constants from './constants.js'

function getMavenProp() {
    let isDone = false
    let parser = new Parser()
    let jarFile
    let spigotVersion
    let vaultVersion

    readFile(constants.pomFile, (err, data) => {
        parser.parseString(data, (err2, result) => {
            jarFile = `${constants.mavenTargetDir}/${result.project.artifactId}-${result.project.version}.jar`
            spigotVersion = result.project.properties[0]["spigot.version"]
            vaultVersion = result.project.properties[0]["vault.version"]
            if (!fileExists(jarFile)) {
                console.error('Build Secuboid with Maven before running automation (mvn package)!')
                exit(1)
            }
        })
        isDone = true
    })
    loopWhile(() => !isDone)

    return {
        jarFile: jarFile,
        spigotVersion: spigotVersion,
        vaultVersion: vaultVersion
    }
}

function downloadIfNotExists(url, version = "") {
    let urlSource = url.replace(/\{\{VERSION\}\}/g, version)
    let fileBaseName = basename(urlSource);
    let pathFile = `${constants.cacheDir}/${fileBaseName}`
    if (!fileExists(pathFile)) {
        downloadSync(urlSource, pathFile)
    }

    return fileBaseName;
}

if (platform != 'linux' && arch != 'x64') {
    console.error('Sorry! Only Linux x64 is supported for Automation!')
    exit(1)
}

const mavenProp = getMavenProp()

mkdirRecursive(constants.cacheDir)
const jdkFile = downloadIfNotExists(constants.jreUrl)
const mariadbFile = downloadIfNotExists(constants.mariadbUrl)
const spigotFile = downloadIfNotExists(constants.spigotUrl, mavenProp.spigotVersion)
const vaultFile = downloadIfNotExists(constants.vaultUrl, mavenProp.vaultVersion)
const groupmanagerFile = downloadIfNotExists(constants.groupmanagerUrl)
const essentialsFile = downloadIfNotExists(constants.essentialsUrl)
const essentialsChatFile = downloadIfNotExists(constants.essentialschatUrl)
const essentialsSpawnFile = downloadIfNotExists(constants.essentialsspawnUrl)

for (let i = 0; i <= 1; i++) {
    const isMaria = (i == 1)

    rmSync(constants.workDir, { recursive: true, force: true })
    mkdirRecursive(constants.workDir)
    copySync('workresources', constants.workDir)
    if (isMaria) {
        copySync('workresources-mariadb', constants.workDir)
    }
    copyFileSync(mavenProp.jarFile, `${constants.pluginsDir}/${basename(mavenProp.jarFile)}`)
    copyFileSync(`${constants.cacheDir}/${spigotFile}`, `${constants.workDir}/${spigotFile}`)
    copyFileSync(`${constants.cacheDir}/${vaultFile}`, `${constants.pluginsDir}/${vaultFile}`)
    copyFileSync(`${constants.cacheDir}/${groupmanagerFile}`, `${constants.pluginsDir}/${groupmanagerFile}`)
    copyFileSync(`${constants.cacheDir}/${essentialsFile}`, `${constants.pluginsDir}/${essentialsFile}`)
    copyFileSync(`${constants.cacheDir}/${essentialsChatFile}`, `${constants.pluginsDir}/${essentialsChatFile}`)
    copyFileSync(`${constants.cacheDir}/${essentialsSpawnFile}`, `${constants.pluginsDir}/${essentialsSpawnFile}`)

    const jreRelativeDir = 'jre'
    const jreDir = constants.workDir + '/' + jreRelativeDir
    const mariadbDir = constants.workDir + '/mariadb'
    mkdirRecursive(jreDir)
    mkdirRecursive(mariadbDir)
    unTarGzSync(`${constants.cacheDir}/${jdkFile}`, { C: jreDir, strip: 1 })
    unTarGzSync(`${constants.cacheDir}/${mariadbFile}`, { C: mariadbDir, strip: 1 })

    const execList = new ExecList()
    execList.catchSig()

    try {
        const autoTest = new AutoTest(execList, mariadbDir, jreRelativeDir, spigotFile)
        console.log('=== Test with maria ' + isMaria + ' ===')
        autoTest.run(isMaria)
    } catch (err) {
        console.error(err)
        execList.killAll()
        exit(1)
    }
}