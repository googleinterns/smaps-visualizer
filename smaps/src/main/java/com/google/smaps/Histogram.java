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
    Object[][] dataArray = makeDataArray(regionList);

    // Convert Java object to Json object.
    Gson gson = new Gson();
    String histogramJson = gson.toJson(dataArray);

    // Write Json to histogram.js
    response.getWriter().println(histogramJson);
  }

  /** Creates 2D array of data for histogram */
  static Object[][] makeDataArray(List<Region> regions) {
    // Array will contain both Strings and numbers, so must be of type Object.
    Object[][] dataArray = new Object[regions.size() + 1][2];
    dataArray[0][0] = "Range";
    dataArray[0][1] = "Size";

    for (int i = 0; i < regions.size(); i++) {
      Region curR = regions.get(i);
      String range = curR.startLoc() + " - " + curR.endLoc();
      Object val = (Object) curR.size();
      Object[] pair = {range, val};
      dataArray[i + 1] = pair;
    }

    return dataArray;
  }
}