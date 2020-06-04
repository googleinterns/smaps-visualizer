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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class RegionTest {
  private Region mockRegion;

  @Before
  public void setUp() throws Exception {
    // Sets up a fake Region object.
    List<String> flags = new ArrayList<>(Arrays.asList("mr", "mw", "me", "sd"));
    mockRegion = Region.create("16ec0000000", "16efa600000", "rw-p", 956416, 4, 8, 0, 0, 16, 0, 44,
        0, 8, 20516, 0, 256, 512, 0, 0, 0, 262144, 0, 0, 0, flags);
  }

  @Test
  public void RegionTest() {
    // Tests each field was set properly.
    String startLoc = mockRegion.startLoc();
    assertEquals(startLoc, "16ec0000000");

    String endLoc = mockRegion.endLoc();
    assertEquals(endLoc, "16efa600000");

    String permissions = mockRegion.permissions();
    assertEquals(permissions, "rw-p");

    long size = mockRegion.size();
    assertEquals(size, 956416);

    long kernelPageSize = mockRegion.kernelPageSize();
    assertEquals(kernelPageSize, 4);

    long mmuPageSize = mockRegion.mmuPageSize();
    assertEquals(mmuPageSize, 8);

    long rss = mockRegion.rss();
    assertEquals(rss, 0);

    long pss = mockRegion.pss();
    assertEquals(pss, 0);

    long sharedClean = mockRegion.sharedClean();
    assertEquals(sharedClean, 16);

    long sharedDirty = mockRegion.sharedDirty();
    assertEquals(sharedDirty, 0);

    long privateClean = mockRegion.privateClean();
    assertEquals(privateClean, 44);

    long privateDirty = mockRegion.privateDirty();
    assertEquals(privateDirty, 0);

    long referenced = mockRegion.referenced();
    assertEquals(referenced, 8);

    long anonymous = mockRegion.anonymous();
    assertEquals(anonymous, 20516);

    long lazyFree = mockRegion.lazyFree();
    assertEquals(lazyFree, 0);

    long anonHugePages = mockRegion.anonHugePages();
    assertEquals(anonHugePages, 256);

    long shmemHugePages = mockRegion.shmemHugePages();
    assertEquals(shmemHugePages, 512);

    long shmemPmdMapped = mockRegion.shmemPmdMapped();
    assertEquals(shmemPmdMapped, 0);

    long sharedHugetlb = mockRegion.sharedHugetlb();
    assertEquals(sharedHugetlb, 0);

    long privateHugetlb = mockRegion.privateHugetlb();
    assertEquals(privateHugetlb, 0);

    long hugePFNMap = mockRegion.hugePFNMap();
    assertEquals(hugePFNMap, 262144);

    long swap = mockRegion.swap();
    assertEquals(swap, 0);

    long swapPss = mockRegion.swapPss();
    assertEquals(swapPss, 0);

    long locked = mockRegion.locked();
    assertEquals(locked, 0);

    List<String> vmFlags = mockRegion.vmFlags();
    List<String> expectedVmFlags = new ArrayList<>(Arrays.asList("mr", "mw", "me", "sd"));
    assertEquals(vmFlags, expectedVmFlags);

    Region expectedRegion = Region.create("16ec0000000", "16efa600000", "rw-p", 956416, 4, 8, 0, 0,
        16, 0, 44, 0, 8, 20516, 0, 256, 512, 0, 0, 0, 262144, 0, 0, 0, expectedVmFlags);
    assertEquals(expectedRegion, mockRegion);
  }
}