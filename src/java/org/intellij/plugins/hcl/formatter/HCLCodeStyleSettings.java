/*
 * Copyright 2000-2016 JetBrains s.r.o.
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

import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import org.jetbrains.annotations.NotNull;

/**
 * @author Mikhail Golubev
 * @author Vladislav Rassokhin
 */
public class HCLCodeStyleSettings extends CustomCodeStyleSettings {

  public static int DO_NOT_ALIGN_PROPERTY = PropertyAlignment.DO_NOT_ALIGN.getId();
  public static int ALIGN_PROPERTY_ON_VALUE = PropertyAlignment.ALIGN_ON_VALUE.getId();
  public static int ALIGN_PROPERTY_ON_EQUALS = PropertyAlignment.ALIGN_ON_EQUALS.getId();

  // TODO: check whether it's possible to migrate CustomCodeStyleSettings to newer com.intellij.util.xmlb.XmlSerializer
  /**
   * Contains value of {@link PropertyAlignment#getId()}
   *
   * @see #DO_NOT_ALIGN_PROPERTY
   * @see #ALIGN_PROPERTY_ON_VALUE
   * @see #ALIGN_PROPERTY_ON_EQUALS
   */
  public int PROPERTY_ALIGNMENT = PropertyAlignment.ALIGN_ON_EQUALS.getId();

  public int OBJECT_WRAPPING = CommonCodeStyleSettings.WRAP_ALWAYS;
  // This was default policy for array elements wrapping in JavaScript's JSON.
  // CHOP_DOWN_IF_LONG seems more appropriate however for short arrays.
  public int ARRAY_WRAPPING = CommonCodeStyleSettings.DO_NOT_WRAP;

  public HCLCodeStyleSettings(CodeStyleSettings container, Language language) {
    super(language.getID(), container);
  }

  public enum PropertyAlignment {
    DO_NOT_ALIGN("Do not align", 0),
    ALIGN_ON_VALUE("On value", 1),
    ALIGN_ON_EQUALS("On equals", 2);

    private final String myDescription;
    private final int myId;

    PropertyAlignment(@NotNull String description, int id) {
      myDescription = description;
      myId = id;
    }

    @NotNull
    public String getDescription() {
      return myDescription;
    }

    public int getId() {
      return myId;
    }
  }
}
