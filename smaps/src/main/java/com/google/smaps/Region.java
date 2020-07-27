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
import java.io.Serializable;
import java.util.List;

/** Represents a region of memory from the dump. */
@AutoValue
abstract class Region implements Serializable {
  // The line number of the first line of the region print-out.
  abstract int lineNumber();
  // The start of the address space (inclusive).
  abstract String startLoc();
  // The end of the address space (exclusive).
  abstract String endLoc();
  // Permissions for the region: (R)ead, (W)rite, E(X)ecute, (S)hared, (P)rivate.
  abstract String permissions();
  // Region offset.
  abstract String offset();
  // The device (major:minor).
  abstract String device();
  // The inode on the device.
  abstract long inode();
  // The file associated with this mapping, If the pathname has been unlinked, the symbolic link
  // will contain the string '(deleted)'.
  abstract String pathname();
  // Size of the mapping in KiB.
  abstract long size();
  // The page size used by the kernel to back the virtual memory area.
  abstract long kernelPageSize();
  // The page size used by the MMU.
  abstract long mmuPageSize();
  // The amount of memory that is currently resident in RAM.
  abstract long rss();
  // The process' proportional share of this mapping.
  abstract long pss();
  // Number of shared clean pages - memory bytes that are shared with another process that have not
  // been modified since their mapping.
  abstract long sharedClean();
  // Number of shared dirty pages - memory bytes that are shared with another process that have been
  // modified since their mapping.
  abstract long sharedDirty();
  // Number of private clean pages - memory bytes that are not shared with another process that have
  // not been modified since their mapping.
  abstract long privateClean();
  // Number of private dirty pages - memory bytes that are not shared with another process that have
  // been modified since their mapping.
  abstract long privateDirty();
  // The amount of memory currently marked as referenced or accessed.
  abstract long referenced();
  // The amount of memory that does not belong to any file.
  abstract long anonymous();
  // The amount of memory marked by madvise(2).
  abstract long lazyFree();
  // Non-file backed huge pages mapped into user-space page tables.
  abstract long anonHugePages();
  // Memory used by shared memory and allocated with huge pages.
  abstract long shmemHugePages();
  // Shared memory mapped into user space with huge pages.
  abstract long shmemPmdMapped();
  // The amount of shared memory consumed by huge pages.
  abstract long sharedHugetlb();
  // The amount of private memory consumed by huge pages.
  abstract long privateHugetlb();
  // Sum of the previous two fields (sharedHugetlb and privateHugetlb).
  abstract long hugePFNMap();
  // How much would-be-anonymous memory is also used, but out on swap.
  abstract long swap();
  // Shows proportional swap share of this mapping, does not take into account swapped out page of
  // underlying shmem objects.
  abstract long swapPss();
  // Indicates whether the mapping is locked in memory or not.
  abstract long locked();
  // The kernel flags associated with the virtual memory area, encoded using two-letter codes. These
  // share a variety of details about a memory segment.
  abstract List<String> vmFlags();

  static Builder builder() {
    // TODO(sophbohr22): Add these set initializations for all fields.
    return new AutoValue_Region.Builder().setShmemHugePages(0).setHugePFNMap(0);
  }

  @AutoValue.Builder
  abstract static class Builder {
    abstract Builder setLineNumber(int value);
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