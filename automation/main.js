import { copyFileSync, readFile, rmSync } from 'fs'
import { exit } from 'process'
import { Parser } from 'xml2js'
import { loopWhile } from 'deasync'
import { basename } from 'path'

import { downloadSync, fileExists, mkdirRecursive } from './utils.js'
import { JavaExec } from './javaexec.js'

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

let mavenProp = getMavenProp()

mkdirRecursive(constants.cacheDir)
let spigotFile = downloadIfNotExists(constants.spigotUrl, mavenProp.spigotVersion)
let vaultFile = downloadIfNotExists(constants.vaultUrl, mavenProp.vaultVersion)
let groupmanagerFile = downloadIfNotExists(constants.groupmanagerUrl)
let essentialsFile = downloadIfNotExists(constants.essentialsUrl)
let essentialsChatFile = downloadIfNotExists(constants.essentialschatUrl)
let essentialsSpawnFile = downloadIfNotExists(constants.essentialsspawnUrl)

rmSync(constants.serverDir, { recursive: true, force: true })
mkdirRecursive(constants.pluginsDir)
copyFileSync(`${constants.cacheDir}/${spigotFile}`, `${constants.workDir}/${spigotFile}`)
copyFileSync(`${constants.cacheDir}/${vaultFile}`, `${constants.pluginsDir}/${vaultFile}`)
copyFileSync(`${constants.cacheDir}/${groupmanagerFile}`, `${constants.pluginsDir}/${groupmanagerFile}`)
copyFileSync(`${constants.cacheDir}/${essentialsFile}`, `${constants.pluginsDir}/${essentialsFile}`)
copyFileSync(`${constants.cacheDir}/${essentialsChatFile}`, `${constants.pluginsDir}/${essentialsChatFile}`)
copyFileSync(`${constants.cacheDir}/${essentialsSpawnFile}`, `${constants.pluginsDir}/${essentialsSpawnFile}`)

const javaExec = new JavaExec(spigotFile)
const procEmitter = javaExec.spawnServer()

procEmitter.on('data', (data => {

}))

procEmitter.on('error', (data => {
    
}))

procEmitter.on('ext', (data => {
    
}))