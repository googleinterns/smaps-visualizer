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

import com.google.common.collect.RangeMap;
import java.math.BigInteger;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Unit tests for {@link Analyzer}.
 */
@RunWith(JUnit4.class)
public class AnalyzerTest {
  @Before
  public void createRegionsList() {
    // Creates the regions list from smaps-full.txt file.
    Analyzer.makeRegionList("../smaps-full.txt");
  }

  @Test
  public void retrieveRegionsList() {
    // Tests that the list Analyzer is returning is the same list that is created in FileParser.
    List<Region> regions = FileParser.parseRegionList("../smaps-full.txt");
    assertEquals(regions, Analyzer.getRegionList());
  }

  @Test
  public void createRangeMap() {
    // Tests that the range map was properly created for the regions from smaps-full.txt and
    // functions as expected.
    List<Region> regions = Analyzer.getRegionList();
    Analyzer.makeRangeMap(regions);
    RangeMap<BigInteger, Region> addressRangeMap = Analyzer.getRangeMap();

    // Variables for testing addresses in range map.
    Region r;
    BigInteger start;
    BigInteger within;
    BigInteger end;

    // Get the first region.
    r = regions.get(0);

    // Start of the address range of first region (inclusive).
    start = new BigInteger("16ec0000000", 16);
    assertEquals(addressRangeMap.get(start), r);

    // Somewhere within the address range of first region.
    within = new BigInteger("16ec0000035", 16);
    assertEquals(addressRangeMap.get(within), r);

    // End of the address range of first region (exclusive).
    end = new BigInteger("16efa600000", 16);
    assertNotEquals(addressRangeMap.get(end), r);

    // Get the last region.
    r = regions.get(1071);

    // Start of the address range of last region (inclusive).
    start = new BigInteger("ffffffffff600000", 16);
    assertEquals(addressRangeMap.get(start), r);

    // Somewhere within the address range of last region.
    within = new BigInteger("ffffffffff600050", 16);
    assertEquals(addressRangeMap.get(within), r);

    // End of the address range of last region (exclusive).
    end = new BigInteger("ffffffffff601000", 16);
    assertNotEquals(addressRangeMap.get(end), r);
  }
}