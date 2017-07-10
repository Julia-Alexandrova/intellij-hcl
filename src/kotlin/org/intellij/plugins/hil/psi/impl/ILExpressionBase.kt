/*
 * Copyright 2000-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.intellij.plugins.hil.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElementVisitor
import org.intellij.plugins.hcl.psi.HCLElement
import org.intellij.plugins.hil.psi.ILElementVisitor
import org.intellij.plugins.hil.psi.ILExpression

abstract class ILExpressionBase(node: ASTNode) : ASTWrapperPsiElement(node), ILExpression {

  override fun accept(visitor: PsiElementVisitor) {
    if (visitor is ILElementVisitor) {
      visitor.visitILExpression(this)
    } else {
      visitor.visitElement(this)
    }
  }

  open fun getTypeClass(): Class<out Any>? {
    return null
  }

  override fun toString(): String {
    val name = this.javaClass.simpleName
    val trimmed = StringUtil.trimEnd(name, "Impl")
    if (trimmed.startsWith("ILBinary")) return "ILBinaryExpression"
    if ("ILLiteralExpression" == trimmed || "ILParameterListExpression" == trimmed) return StringUtil.trimEnd(trimmed, "Expression")
    return trimmed
  }

}

fun ILExpression.getHCLHost(): HCLElement? {
  val host = InjectedLanguageManager.getInstance(this.project).getInjectionHost(this)
  if (host is ILExpression && host != this) return host.getHCLHost()
  return if (host is HCLElement) host else null
}
