import { accessSync, constants, createWriteStream, mkdirSync } from 'fs'
import follow_redirects from 'follow-redirects'
import { loopWhile } from 'deasync'
import { exit } from 'process'

const { https } = follow_redirects;

export function fileExists(filename) {
    try {
        accessSync(filename, constants.F_OK)
    } catch (e) {
        return false
    }
    return true
}

export function mkdirRecursive(dirname) {
    mkdirSync(dirname, { recursive: true })
}

export function downloadSync(source, target) {
    let isDone = false
    let url = new URL(source)
    console.log(`download: ${source}`)

    let ws = createWriteStream(target, {})
    https.get(url, (res) => {
        console.log('statusCode:', res.statusCode)
        if (res.statusCode != 200) {
            console.error('The file is not found from the URL!')
            exit(1)
        }

        res.on('data', (data) => {
            ws.write(data)
        })
        res.on('end', () => {
            ws.end()
            isDone = true
        })
    }).on('error', (e) => {
        console.error(e)
        exit(1)
    })
    loopWhile(() => !isDone)
}
