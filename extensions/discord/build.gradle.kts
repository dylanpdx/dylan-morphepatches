extension {
    name = "extensions/discord.mpe"
}

android {
    namespace = "app.morphe.extension.discord"
}

dependencies {
    //compileOnly(project(":extensions:shared:library"))
    //compileOnly(project(":extensions:discord:stub"))
    compileOnly(libs.annotation)
    compileOnly(libs.okhttp)
    compileOnly(libs.appcompat)
}
