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
import java.util.List;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Retrieves the address entered by the user on memory-map.html and calculates it's pixel location,
 * and sends that number as a Json to memory-map.js.
 */
@MultipartConfig
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
      }

      System.out.println("POST!----------------");
      System.out.println("address string: " + addressString);
      System.out.println("big int: " + addressBigInt);
    }

    // Reload the interactive-histogram.html.
    response.sendRedirect("/memory-map.html");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Response will be a Json.
    response.setContentType("application/json");

    int index;
    // If address is empty, set index to -1 so that the index check in memory-map.json will simply
    // set the screen to the top and not to a region.
    if (addressString == "") {
      index = -1;
    } else {
      // TODO(@sophhbohr22): Error checking.
      List<Region> regions = Analyzer.getRegionList();
      RangeMap<BigInteger, Region> addressRangeMap = Analyzer.getRangeMap();

      // Use the range map to get the region occupying the address the user entered.
      Region r = addressRangeMap.get(addressBigInt);

      // Get the index of the region in the list, which is also the ID of the region in the memory
      // map.
      index = regions.indexOf(r);
    }

    // Transfer the information to a Json list.
    Gson addressGson = new Gson();
    Gson indexGson = new Gson();
    String addressJson = addressGson.toJson(addressString);
    String indexJson = indexGson.toJson(index);

    // Create a list of the two Jsons.
    List<String> jsonList = new ArrayList<String>();
    jsonList.add(addressJson);
    jsonList.add(indexJson);

    System.out.println("GET!----------------");
    System.out.println("address string: " + addressString);
    System.out.println("big int: " + addressBigInt);
    System.out.println("index: " + indexJson);

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