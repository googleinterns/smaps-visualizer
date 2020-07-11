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
import java.util.Hashtable;
import java.util.List;

/**
 * Takes in the data from the smaps file upload and analyzes and converts it into useful structures.
 */
class Analyzer {
  // Holds all regions of the smaps dump.
  static List<Region> regions;
  // Holds the range map for the address ranges.
  static RangeMap<BigInteger, Region> addressRangeMap;
  // Holds the hashtable for the regions and their location on memory-map.html in pixels.
  static Hashtable<Region, Integer> pixelHashtable;

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
      // Put the region into the range map with the address range [inclusive, exclusive).
      addressRangeMap.put(Range.closedOpen(start, end), curR);
    }
  }

  /* Returns the address range map. */
  static RangeMap<BigInteger, Region> getRangeMap() {
    return addressRangeMap;
  }

  /* Creates a hash table that uses a region as the key and it's location on the page of
   * memory-map.html in pixels as the value. */
  static void makePixelHashtable(List<Region> regionsList) {
    pixelHashtable = new Hashtable<Region, Integer>();
    // Start with 100 pixels down the page to scroll a little past the buttons/header.
    int location = 100;
    // Go through each region and add it to the hashtable with it's pixel value.
    for (int i = regionsList.size() - 1; i >= 0; i--) {
      Region curR = regionsList.get(i);
      pixelHashtable.put(curR, location);
      // Increase the location by 40 because each region is 40 pixels tall.
      location += 40;
    }
  }

  /* Returns the region/pixel hashtable. */
  static Hashtable<Region, Integer> getPixelHashtable() {
    return pixelHashtable;
  }
}