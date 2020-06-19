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
 * Servlet retrieves histogram information and formats it into a Json array for creating the chart
 * and dashboard tools in histogram.js.
 */
@WebServlet(name = "Histogram", value = "/histogram")
public class Histogram extends HttpServlet {
  static long lowerBound; // used to populate the lower bound text box and slider min
  static long upperBound; // used to populate the upper bound text box and slider max
  static boolean postFired = false; // flag indicating whether a user has entered info in textbox

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get numbers from form in interactive-histogram.html.
    String lower = request.getParameter("lower-bound");
    String upper = request.getParameter("upper-bound");
    postFired = true;

    // In case user entered a number with .0 as the decimal, cast to long to drop the decimal,
    // otherwise just convert the String to a Long.
    if (lower.contains(".")) {
      double lowerDecimal = Double.parseDouble(lower);
      lowerBound = (long) lowerDecimal;
    } else {
      lowerBound = Long.parseLong(lower);
    }
    if (upper.contains(".")) {
      double upperDecimal = Double.parseDouble(upper);
      upperBound = (long) upperDecimal;
    } else {
      upperBound = Long.parseLong(upper);
    }

    // Reload the interactive-histogram.html.
    response.sendRedirect("/interactive-histogram.html");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    List<Region> regionList = FileParser.getRegionList("../smaps-full.txt");

    // Parse histogram data
    List<Object[]> histogramData = makeDataArray(regionList);

    // If the user hasn't entered custom bounds yet, use the min and max from regions list.
    if (!postFired) {
      setMinMax(regionList);
    }

    // Construct 2D array to hold the bounds.
    long[] bounds = new long[2];
    bounds[0] = lowerBound;
    bounds[1] = upperBound;

    // Transfer the Java Object arrays into JavaScript Objects (Json).
    Gson boundsGson = new Gson();
    Gson histGson = new Gson();
    String histogramDataJson = histGson.toJson(histogramData);
    String boundsJson = boundsGson.toJson(bounds);

    // Put the two Json strings into a list to send to histogram.js.
    List<String> jsonList = new ArrayList<String>();
    jsonList.add(boundsJson);
    jsonList.add(histogramDataJson);

    // Write Json to histogram.js
    response.getWriter().println(jsonList);
  }

  /** Creates list of 2D Object arrays of data for histogram */
  static ArrayList<Object[]> makeDataArray(List<Region> regions) {
    // List must contain both Strings and numbers, so must be of type Object.
    ArrayList<Object[]> dataArray = new ArrayList<Object[]>();

    // First pair in list must define the format of the data.
    Object[] labelPair = {"Range", "Size"};
    dataArray.add(labelPair);

    // Go through the regions and add range/size pairs to the list.
    for (int i = 0; i < regions.size(); i++) {
      Region curR = regions.get(i);
      String range = curR.startLoc() + " - " + curR.endLoc();
      Object val = (Object) curR.size();
      Object[] pair = {range, val};
      dataArray.add(pair);
    }

    return dataArray;
  }

  /** Set the two global bounds variables to be the min and max sizes of regions in the list. */
  static void setMinMax(List<Region> regions) {
    long min = regions.get(0).size();
    long max = regions.get(0).size();

    for (int i = 0; i < regions.size(); i++) {
      long curSize = regions.get(i).size();
      if (curSize < min) {
        min = curSize;
      }
      if (curSize > max) {
        max = curSize;
      }
    }

    lowerBound = min;
    upperBound = max;
  }
}