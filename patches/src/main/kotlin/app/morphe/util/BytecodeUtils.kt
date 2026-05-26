package app.morphe.util

import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.Reference

/**
 * Get the [Reference] of an [Instruction] as [T].
 * https://github.com/RookieEnough/De-Vanced/blob/6a3afb111afeaa15f1702b9895fd24117d4b3842/patches/src/main/kotlin/app/morphe/util/BytecodeUtils.kt#L450
 * @param T The type of [Reference] to cast to.
 * @return The [Reference] as [T] or null
 * if the [Instruction] is not a [ReferenceInstruction] or the [Reference] is not of type [T].
 * @see ReferenceInstruction
 */
inline fun <reified T : Reference> Instruction.getReference() = (this as? ReferenceInstruction)?.reference as? T