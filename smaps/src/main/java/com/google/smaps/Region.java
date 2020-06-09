/**
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.smaps;

import com.google.auto.value.AutoValue;
import java.util.List;

/**
 * Represents a region of memory from the dump.
 */
@AutoValue
abstract class Region {
  // TODO(sophbohr22): Add Javadoc comments for each of these fields.
  abstract String startLoc();
  abstract String endLoc();
  abstract String permissions();
  abstract String offset();
  abstract String device();
  abstract long inode();
  abstract String pathname();
  abstract long size();
  abstract long kernelPageSize();
  abstract long mmuPageSize();
  abstract long rss();
  abstract long pss();
  abstract long sharedClean();
  abstract long sharedDirty();
  abstract long privateClean();
  abstract long privateDirty();
  abstract long referenced();
  abstract long anonymous();
  abstract long lazyFree();
  abstract long anonHugePages();
  abstract long shmemHugePages();
  abstract long shmemPmdMapped();
  abstract long sharedHugetlb();
  abstract long privateHugetlb();
  abstract long hugePFNMap();
  abstract long swap();
  abstract long swapPss();
  abstract long locked();
  abstract List<String> vmFlags();

  static Builder builder() {
    return new AutoValue_Region.Builder();
  }

  @AutoValue.Builder
  abstract static class Builder {
    abstract Builder setStartLoc(String value);
    abstract Builder setEndLoc(String value);
    abstract Builder setPermissions(String value);
    abstract Builder setOffset(String value);
    abstract Builder setDevice(String value);
    abstract Builder setInode(long value);
    abstract Builder setPathname(String value);
    abstract Builder setSize(long value);
    abstract Builder setKernelPageSize(long value);
    abstract Builder setMmuPageSize(long value);
    abstract Builder setRss(long value);
    abstract Builder setPss(long value);
    abstract Builder setSharedClean(long value);
    abstract Builder setSharedDirty(long value);
    abstract Builder setPrivateClean(long value);
    abstract Builder setPrivateDirty(long value);
    abstract Builder setReferenced(long value);
    abstract Builder setAnonymous(long value);
    abstract Builder setLazyFree(long value);
    abstract Builder setAnonHugePages(long value);
    abstract Builder setShmemHugePages(long value);
    abstract Builder setShmemPmdMapped(long value);
    abstract Builder setSharedHugetlb(long value);
    abstract Builder setPrivateHugetlb(long value);
    abstract Builder setHugePFNMap(long value);
    abstract Builder setSwap(long value);
    abstract Builder setSwapPss(long value);
    abstract Builder setLocked(long value);
    abstract Builder setVmFlags(List<String> value);

    abstract Region build();
  }
}