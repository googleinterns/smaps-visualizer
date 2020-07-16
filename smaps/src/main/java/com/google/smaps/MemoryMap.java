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

import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Retrieves memory map information and formats it into a Json array for creating the memory map and
 * key in memory-map.js.
 */
@WebServlet(name = "MemoryMap", value = "/memorymap")
public class MemoryMap extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Response will be a Json.
    response.setContentType("application/json");

    // Get the region list from the analyzer, since it was already made during file upload.
    List<Region> regionList = Analyzer.getRegionList();

    // Parse the memory map data.
    List<Object[]> memoryMapData = makeDataArray(regionList);

    // Transfer the Java Object arrays into JavaScript Objects (Json).
    Gson memoryMapGson = new Gson();
    String memoryMapJson = memoryMapGson.toJson(memoryMapData);

    // Write Json to memory-map.js.
    response.getWriter().println(memoryMapJson);
  }

  /** Creates list of 2D Object arrays of data for memory map. */
  static ArrayList<Object[]> makeDataArray(List<Region> regions) {
    // Holds all the arrays of information for the memory map.
    ArrayList<Object[]> dataArray = new ArrayList<Object[]>();

    // Go through the regions and add range/permissions pairs to the list.
    for (int i = 0; i < regions.size(); i++) {
      Region curR = regions.get(i);
      String start = formatAddress(curR.startLoc());
      String end = formatAddress(curR.endLoc());
      String range = start + " - " + end;
      Object permissions = (Object) curR.permissions();
      Object[] pair = {range, permissions};
      dataArray.add(pair);
    }
    return dataArray;
  }

  /**
   * Formats the address string to be 16 characters long by adding zeroes to the beginning of the
   * address to fill it.
   */
  static String formatAddress(String address) {
    String formattedString = address;
    while (formattedString.length() < 16) {
      formattedString = "0" + formattedString;
    }
    return formattedString;
  }
}