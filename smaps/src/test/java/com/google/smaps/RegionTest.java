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
  private Region r;

  @Before
  public void setUp() throws Exception {
    // Sets up a fake Region object.
    Region.Builder region = Region.builder();
    region.setStartLoc("16ec0000000");
    region.setEndLoc("16efa600000");
    region.setPermissions("rw-p");
    region.setOffset("001a1000");
    region.setDevice("08:01");
    region.setInode(7700);
    region.setPathname("/memfd:stan (deleted)");
    region.setSize(956416);
    region.setKernelPageSize(4);
    region.setMmuPageSize(8);
    region.setRss(0);
    region.setPss(0);
    region.setSharedClean(16);
    region.setSharedDirty(0);
    region.setPrivateClean(44);
    region.setPrivateDirty(0);
    region.setReferenced(8);
    region.setAnonymous(20516);
    region.setLazyFree(0);
    region.setAnonHugePages(256);
    region.setShmemHugePages(512);
    region.setShmemPmdMapped(0);
    region.setSharedHugetlb(0);
    region.setPrivateHugetlb(0);
    region.setHugePFNMap(262144);
    region.setSwap(0);
    region.setSwapPss(0);
    region.setLocked(0);
    List<String> flags = new ArrayList<>(Arrays.asList("mr", "mw", "me", "sd"));
    region.setVmFlags(flags);

    r = region.build();
  }

  @Test
  public void regionCreation() {
    // Tests each field was set properly.
    String startLoc = r.startLoc();
    assertEquals("16ec0000000", startLoc);

    String endLoc = r.endLoc();
    assertEquals("16efa600000", endLoc);

    String permissions = r.permissions();
    assertEquals("rw-p", permissions);

    String offset = r.offset();
    assertEquals("001a1000", offset);

    String device = r.device();
    assertEquals("08:01", device);

    long inode = r.inode();
    assertEquals(7700, inode);

    String pathname = r.pathname();
    assertEquals("/memfd:stan (deleted)", pathname);

    long size = r.size();
    assertEquals(956416, size);

    long kernelPageSize = r.kernelPageSize();
    assertEquals(4, kernelPageSize);

    long mmuPageSize = r.mmuPageSize();
    assertEquals(8, mmuPageSize);

    long rss = r.rss();
    assertEquals(0, rss);

    long pss = r.pss();
    assertEquals(0, pss);

    long sharedClean = r.sharedClean();
    assertEquals(16, sharedClean);

    long sharedDirty = r.sharedDirty();
    assertEquals(0, sharedDirty);

    long privateClean = r.privateClean();
    assertEquals(44, privateClean);

    long privateDirty = r.privateDirty();
    assertEquals(0, privateDirty);

    long referenced = r.referenced();
    assertEquals(8, referenced);

    long anonymous = r.anonymous();
    assertEquals(20516, anonymous);

    long lazyFree = r.lazyFree();
    assertEquals(0, lazyFree);

    long anonHugePages = r.anonHugePages();
    assertEquals(256, anonHugePages);

    long shmemHugePages = r.shmemHugePages();
    assertEquals(512, shmemHugePages);

    long shmemPmdMapped = r.shmemPmdMapped();
    assertEquals(0, shmemPmdMapped);

    long sharedHugetlb = r.sharedHugetlb();
    assertEquals(0, sharedHugetlb);

    long privateHugetlb = r.privateHugetlb();
    assertEquals(0, privateHugetlb);

    long hugePFNMap = r.hugePFNMap();
    assertEquals(262144, hugePFNMap);

    long swap = r.swap();
    assertEquals(0, swap);

    long swapPss = r.swapPss();
    assertEquals(0, swapPss);

    long locked = r.locked();
    assertEquals(0, locked);

    List<String> vmFlags = r.vmFlags();
    List<String> expectedVmFlags = new ArrayList<>(Arrays.asList("mr", "mw", "me", "sd"));
    assertEquals(expectedVmFlags, vmFlags);
  }
}