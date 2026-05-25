/*
    Based on https://github.com/RookieEnough/De-Vanced/blob/6a3afb111afeaa15f1702b9895fd24117d4b3842/patches/src/main/kotlin/app/morphe/patches/all/misc/transformation/TransformInstructionsPatch.kt#L3
 */

package app.morphe.patches.all.misc.iap

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.util.proxy.mutableTypes.MutableClass
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction10x
import com.android.tools.smali.dexlib2.iface.ClassDef
import com.android.tools.smali.dexlib2.iface.Method
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.util.MethodUtil

@Suppress("unused")
internal val disableIapPatch = bytecodePatch (
    name = "Disable IAP",
    description = "Disables inapp purchases by preventing connection to billing client"
) {
    fun MutableClass.findMutableMethodOf(method: MethodReference) = this.methods.first {
        MethodUtil.methodSignaturesMatch(it, method)
    }

    fun findStartConnectionIndices(classDef: ClassDef, method: Method): Sequence<Int>? =
        method.implementation?.instructions?.asSequence()?.withIndex()?.mapNotNull { (index, instruction) ->
            if (instruction.opcode != Opcode.INVOKE_VIRTUAL) return@mapNotNull null
            val ref = (instruction as? ReferenceInstruction)?.reference as? MethodReference?: return@mapNotNull null
            if (ref.definingClass == "Lcom/android/billingclient/api/BillingClient;" && ref.name == "startConnection")
                index
            else
                null
        }

    execute {
        buildMap {
            classDefForEach { classDef ->
                val methods = buildList {
                    classDef.methods.forEach { method ->
                        if (findStartConnectionIndices(classDef, method)?.any() == true) add(method)
                    }
                }
                if (methods.isNotEmpty()) put(classDef, methods)
            }
        }.forEach { (classDef, methods) ->
            val mutableClass = mutableClassDefBy(classDef)

            methods.map(mutableClass::findMutableMethodOf).forEach methods@{ mutableMethod ->
                val indices = findStartConnectionIndices(mutableClass, mutableMethod)
                    ?.toCollection(ArrayDeque()) ?: return@methods

                while (!indices.isEmpty()) {
                    val index = indices.removeLast()
                    val nop = BuilderInstruction10x(Opcode.NOP)// idk if this is the correct way, maybe redir to a custom method would be better
                    mutableMethod.implementation!!.replaceInstruction(index, nop)
                }
            }
        }
    }
}