/*
    Based on https://github.com/RookieEnough/De-Vanced/blob/6a3afb111afeaa15f1702b9895fd24117d4b3842/patches/src/main/kotlin/app/morphe/patches/all/misc/transformation/TransformInstructionsPatch.kt#L3
 */

package app.morphe.patches.all.misc.iap

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.util.proxy.mutableTypes.MutableMethod
import app.morphe.patches.all.misc.transformation.transformInstructionsPatch
import app.morphe.patches.shared.misc.string.replaceStringPatch
import app.morphe.util.getRandomString
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction10x
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction

@Suppress("unused")
internal val disableIapPatch = bytecodePatch(
    name = "Disable IAP",
    description = "Disables inapp purchases by preventing connection to billing client",
    default = false,
) {
    dependsOn(
        replaceStringPatch(
            from = "com.android.vending.billing.InAppBillingService.BIND",
            to = getRandomString(10),
        ),
        replaceStringPatch(
            from = "com.android.vending",
            to = getRandomString(10),
        )
    )
}
