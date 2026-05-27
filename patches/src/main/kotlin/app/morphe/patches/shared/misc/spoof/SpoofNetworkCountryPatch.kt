package app.morphe.patches.shared.misc.spoof

import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.stringOption
import app.morphe.patches.all.misc.transformation.transformInstructionsPatch
import app.morphe.util.getReference
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction10x
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

@Suppress("unused")
internal val spoofNetworkCountryPatch = bytecodePatch(
    name = "Spoof network country",
    description = "Spoofs the country code returned by TelephonyManager",
    default = false,
) {
    val countryCode by stringOption(
        key = "countryCode",
        default = "us",
        title = "Country code",
        description = "The 2 letter country code to spoof (us, gb, de, etc.)",
        required = true,
    ) {
        it!!.matches(Regex("^[a-z]{2}$"))
    }

    dependsOn(
        transformInstructionsPatch(
            filterMap = filterMap@{ _, method, instruction, index ->
                if (instruction.opcode != Opcode.INVOKE_VIRTUAL) return@filterMap null

                val ref = instruction.getReference<MethodReference>() ?: return@filterMap null
                if (ref.definingClass != "Landroid/telephony/TelephonyManager;"
                    || ref.name != "getNetworkCountryIso") return@filterMap null

                val next = method.implementation?.instructions
                    ?.drop(index + 1)?.firstOrNull() ?: return@filterMap null
                if (next.opcode != Opcode.MOVE_RESULT_OBJECT) return@filterMap null

                Pair(index, (next as OneRegisterInstruction).registerA)
            },
            transform = { mutableMethod, (invokeIndex, register) ->
                mutableMethod.replaceInstruction(
                    invokeIndex + 1,
                    "const-string v$register, \"${countryCode!!}\"", // push into register to simulate return from func
                )
                mutableMethod.replaceInstruction( // don't invoke
                    invokeIndex,
                    BuilderInstruction10x(Opcode.NOP),
                )
            },
        )
    )

}
