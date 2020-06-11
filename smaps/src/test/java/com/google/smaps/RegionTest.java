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

/**
 * Unit tests for {@link Region}.
 */
@RunWith(JUnit4.class)
public class RegionTest {
  private Region r;

  @Before
  public void setUp() throws Exception {
    // Sets up a fake Region object.
    Region.Builder region = Region.builder();
    region.setLineNumber(1);
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
    assertEquals(1, r.lineNumber());
    assertEquals("16ec0000000", r.startLoc());
    assertEquals("16efa600000", r.endLoc());
    assertEquals("rw-p", r.permissions());
    assertEquals("001a1000", r.offset());
    assertEquals("08:01", r.device());
    assertEquals(7700, r.inode());
    assertEquals("/memfd:stan (deleted)", r.pathname());
    assertEquals(956416, r.size());
    assertEquals(4, r.kernelPageSize());
    assertEquals(8, r.mmuPageSize());
    assertEquals(0, r.rss());
    assertEquals(0, r.pss());
    assertEquals(16, r.sharedClean());
    assertEquals(0, r.sharedDirty());
    assertEquals(44, r.privateClean());
    assertEquals(0, r.privateDirty());
    assertEquals(8, r.referenced());
    assertEquals(20516, r.anonymous());
    assertEquals(0, r.lazyFree());
    assertEquals(256, r.anonHugePages());
    assertEquals(512, r.shmemHugePages());
    assertEquals(0, r.shmemPmdMapped());
    assertEquals(0, r.sharedHugetlb());
    assertEquals(0, r.privateHugetlb());
    assertEquals(262144, r.hugePFNMap());
    assertEquals(0, r.swap());
    assertEquals(0, r.swapPss());
    assertEquals(0, r.locked());

    List<String> expectedVmFlags = new ArrayList<>(Arrays.asList("mr", "mw", "me", "sd"));
    assertEquals(expectedVmFlags, r.vmFlags());
  }
}