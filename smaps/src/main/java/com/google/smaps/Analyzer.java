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

import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Takes in the data from the smaps file upload, analyzes it, and converts it into useful
 * structures.
 */
class Analyzer {
  /* Creates the list of regions from the file path using the parseRegionList method in the
   * FileParser class. */
  static List<Region> makeRegionList(String filePathname) {
    return FileParser.parseRegionList(filePathname);
  }

  /* Creates the address range map (interval map) with addresses as keys and the region in which it
   * can be found as the value. */
  static ImmutableRangeMap<BigInteger, Region> makeRangeMap(List<Region> regionList) {
    // Create the range map from a tree range map.
    RangeMap<BigInteger, Region> addressRangeMap = TreeRangeMap.create();

    // Go through every region and add it to the range map.
    for (int i = 0; i < regionList.size(); i++) {
      Region curR = regionList.get(i);

      // Parse the addresses with base 16 because they are hexadecimal.
      BigInteger start = new BigInteger(curR.startLoc(), 16);
      BigInteger end = new BigInteger(curR.endLoc(), 16);

      // Put the region into the range map with the address range [inclusive, exclusive).
      addressRangeMap.put(Range.closedOpen(start, end), curR);
    }

    // Return the range map in the form of an immutable range map, so that it is
    // serializable and able to be saved to an http session.
    return ImmutableRangeMap.copyOf(addressRangeMap);
  }

  /* Calculates the extrema of the sizes of the regions in the list, and returns them in an
   * array. */
  static long[] getMinMax(List<Region> regionList) {
    // Initialize the min and max to both be the first size in the list.
    long min = regionList.get(0).size();
    long max = regionList.get(0).size();

    // Go through every region in the list and check if its size is smaller than the min or greater
    // than the max.
    for (int i = 0; i < regionList.size(); i++) {
      long curSize = regionList.get(i).size();
      if (curSize < min) {
        min = curSize;
      }
      if (curSize > max) {
        max = curSize;
      }
    }

    // Set the variables for the extrema, and return them in an array.
    long[] extrema = {min, max};
    return extrema;
  }
}