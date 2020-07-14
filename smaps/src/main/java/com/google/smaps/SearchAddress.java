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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Retrieves the address entered by the user on memory-map.html, calculates which region occupies
 * that address, and sends that region's index in the regions list to memory-map.js.
 */
@WebServlet(name = "SearchAddress", value = "/searchaddress")
public class SearchAddress extends HttpServlet {
  // Stores the String representation of the hex address as the user entered it.
  static String address;
  // Stores the address as a BigInteger that will be used to find the region occupying it.
  static BigInteger addressBigInt;
  // Stores the error message to display on memory-map.html.
  static String errorMessage;

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Reset the error message to be blank.
    errorMessage = "";

    // Check whether the reset button was clicked or not.
    String resetButton = request.getParameter("reset-address-btn");
    if (resetButton != null) {
      // Reset was clicked, so remove the address from the searchbox and set addressBigInt to null.
      address = "";
      addressBigInt = null;
    } else {
      // Get the address the user entered.
      address = request.getParameter("address-input");

      // Use this regular expression to check that the address is actually hexadecimal, so that
      // there isn't a NumberFormatException. If there are characters in the address that aren't in
      // a valid hex number, set addressBigInt to null, and set errorMessage to proper error
      // message.
      if (address.matches("^[0-9a-fA-F]+$")) {
        // Convert the address to a BigInteger and set it to the global addressBigInt.
        addressBigInt = new BigInteger(address, 16);
      } else {
        errorMessage = "Address " + address + " is not a valid hexadecimal number.";
        addressBigInt = null;
      }
    }

    // Reload the interactive-histogram.html.
    response.sendRedirect("/memory-map.html");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Response will be a Json.
    response.setContentType("application/json");

    // Stores the index in the regions list that the region is in, if the address searched doesn't
    // match a region then index is set to -1.
    int index;

    // If there isn't an address to find (because the search box was blank, reset was clicked, or
    // the address wasn't a valid hex number), set index to -1.
    if (addressBigInt == null) {
      index = -1;
    } else {
      // Get the list of regions and the range map from the Analyzer.
      List<Region> regions = Analyzer.getRegionList();
      RangeMap<BigInteger, Region> addressRangeMap = Analyzer.getRangeMap();

      // Use the range map to get the region occupying the address the user entered, if there is no
      // match, r is set to null.
      Region r = addressRangeMap.get(addressBigInt);

      // Get the index of the region in the list, which is also the ID of the region in the memory
      // map. If r is null, then set errorMessage to the proper error message and index to -1.
      if (r != null) {
        index = regions.indexOf(r);
      } else {
        errorMessage = "Address " + address + " is not present in this memory map.";
        index = -1;
      }
    }

    // Transfer the region information into Jsons.
    Gson addressGson = new Gson();
    Gson indexGson = new Gson();
    Gson errorGson = new Gson();
    String addressJson = addressGson.toJson(address);
    String indexJson = indexGson.toJson(index);
    String errorJson = errorGson.toJson(errorMessage);

    // Create a list of the two Jsons.
    List<String> jsonList = new ArrayList<String>();
    jsonList.add(addressJson);
    jsonList.add(indexJson);
    jsonList.add(errorJson);

    // Write Json to memory-map.js
    response.getWriter().println(jsonList);
  }
}