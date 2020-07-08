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

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Takes in the data from the smaps file upload and analyzes and converts it into useful structures.
 */
class Analyzer {
  // Holds all regions of the smaps dump.
  static List<Region> regions;
  // Holds the range map for the address ranges.
  static RangeMap<BigInteger, Region> addressRangeMap;

  /* Creates the list of regions in this class using FileParser's method so it can be accessed by
   * multiple visualizations. */
  static void makeRegionList(String filePathname) {
    regions = FileParser.parseRegionList(filePathname);
  }

  /* Returns the list of regions. */
  static List<Region> getRegionList() {
    return regions;
  }

  /* Creates the address range map (interval map) with addresses as keys and the region occupying
   * that address as the value. */
  static void makeRangeMap(List<Region> regionsList) {
    // Create the range map.
    addressRangeMap = TreeRangeMap.create();
    // Go through every region and add it to the range map.
    for (int i = 0; i < regionsList.size(); i++) {
      Region curR = regionsList.get(i);
      // Parse the addresses with base 16 because they are hexadecimal.
      BigInteger start = new BigInteger(curR.startLoc(), 16);
      BigInteger end = new BigInteger(curR.endLoc(), 16);
      // Put the region into the range map with the address range (inclusive, exclusive).
      addressRangeMap.put(Range.closedOpen(start, end), curR);
    }
  }

  /* Returns the address range map. */
  static RangeMap<BigInteger, Region> getRangeMap() {
    return addressRangeMap;
  }
}