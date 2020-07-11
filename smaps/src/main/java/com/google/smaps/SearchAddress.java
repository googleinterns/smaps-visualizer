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

import com.google.common.collect.RangeMap;
import com.google.gson.Gson;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Retrieves the address entered by the user on memory-map.html and calculates it's pixel location,
 * and sends that number as a Json to memory-map.js.
 */
@WebServlet(name = "SearchAddress", value = "/searchaddress")
public class SearchAddress extends HttpServlet {
  // Stores the string representation of the hex address as the user entered it.
  static String addressString;
  // Stores the address that will be used to find the region occupying it.
  static BigInteger addressBigInt;

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Check whether the reset button was clicked or not.
    String resetButton = request.getParameter("reset-address-btn");
    if (resetButton != null) {
      // Reset was clicked, so remove the address from the searchbox.
      addressString = "";
      addressBigInt = null;
    } else {
      // Get the address the user entered and convert it to a BigInteger.
      String address = request.getParameter("address-input");

      // Set the address to the global addressString.
      addressString = address;

      // Check to make sure the user actually entered an address into the textbox before clicking
      // "go".
      if (address != null && !address.equals("")) {
        // Convert the address to a BigInteger and set it to the global addressBigInt.
        addressBigInt = new BigInteger(address, 16);
      } else {
        addressString = "";
        addressBigInt = null;
      }
      System.out.println("POST: " + addressBigInt);
    }

    // Reload the interactive-histogram.html.
    response.sendRedirect("/memory-map.html");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Response will be a Json.
    response.setContentType("application/json");

    // Location page will scroll to.
    int pixelLoc;

    // If address is empty, page will scroll to the top.
    if (addressString == "") {
      pixelLoc = 0;
    } else {
      // Get the range map and the region/pixel hashtable from the analyzer, since they were already
      // made during file upload.
      Hashtable<Region, Integer> pixelHashtable = Analyzer.getPixelHashtable();
      RangeMap<BigInteger, Region> addressRangeMap = Analyzer.getRangeMap();

      // Use the range map to get the region occupying the address the user entered.
      Region r = addressRangeMap.get(addressBigInt);

      // Use the hashtable to get the pixel location of the region found with the range map.
      pixelLoc = pixelHashtable.get(r);
    }

    // Transfer the pixel number into a Json.
    Gson addressGson = new Gson();
    Gson pixelGson = new Gson();
    String addressJson = addressGson.toJson(addressString);
    String pixelJson = pixelGson.toJson(pixelLoc);

    // Create a list of the two Jsons.
    List<String> jsonList = new ArrayList<String>();
    jsonList.add(addressJson);
    jsonList.add(pixelJson);

    System.out.println("GET: " + addressBigInt);
    System.out.println("PIXELJSON: " + pixelLoc);

    // Write Json to memory-map.js
    response.getWriter().println(jsonList);
  }

  /* This method is called with each new file upload, and resets the search box to be empty and the
   * scroll location to be at the top of the page.
   */
  public static void initializeAddress() {
    addressString = "";
    addressBigInt = null;
    return;
  }
}