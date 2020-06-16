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

import com.google.appengine.api.utils.SystemProperty;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet retrieves histogram information and formats it for creating the chart. */
@WebServlet(name = "Histogram", value = "/histogram")
public class Histogram extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");

    // Data must be in the form of a 2D array to be parsed properly in histogram.js.
    List<Region> regionList = FileParser.getRegionList("../smaps-full.txt");
    List<ArrayList<Object[]>> dataArrays = makeDataArrays(regionList);

    // Convert Java objects to Json objects.
    List<String> histogramJsonList = new ArrayList<String>();
    for (ArrayList<Object[]> d : dataArrays) {
      Gson gson = new Gson();
      String histogramJson = gson.toJson(d);
      histogramJsonList.add(histogramJson);
    }

    // Write Json to histogram.js
    response.getWriter().println(histogramJsonList);
  }

  /** Creates 2D array of data for histogram */
  static ArrayList<ArrayList<Object[]>> makeDataArrays(List<Region> regions) {
    // Array will contain both Strings and numbers, so must be of type Object.
    ArrayList<Object[]> smallDataArray = new ArrayList<Object[]>();
    ArrayList<Object[]> mediumDataArray = new ArrayList<Object[]>();
    ArrayList<Object[]> largeDataArray = new ArrayList<Object[]>();
    ArrayList<Object[]> xLargeDataArray = new ArrayList<Object[]>();
    addLabels(smallDataArray);
    addLabels(mediumDataArray);
    addLabels(largeDataArray);
    addLabels(xLargeDataArray);

    for (int i = 0; i < regions.size(); i++) {
      Region curR = regions.get(i);
      String range = curR.startLoc() + " - " + curR.endLoc();
      Long rSize = curR.size();
      Object val = (Object) rSize;
      Object[] pair = {range, val};
      if (rSize < 250) {
        smallDataArray.add(pair);
      } else if (rSize < 5000) {
        mediumDataArray.add(pair);
      } else if (rSize < 1000000) {
        largeDataArray.add(pair);
      } else {
        xLargeDataArray.add(pair);
      }
    }

    // Create a list of the all the data lists
    ArrayList<ArrayList<Object[]>> dataArrays = new ArrayList<ArrayList<Object[]>>();
    dataArrays.add(smallDataArray);
    dataArrays.add(mediumDataArray);
    dataArrays.add(largeDataArray);
    dataArrays.add(xLargeDataArray);

    return dataArrays;
  }

  static void addLabels(List<Object[]> array) {
    Object[] labelPair = {"Range", "Size"};
    array.add(labelPair);

    return;
  }
}