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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Unit tests for {@link FileParser}.
 */
@RunWith(JUnit4.class)
public class FileParserTest {
  private List<Region> regions;

  @Before
  public void createRegionsList() throws Exception {
    // Creates regions list from smaps-full.txt file.
    Analyzer.makeRegionList("../smaps-full.txt");
    regions = Analyzer.getRegionList();
  }

  @Test
  public void fileNotFound() {
    // Tests that an empty list is successfully returned with nonexistent file.
    Analyzer.makeRegionList("../fake-file.txt");
    List<Region> list = Analyzer.getRegionList();
    assertNull(list);
  }

  @Test
  public void wrongFileFormat() {
    // Tests that an empty list is successfully returned with a file that has too few parameters on
    // first line.
    Analyzer.makeRegionList("../smaps-wrong-format.txt");
    List<Region> list = Analyzer.getRegionList();
    asserNull(list);
  }

  @Test
  public void numberRegions() {
    // Tests the correct number of regions were added to list.
    int num = regions.size();
    assertEquals(1072, num);
  }

  @Test
  public void firstRegionAlloc() {
    // Tests the first region was set properly in the list.
    Region firstR = regions.get(0);
    int lineNumber = firstR.lineNumber();
    String startLoc = firstR.startLoc();
    String endLoc = firstR.endLoc();
    String pathname = firstR.pathname();
    long size = firstR.size();
    List<String> vmFlags = firstR.vmFlags();
    List<String> expectedVmFlags = new ArrayList<>(Arrays.asList("mr", "mw", "me", "sd"));

    assertEquals(1, lineNumber);
    assertEquals("16ec0000000", startLoc);
    assertEquals("16efa600000", endLoc);
    assertEquals("", pathname);
    assertEquals(956416, size);
    assertEquals(expectedVmFlags, vmFlags);
  }

  @Test
  public void middleRegionAlloc() {
    // Tests a middle region was set properly in the list.
    Region middleR = regions.get(641);
    int lineNumber = middleR.lineNumber();
    String startLoc = middleR.startLoc();
    String endLoc = middleR.endLoc();
    String pathname = middleR.pathname();
    long size = middleR.size();
    List<String> vmFlags = middleR.vmFlags();
    List<String> expectedVmFlags =
        new ArrayList<>(Arrays.asList("rd", "wr", "sh", "mr", "mw", "me", "ms", "lo", "sd"));

    assertEquals(14744, lineNumber);
    assertEquals("7fd148000000", startLoc);
    assertEquals("7fd148200000", endLoc);
    assertEquals("/memfd:mmapped-unicorn_memfd (deleted)", pathname);
    assertEquals(2048, size);
    assertEquals(expectedVmFlags, vmFlags);
  }

  @Test
  public void lastRegionAlloc() {
    // Tests the last region was set properly in the list.
    Region lastR = regions.get(1071);
    int lineNumber = lastR.lineNumber();
    String startLoc = lastR.startLoc();
    String endLoc = lastR.endLoc();
    String pathname = lastR.pathname();
    long size = lastR.size();
    List<String> vmFlags = lastR.vmFlags();
    List<String> expectedVmFlags = new ArrayList<>(Arrays.asList("rd", "ex"));

    assertEquals(24634, lineNumber);
    assertEquals("ffffffffff600000", startLoc);
    assertEquals("ffffffffff601000", endLoc);
    assertEquals("[vsyscall]", pathname);
    assertEquals(4, size);
    assertEquals(expectedVmFlags, vmFlags);
  }
}