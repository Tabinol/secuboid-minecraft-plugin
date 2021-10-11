export default {
    pomFile: "../pom.xml",
    mavenTargetDir: "../target",
    cacheDir: "target/cache",
    workDir: "target/work",
    pluginsDir: "target/work/plugins",
    javaArgsDefault: [
        "-Xms2G",
        "-Xmx2G",
        "-DIReallyKnowWhatIAmDoingISwear"
    ],
    jreUrl: "https://github.com/adoptium/temurin16-binaries/releases/download/jdk-16.0.2%2B7/OpenJDK16U-jdk_x64_linux_hotspot_16.0.2_7.tar.gz",
    mariadbUrl: "https://ftp.osuosl.org/pub/mariadb/mariadb-10.6.4/bintar-linux-systemd-x86_64/mariadb-10.6.4-linux-systemd-x86_64.tar.gz",
    spigotUrl: "https://download.getbukkit.org/spigot/spigot-{{VERSION}}.jar",
    vaultUrl: "https://jitpack.io/com/github/MilkBowl/Vault/{{VERSION}}/Vault-{{VERSION}}.jar",
    groupmanagerUrl: "https://github.com/ElgarL/GroupManager/releases/download/v2.9/GroupManager.jar",
    essentialsUrl: "https://github.com/EssentialsX/Essentials/releases/download/2.18.2/EssentialsX-2.18.2.0.jar",
    essentialschatUrl: "https://github.com/EssentialsX/Essentials/releases/download/2.18.2/EssentialsXChat-2.18.2.0.jar",
    essentialsspawnUrl: "https://github.com/EssentialsX/Essentials/releases/download/2.18.2/EssentialsXSpawn-2.18.2.0.jar"
}