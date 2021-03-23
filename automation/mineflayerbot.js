import { createBot } from 'mineflayer'
import { createInterface } from 'readline'

const playerName = process.argv.slice(2)[0]
const bot = createBot({ username: playerName, hideErrors: false })
const rl = createInterface({ input: process.stdin, output: process.stdout })

function sendToConsole(event, params = {}) {
    console.log(JSON.stringify({ event: event, params: params }))
}

bot.on('spawn', () => {
    sendToConsole('spawn')
})

rl.on('line', (line) => {
    let action
    try {
        action = JSON.parse(line)
    } catch (e) {
        // Just skip, not a JSON
        return
    }

    const command = action.command
    const args = action.args
    switch (command) {
        case 'chat':
            bot.chat(args.message)
            break
        default:
    }
})