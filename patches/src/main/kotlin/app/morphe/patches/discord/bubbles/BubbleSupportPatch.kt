package app.morphe.patches.discord.bubbles

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.resourcePatch
import org.w3c.dom.Element

@Suppress("unused")
val bubbleSupportPatch = bytecodePatch("Bubbles", default = false) {
    compatibleWith("com.discord")
    extendWith("extensions/discord.mpe") // TODO: move to discord-bubble.mpe?

    /*
        Classes/Methods to look into:
    com.discord.notifications.renderer.NotificationRenderer$display$1 -> invokeSuspend
    com.discord.notifications.renderer.utils.NotificationManagerUtilsKt -> notify (3 params)

     */

    dependsOn(
        resourcePatch {
            execute {
                document("AndroidManifest.xml").use { document ->
                    val app = document.getElementsByTagName("application").item(0) as Element;
                    val newActivity = document.createElement("activity");
                    newActivity.setAttribute("android:name","app.morphe.extension.discord.BubbleActivity")
                    newActivity.setAttribute("android:theme","@android:style/Theme.Translucent.NoTitleBar")
                    newActivity.setAttribute("android:label","@string/app_name")
                    newActivity.setAttribute("android:allowEmbedded","true")
                    newActivity.setAttribute("android:resizeableActivity","true")
                    newActivity.setAttribute("android:exported","true")
                    app.appendChild(newActivity)
                    /*<activity
                      android:name="app.morphe.extension.discord.BubbleActivity"
                      android:theme="@android:style/Theme.Translucent.NoTitleBar"
                      android:label="@string/app_name"
                      android:allowEmbedded="true"
                      android:resizeableActivity="true"
                      android:exported="true"
                    />*/
                }
            }
        }
    )
    execute {

    }
}
