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
package org.intellij.plugins.hcl.formatter;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiInvalidElementAccessException;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.psi.tree.TokenSet;
import org.intellij.plugins.hcl.HCLElementTypes;
import org.intellij.plugins.hcl.HCLParserDefinition;
import org.intellij.plugins.hcl.psi.HCLArray;
import org.intellij.plugins.hcl.psi.HCLElementVisitor;
import org.jetbrains.annotations.NotNull;

public class ArrayTailingCommaFormatter extends HCLElementVisitor {
  private Project myProject;
  private PsiElement myElement;
  private PsiDocumentManager myDocumentManager;
  private Document myDocument;
  private HCLCodeStyleSettings myFormattingSettings;
  private TextRange myRange;
  private TextRange myOrigRange;
  private int myDelta = 0;

  private static final TokenSet COMMENT_OR_WHITESPACE = TokenSet.orSet(HCLParserDefinition.HCL_COMMENTARIES, HCLParserDefinition.WHITE_SPACES);


  public ArrayTailingCommaFormatter(PsiElement element) {
    try {
      myProject = element.getProject();
      myElement = element;
      PsiFile file = element.getContainingFile();
      myDocumentManager = PsiDocumentManager.getInstance(myProject);
      myDocument = myDocumentManager.getDocument(file);
      myFormattingSettings = CodeStyleSettingsManager.getSettings(myProject).getCustomSettings(HCLCodeStyleSettings.class);
    } catch (PsiInvalidElementAccessException e) {
      myProject = null;
    }
  }

  public TextRange process(TextRange range) {
    if (myProject == null) return range;
    if (myDocument == null || !myFormattingSettings.PROPER_LAST_COMMA_IN_MULTILINE_ARRAYS) {
      return range;
    }
    myOrigRange = range;
    myRange = TextRange.create(range.getStartOffset(), range.getEndOffset());
    myDocumentManager.doPostponedOperationsAndUnblockDocument(myDocument);
    myElement.accept(this);
    myDocumentManager.commitDocument(myDocument);
    return myRange;
  }

  @Override
  public void visitArray(@NotNull final HCLArray array) {
    TextRange textRange = array.getTextRange();
    if (myOrigRange.contains(textRange) && !array.getValueList().isEmpty()) {
      processArray(array);
    }
    // Support nested arrays
    visitElement(array);
  }

  private void processArray(@NotNull final HCLArray array) {
    PsiElement last = array.getLastChild();
    if (last != null && last.getNode().getElementType().equals(HCLElementTypes.R_BRACKET)) last = last.getPrevSibling();
    while (last != null && COMMENT_OR_WHITESPACE.contains(last.getNode().getElementType())) {
      last = last.getPrevSibling();
    }

    if (last == null) return;
    if (myDocument.getLineNumber(array.getTextOffset()) == myDocument.getLineNumber(last.getTextOffset())) {
      if (HCLElementTypes.COMMA.equals(last.getNode().getElementType())) {
        // Remove last comma in single line array
        int delta = -last.getTextLength();
        myRange = TextRange.from(myRange.getStartOffset(), myRange.getLength() + delta);
        TextRange range = last.getTextRange();
        myDocument.deleteString(range.getStartOffset(), range.getEndOffset());
        myDelta += delta;
      }
      return;
    }
    if (HCLElementTypes.COMMA.equals(last.getNode().getElementType())) return;

    String addition = ",";
    int delta = addition.length();
    myRange = TextRange.from(myRange.getStartOffset(), myRange.getLength() + delta);
    myDocument.insertString(last.getTextRange().getEndOffset() + myDelta, addition);
    myDelta += delta;
  }

  @Override
  public void visitElement(PsiElement element) {
    element.acceptChildren(this);
  }
}