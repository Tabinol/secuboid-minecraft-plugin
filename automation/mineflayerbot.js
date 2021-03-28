import { createBot } from 'mineflayer'
import mineflayerPathfinder from 'mineflayer-pathfinder'
import minecraftData from 'minecraft-data'
import { createInterface } from 'readline'

const { pathfinder, Movements, goals: { GoalXZ } } = mineflayerPathfinder

const playerName = process.argv.slice(2)[0]
const bot = createBot({ username: playerName, hideErrors: false })
const rl = createInterface({ input: process.stdin, output: process.stdout })
bot.loadPlugin(pathfinder)

let mcData = null
let defaultMove = null

function sendToConsole(event, params = {}) {
    console.log(JSON.stringify({ event: event, params: params }))
}

bot.on('spawn', () => {
    mcData = minecraftData(bot.version)
    defaultMove = new Movements(bot, mcData)
    sendToConsole('spawn')
})

bot.on('chat', (username, message, translate, jsonMsg, matches) => {
    sendToConsole('spawn', {
        username: username,
        message: message,
        translate: translate,
        jsonMsg: jsonMsg,
        matches: matches
    })
})

bot.on('forcedMove', () => {
    sendToConsole('forcedMove')
})

bot.on('goal_reached', () => {
    sendToConsole('goal_reached')
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
        case 'move': // Bug!
            bot.pathfinder.setMovements(defaultMove)
            bot.pathfinder.setGoal(new GoalXZ(args.x, args.z))
            break
        case 'quit':
            bot.quit()
            break
        default:
    }
})