export default {
    pomFile: "../pom.xml",
    mavenTargetDir: "../target",
    cacheDir: "target/cache",
    workDir: "target/work",
    serverDir: "target/work/server",
    pluginsDir: "target/work/plugins",
    javaArgsDefault: [
        "-Xms2G",
        "-Xmx2G",
        "-XX:+UseConcMarkSweepGC",
    ],
    spigotUrl: "https://cdn.getbukkit.org/spigot/spigot-{{VERSION}}.jar",
    vaultUrl: "https://jitpack.io/com/github/MilkBowl/Vault/{{VERSION}}/Vault-{{VERSION}}.jar",
    groupmanagerUrl: "https://github.com/ElgarL/GroupManager/releases/download/v2.9/GroupManager.jar",
    essentialsUrl: "https://github.com/EssentialsX/Essentials/releases/download/2.18.2/EssentialsX-2.18.2.0.jar",
    essentialschatUrl: "https://github.com/EssentialsX/Essentials/releases/download/2.18.2/EssentialsXChat-2.18.2.0.jar",
    essentialsspawnUrl: "https://github.com/EssentialsX/Essentials/releases/download/2.18.2/EssentialsXSpawn-2.18.2.0.jar"
}