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
    assertEquals(startLoc, "16ec0000000");

    String endLoc = r.endLoc();
    assertEquals(endLoc, "16efa600000");

    String permissions = r.permissions();
    assertEquals(permissions, "rw-p");

    String offset = r.offset();
    assertEquals(offset, "001a1000");

    String device = r.device();
    assertEquals(device, "08:01");

    long inode = r.inode();
    assertEquals(inode, 7700);

    String pathname = r.pathname();
    assertEquals(pathname, "/memfd:stan (deleted)");

    long size = r.size();
    assertEquals(size, 956416);

    long kernelPageSize = r.kernelPageSize();
    assertEquals(kernelPageSize, 4);

    long mmuPageSize = r.mmuPageSize();
    assertEquals(mmuPageSize, 8);

    long rss = r.rss();
    assertEquals(rss, 0);

    long pss = r.pss();
    assertEquals(pss, 0);

    long sharedClean = r.sharedClean();
    assertEquals(sharedClean, 16);

    long sharedDirty = r.sharedDirty();
    assertEquals(sharedDirty, 0);

    long privateClean = r.privateClean();
    assertEquals(privateClean, 44);

    long privateDirty = r.privateDirty();
    assertEquals(privateDirty, 0);

    long referenced = r.referenced();
    assertEquals(referenced, 8);

    long anonymous = r.anonymous();
    assertEquals(anonymous, 20516);

    long lazyFree = r.lazyFree();
    assertEquals(lazyFree, 0);

    long anonHugePages = r.anonHugePages();
    assertEquals(anonHugePages, 256);

    long shmemHugePages = r.shmemHugePages();
    assertEquals(shmemHugePages, 512);

    long shmemPmdMapped = r.shmemPmdMapped();
    assertEquals(shmemPmdMapped, 0);

    long sharedHugetlb = r.sharedHugetlb();
    assertEquals(sharedHugetlb, 0);

    long privateHugetlb = r.privateHugetlb();
    assertEquals(privateHugetlb, 0);

    long hugePFNMap = r.hugePFNMap();
    assertEquals(hugePFNMap, 262144);

    long swap = r.swap();
    assertEquals(swap, 0);

    long swapPss = r.swapPss();
    assertEquals(swapPss, 0);

    long locked = r.locked();
    assertEquals(locked, 0);

    List<String> vmFlags = r.vmFlags();
    List<String> expectedVmFlags = new ArrayList<>(Arrays.asList("mr", "mw", "me", "sd"));
    assertEquals(vmFlags, expectedVmFlags);
  }
}