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
import javax.servlet.http.HttpSession;

/**
 * Retrieves histogram information and formats it into a Json array for creating the chart
 * and dashboard tools in histogram.js.
 */
@WebServlet(name = "Histogram", value = "/histogram")
public class Histogram extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get this user's session.
    HttpSession session = request.getSession();

    // Will hold the size bounds for the histogram.
    long lowerBound;
    long upperBound;

    // Check whether the reset button was clicked or not.
    String resetButton = request.getParameter("reset-bounds-btn");
    if (resetButton != null) {
      // Reset was clicked, so set the bounds to be the extrema of the sizes, so the user can see
      // the entire range of sizes.
      lowerBound = (long) session.getAttribute("minBound");
      upperBound = (long) session.getAttribute("maxBound");

      // Reset the field that searches for a specific string in a pathname.
      session.setAttribute("name", "");
    } else {
      // Get user-chosen numbers from form in interactive-histogram.html.
      String lower = request.getParameter("lower-bound");
      String upper = request.getParameter("upper-bound");

      // Parse the bound from user input and set the bounds to the chosen bounds.
      lowerBound = parseBound(lower);
      upperBound = parseBound(upper);

      // Check the name field to see if a specific string in a pathname was searched for, and if so
      // add it to the session.
      String name = request.getParameter("path-filter");
      if (name != null) {
        session.setAttribute("name", name);
      }
    }

    // Set the session's histogram bounds to the new bounds.
    session.setAttribute("lowerBound", lowerBound);
    session.setAttribute("upperBound", upperBound);

    // Set the postFired option to be true since the user has done at least one post.
    session.setAttribute("postFired", true);

    // Reload the interactive-histogram.html.
    response.sendRedirect("/interactive-histogram.html");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Response will be a Json.
    response.setContentType("application/json");

    // Get this user's session.
    HttpSession session = request.getSession();

    // Check whether doPost has been fired for the first time yet.
    boolean postFired = (boolean) session.getAttribute("postFired");

    // Will hold the size bounds for the histogram.
    long lowerBound;
    long upperBound;

    // Will hold the pathname from path filter.
    String name;

    // If the user hasn't entered custom bounds yet, use the initial min and max from regions list,
    // otherwise use the ones that were submitted previously and set to the session. If the user
    // hasn't chosen a specific name in the pathname to filter, just make it an emprty string,
    // otherwise get it from the session.
    if (!postFired) {
      lowerBound = (long) session.getAttribute("minBound");
      upperBound = (long) session.getAttribute("maxBound");
      name = "";

      // Set the new variables to the session.
      session.setAttribute("lowerBound", lowerBound);
      session.setAttribute("upperBound", upperBound);
      session.setAttribute("name", "");
    } else {
      lowerBound = (long) session.getAttribute("lowerBound");
      upperBound = (long) session.getAttribute("upperBound");
      name = (String) session.getAttribute("name");
    }

    // Get the region list from the session, which was set when the file was uploaded.
    List<Region> regionList = (List<Region>) session.getAttribute("regionList");

    // Parse histogram data.
    List<Object[]> histogramData = makeDataArray(regionList, name);

    // Construct 2D array to hold the bounds.
    long[] bounds = {lowerBound, upperBound};

    // Transfer the arrays into JavaScript Objects (Json).
    Gson boundsGson = new Gson();
    Gson nameGson = new Gson();
    Gson histGson = new Gson();
    String boundsJson = boundsGson.toJson(bounds);
    String nameJson = nameGson.toJson(name);
    String histogramDataJson = histGson.toJson(histogramData);

    // Put the two Json strings into a list to send to histogram.js.
    List<String> jsonList = new ArrayList<String>();
    jsonList.add(boundsJson);
    jsonList.add(nameJson);
    jsonList.add(histogramDataJson);

    // Write Json to histogram.js
    response.getWriter().println(jsonList);
  }

  /**
   * Returns the input from the forms in interactive-histogram.html parsed as a long. The bound
   * input forms in interactive-histogram.html only allow positive integers, but accept numbers with
   * .0, so in that case drop the decimal when parsing.
   */
  public long parseBound(String bound) {
    if (bound.contains(".")) {
      return (long) Double.parseDouble(bound);
    }
    return Long.parseLong(bound);
  }

  /** Creates list of 2D Object arrays of data for histogram. */
  static ArrayList<Object[]> makeDataArray(List<Region> regions, String name) {
    // List must contain both Strings and numbers, so must be of type Object.
    ArrayList<Object[]> dataArray = new ArrayList<Object[]>();

    // First pair in list must define the format of the data.
    Object[] labelPair = {"Range", "Size"};
    dataArray.add(labelPair);

    // Add an array to init the data, but make range null and the size -1 so that it won't show up
    // in the actual chart.
    Object[] initPair = {null, -1};
    dataArray.add(initPair);

    // Go through the regions and add range/size pairs to the list, and check that it contains in
    // its pathname the specific name that was searched for (if no name was searched, it will just
    // be an empty string).
    for (int i = 0; i < regions.size(); i++) {
      Region curR = regions.get(i);
      if (curR.pathname().contains(name)) {
        String range = curR.startLoc() + " - " + curR.endLoc();
        Object val = (Object) curR.size();
        Object[] pair = {range, val};
        dataArray.add(pair);
      }
    }
    return dataArray;
  }
}