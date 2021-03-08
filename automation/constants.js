export default {
    pomFile: "../pom.xml",
    mavenTargetDir: "../target",
    cacheDir: "target/cache",
    workDir: "target/work",
    pluginsDir: "target/work/plugins",
    javaArgsDefault: [
        "-Xms2G",
        "-Xmx2G",
        "-XX:+UseConcMarkSweepGC",
        "-DIReallyKnowWhatIAmDoingISwear"
    ],
    jreUrl: "https://github.com/AdoptOpenJDK/openjdk8-binaries/releases/download/jdk8u282-b08/OpenJDK8U-jre_x64_linux_hotspot_8u282b08.tar.gz",
    mariadbUrl: "https://mirror.its.dal.ca/mariadb/mariadb-10.5.9/bintar-linux-x86_64/mariadb-10.5.9-linux-x86_64.tar.gz",
    spigotUrl: "https://cdn.getbukkit.org/spigot/spigot-{{VERSION}}.jar",
    vaultUrl: "https://jitpack.io/com/github/MilkBowl/Vault/{{VERSION}}/Vault-{{VERSION}}.jar",
    groupmanagerUrl: "https://github.com/ElgarL/GroupManager/releases/download/v2.9/GroupManager.jar",
    essentialsUrl: "https://github.com/EssentialsX/Essentials/releases/download/2.18.2/EssentialsX-2.18.2.0.jar",
    essentialschatUrl: "https://github.com/EssentialsX/Essentials/releases/download/2.18.2/EssentialsXChat-2.18.2.0.jar",
    essentialsspawnUrl: "https://github.com/EssentialsX/Essentials/releases/download/2.18.2/EssentialsXSpawn-2.18.2.0.jar"
}