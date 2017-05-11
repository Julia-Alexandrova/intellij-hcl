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
package org.intellij.plugins.hcl.terraform;

import com.intellij.execution.configurations.PathEnvironmentVariableUtil;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@State(
    name = "TerraformToolProjectSettings",
    storages = @Storage(id = "terraform.xml", file = "terraform.xml")
)
public class TerraformToolProjectSettings implements PersistentStateComponent<TerraformToolProjectSettings.State> {
  private State myState = new State();

  public static TerraformToolProjectSettings getInstance(@NotNull final Project project) {
    return ServiceManager.getService(project, TerraformToolProjectSettings.class);
  }

  @Nullable
  @Override
  public State getState() {
    return myState;
  }

  @Override
  public void loadState(State state) {
    myState.myTerraformPath = state.myTerraformPath;
    myState.myMetadataVersion = state.myMetadataVersion;
  }

  public String getTerraformPath() {
    return myState.myTerraformPath;
  }

  public void setTerraformPath(String terraformPath) {
    myState.myTerraformPath = terraformPath;
  }

  public String getMetadataVersion() {
    return myState.myMetadataVersion;
  }

  public void setMetadataVersion(String version) {
    myState.myMetadataVersion = version;
  }

  public static class State {
    public String myTerraformPath = getDefaultTerraformPath();
    public String myMetadataVersion = getDefaultMetadataVersion();

    public String myLastMetadataETag = null;
    public List<String> myLastMetadataVersions = new ArrayList<String>();
  }

  private static String getDefaultTerraformPath() {
    File terraform = PathEnvironmentVariableUtil.findInPath("terraform");

    if (terraform != null && terraform.canExecute()) {
      return terraform.getAbsolutePath();
    }

    if (SystemInfo.isUnix) {
      return "terraform";
    } else {
      return "terraform.exe";
    }
  }

  private static String getDefaultMetadataVersion() {
    return "bundled";
  }

}
