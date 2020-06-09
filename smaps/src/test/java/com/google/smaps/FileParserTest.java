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
import java.lang.IllegalArgumentException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class FileParserTest {
  private List<Region> regions;

  @Before
  public void setUp() throws Exception {
    // Creates regions list from smaps-full.txt file.
    String filePathName = "../smaps-full.txt";
    regions = FileParser.getRegionList(filePathName);
  }

  @Test
  public void fileNotFound() {
    // Tests an exception is caught for nonexistent file, and ensures list is empty.
    String wrongFilePathName = "../fakeFile.txt";
    List<Region> list = FileParser.getRegionList(wrongFilePathName);
    assertEquals(0, list.size());
  }

  @Test
  public void illegalFileType() {
    // Tests an exception is caught for wrong file type, and ensures list is empty.
    String wrongFilePathName = "../README.md";
    List<Region> list = FileParser.getRegionList(wrongFilePathName);
    assertEquals(0, list.size());
  }

  @Test
  public void numberRegions() {
    // Tests the correct number of regions were added to list.
    int expectedNum = 1072;
    int num = regions.size();
    assertEquals(expectedNum, num);
  }

  @Test
  public void firstRegionAlloc() {
    // Tests the first region was set properly in the list.
    Region firstR = regions.get(0);
    String startLoc = firstR.startLoc();
    String endLoc = firstR.endLoc();
    String pathname = firstR.pathname();
    long size = firstR.size();
    List<String> vmFlags = firstR.vmFlags();
    List<String> expectedVmFlags = new ArrayList<>(Arrays.asList("mr", "mw", "me", "sd"));

    assertEquals(startLoc, "16ec0000000");
    assertEquals(endLoc, "16efa600000");
    assertEquals(pathname, "");
    assertEquals(size, 956416);
    assertEquals(vmFlags, expectedVmFlags);
  }

  @Test
  public void middleRegionAlloc() {
    // Tests a middle region was set properly in the list.
    Region middleR = regions.get(641);
    String startLoc = middleR.startLoc();
    String endLoc = middleR.endLoc();
    String pathname = middleR.pathname();
    long size = middleR.size();
    List<String> vmFlags = middleR.vmFlags();
    List<String> expectedVmFlags =
        new ArrayList<>(Arrays.asList("rd", "wr", "sh", "mr", "mw", "me", "ms", "lo", "sd"));

    assertEquals(startLoc, "7fd148000000");
    assertEquals(endLoc, "7fd148200000");
    assertEquals(pathname, "/memfd:mmapped-unicorn_memfd (deleted)");
    assertEquals(size, 2048);
    assertEquals(vmFlags, expectedVmFlags);
  }

  @Test
  public void lastRegionAlloc() {
    // Tests the last region was set properly in the list.
    Region lastR = regions.get(1071);
    String startLoc = lastR.startLoc();
    String endLoc = lastR.endLoc();
    String pathname = lastR.pathname();
    long size = lastR.size();
    List<String> vmFlags = lastR.vmFlags();
    List<String> expectedVmFlags = new ArrayList<>(Arrays.asList("rd", "ex"));

    assertEquals(startLoc, "ffffffffff600000");
    assertEquals(endLoc, "ffffffffff601000");
    assertEquals(pathname, "[vsyscall]");
    assertEquals(size, 4);
    assertEquals(vmFlags, expectedVmFlags);
  }
}