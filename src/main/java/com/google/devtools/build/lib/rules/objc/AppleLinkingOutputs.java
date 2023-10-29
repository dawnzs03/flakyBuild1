// Copyright 2021 The Bazel Authors. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.devtools.build.lib.rules.objc;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.analysis.RuleConfiguredTargetBuilder;
import com.google.devtools.build.lib.collect.nestedset.NestedSet;
import com.google.devtools.build.lib.packages.BuiltinProvider;
import com.google.devtools.build.lib.packages.Provider;
import com.google.devtools.build.lib.packages.StarlarkInfo;
import com.google.devtools.build.lib.packages.StructImpl;
import com.google.devtools.build.lib.rules.cpp.CcInfo;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.starlark.java.syntax.Location;

/**
 * The providers and artifact outputs returned by the {@code apple_common.link_multi_arch_binary}
 * API.
 */
public class AppleLinkingOutputs {

  /** Represents an Apple target triplet (arch, platform, env) for a multi-arch target. */
  @AutoValue
  public abstract static class TargetTriplet {
    static TargetTriplet create(String architecture, String platform, String environment) {
      return new AutoValue_AppleLinkingOutputs_TargetTriplet(architecture, platform, environment);
    }

    abstract String architecture();

    abstract String platform();

    abstract String environment();

    /** Returns a Starlark Dict representation of a {@link TargetTriplet} */
    public final StructImpl toStarlarkStruct() {
      Provider constructor = new BuiltinProvider<StructImpl>("target_triplet", StructImpl.class) {};
      HashMap<String, Object> fields = new HashMap<>();
      fields.put("architecture", architecture());
      fields.put("environment", environment());
      fields.put("platform", platform());
      return StarlarkInfo.create(constructor, fields, Location.BUILTIN);
    }
  }

  /**
   * A set of related platform/architecture-specific outputs generated by {@code
   * apple_common.link_multi_arch_binary}.
   */
  @AutoValue
  public abstract static class LinkingOutput {

    abstract TargetTriplet getTargetTriplet();

    abstract Artifact getBinary();

    @Nullable
    abstract Artifact getDsymBinary();

    @Nullable
    abstract Artifact getLinkmap();

    static LinkingOutput.Builder builder() {
      return new AutoValue_AppleLinkingOutputs_LinkingOutput.Builder();
    }

    /** Builder for {@link LinkingOutput}. */
    @AutoValue.Builder
    public abstract static class Builder {
      abstract Builder setTargetTriplet(TargetTriplet targetTriplet);

      abstract Builder setBinary(Artifact binary);

      abstract Builder setDsymBinary(Artifact dsymBinary);

      abstract Builder setLinkmap(Artifact linkmap);

      abstract LinkingOutput build();
    }
  }

  private final CcInfo depsCcInfo;
  private final ObjcProvider depsObjcProvider;
  private final ImmutableList<LinkingOutput> outputs;
  private final ImmutableMap<String, NestedSet<Artifact>> outputGroups;

  private final AppleDebugOutputsInfo legacyDebugOutputsProvider;

  AppleLinkingOutputs(
      CcInfo depsCcInfo,
      ObjcProvider depsObjcProvider,
      ImmutableList<LinkingOutput> outputs,
      ImmutableMap<String, NestedSet<Artifact>> outputGroups,
      AppleDebugOutputsInfo legacyDebugOutputsProvider) {
    this.depsCcInfo = depsCcInfo;
    this.depsObjcProvider = depsObjcProvider;
    this.outputs = outputs;
    this.outputGroups = outputGroups;

    this.legacyDebugOutputsProvider = legacyDebugOutputsProvider;
  }

  /**
   * Returns an {@link ObjcProvider} containing information about the transitive dependencies linked
   * into the binary.
   */
  public ObjcProvider getDepsObjcProvider() {
    return depsObjcProvider;
  }

  /**
   * Returns an {@link CcInfo} containing information about the transitive dependencies linked into
   * the binary.
   */
  public CcInfo getDepsCcInfo() {
    return depsCcInfo;
  }

  /** Returns the list of single-architecture/platform outputs. */
  public ImmutableList<LinkingOutput> getOutputs() {
    return outputs;
  }

  /**
   * Returns a {@link AppleDebugOutputsInfo} containing debug information about the linked binary.
   */
  public AppleDebugOutputsInfo getLegacyDebugOutputsProvider() {
    return legacyDebugOutputsProvider;
  }

  /**
   * Returns a map from output group name to set of artifacts belonging to this output group. This
   * should be added to configured target information using {@link
   * RuleConfiguredTargetBuilder#addOutputGroups(Map)}.
   */
  public Map<String, NestedSet<Artifact>> getOutputGroups() {
    return outputGroups;
  }

  /** A builder for {@link AppleBinaryOutput}. */
  public static class Builder {
    private final ImmutableList.Builder<LinkingOutput> outputs;
    private final ImmutableMap.Builder<String, NestedSet<Artifact>> outputGroups;
    private ObjcProvider depsObjcProvider;
    private CcInfo depsCcInfo;

    private AppleDebugOutputsInfo legacyDebugOutputsProvider;

    public Builder() {
      this.outputs = ImmutableList.builder();
      this.outputGroups = ImmutableMap.builder();
    }

    /** Adds a set of related single-architecture/platform artifacts to the output result. */
    @CanIgnoreReturnValue
    public Builder addOutput(LinkingOutput output) {
      outputs.add(output);
      return this;
    }

    /** Adds a set of output groups to the output result. */
    @CanIgnoreReturnValue
    public Builder addOutputGroups(Map<String, NestedSet<Artifact>> outputGroupsToAdd) {
      outputGroups.putAll(outputGroupsToAdd);
      return this;
    }

    /** Sets the legacy debug outputs provider of the output result. */
    @CanIgnoreReturnValue
    public Builder setLegacyDebugOutputsProvider(AppleDebugOutputsInfo debugOutputsProvider) {
      this.legacyDebugOutputsProvider = debugOutputsProvider;
      return this;
    }

    /**
     * Sets the {@link CcInfo} that contains information about transitive dependencies linked into
     * the binary.
     */
    @CanIgnoreReturnValue
    public Builder setDepsCcInfo(CcInfo depsCcInfo) {
      this.depsCcInfo = depsCcInfo;
      return this;
    }

    /**
     * Sets the {@link ObjcProvider} that contains information about transitive dependencies linked
     * into the binary.
     */
    @CanIgnoreReturnValue
    public Builder setDepsObjcProvider(ObjcProvider depsObjcProvider) {
      this.depsObjcProvider = depsObjcProvider;
      return this;
    }

    public AppleLinkingOutputs build() {
      return new AppleLinkingOutputs(
          depsCcInfo,
          depsObjcProvider,
          outputs.build(),
          outputGroups.buildOrThrow(),
          legacyDebugOutputsProvider);
    }
  }
}
