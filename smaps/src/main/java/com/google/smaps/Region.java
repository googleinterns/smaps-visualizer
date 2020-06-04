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
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a region of memory from the dump.
 */
@AutoValue
abstract class Region {
  private String startLoc;
  private String endLoc;
  private String permissions;
  private long size;
  private long kernelPageSize;
  private long mmuPageSize;
  private long rss;
  private long pss;
  private long sharedClean;
  private long sharedDirty;
  private long privateClean;
  private long privateDirty;
  private long referenced;
  private long anonymous;
  private long lazyFree;
  private long anonHugePages;
  private long shmemHugePages;
  private long shmemPmdMapped;
  private long sharedHugetlb;
  private long privateHugetlb;
  private long hugePFNMap;
  private long swap;
  private long swapPss;
  private long locked;
  private List<String> vmFlags;

  static Region create(String startLoc, String endLoc, String permissions, long size,
      long kernelPageSize, long mmuPageSize, long rss, long pss, long sharedClean, long sharedDirty,
      long privateClean, long privateDirty, long referenced, long anonymous, long lazyFree,
      long anonHugePages, long shmemHugePages, long shmemPmdMapped, long sharedHugetlb,
      long privateHugetlb, long hugePFNMap, long swap, long swapPss, long locked,
      List<String> vmFlags) {
    return new AutoValue_Region(startLoc, endLoc, permissions, size, kernelPageSize, mmuPageSize,
        rss, pss, sharedClean, sharedDirty, privateClean, privateDirty, referenced, anonymous,
        lazyFree, anonHugePages, shmemHugePages, shmemPmdMapped, sharedHugetlb, privateHugetlb,
        hugePFNMap, swap, swapPss, locked, vmFlags);
  }

  abstract String startLoc();
  abstract String endLoc();
  abstract String permissions();
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
}