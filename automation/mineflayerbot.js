import { createBot } from 'mineflayer'
import mineflayerPathfinder from 'mineflayer-pathfinder'
import minecraftData from 'minecraft-data'
import { createInterface } from 'readline'
import vec3Module from 'vec3'
import prismarineItem from 'prismarine-item'

const { pathfinder, Movements, goals: { GoalXZ } } = mineflayerPathfinder
const { Vec3 } = vec3Module
const Item = prismarineItem("1.16")

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

bot.on('message', (jsonMsg, position) => {
    sendToConsole('message', {
        jsonMsg: jsonMsg,
        position: position
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
        case 'creativeSetInventorySlot':
            const item = new Item(mcData.itemsByName[args.itemName].id, args.count)
            bot.creative.setInventorySlot(args.slot, item, () => {
                sendToConsole('creativeSetInventorySlotDone')
            })
            break
        case 'lookAt':
            bot.lookAt(new Vec3(args.x, args.y, args.z), args.force, () => {
                sendToConsole('lookAtDone')
            })
            break
        case 'move':
            bot.pathfinder.setMovements(defaultMove)
            bot.pathfinder.setGoal(new GoalXZ(args.x, args.z))
            break
        case 'placeBlock':
            const referenceBlock = bot.blockAtCursor()
            bot.placeBlock(referenceBlock, new Vec3(args.vx, args.vy, args.vz), () => {
                sendToConsole('placeBlockDone')
            })
            break

        case 'activateBlock':
            const block = bot.blockAtCursor()
            bot.activateBlock(block, () => {
                sendToConsole('activateBlockDone')
            })
            break
        case 'waitForChunksToLoad':
            bot.waitForChunksToLoad(() => {
                sendToConsole('waitForChunksToLoadDone')
            })
            break
        case 'quit':
            bot.quit()
            break
        default:
    }
})