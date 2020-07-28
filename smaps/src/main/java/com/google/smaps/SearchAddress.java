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
import com.google.gson.Gson;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Retrieves the address entered by the user on memory-map.html, calculates which region occupies
 * that address, and sends that region's index in the regions list to memory-map.js.
 */
@WebServlet(name = "SearchAddress", value = "/searchaddress")
public class SearchAddress extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get this user's session.
    HttpSession session = request.getSession();

    // Reset the error message to be blank.
    session.setAttribute("addressErrorMessage", "");

    // Check whether the reset button was clicked or not.
    String resetButton = request.getParameter("reset-address-btn");
    if (resetButton != null) {
      // Reset was clicked, so remove the address from the searchbox and set addressBigInt to null.
      session.setAttribute("address", "");
      session.setAttribute("addressBigInt", null);
    } else {
      // Get the address the user entered and set it to the session.
      String originalAddress = request.getParameter("address-input");
      session.setAttribute("originalAddress", originalAddress);

      // Convert the original address into a readable format, and set it to the session.
      String address = addressParser(originalAddress);
      session.setAttribute("address", address);

      // Use this regular expression to check that the address is actually hexadecimal, so that
      // there isn't a NumberFormatException.
      if (address.matches("^[0-9a-fA-F]+$")) {
        // Convert the address to a BigInteger and set it to the global addressBigInt.
        BigInteger addressBigInt = new BigInteger(address, 16);
        session.setAttribute("addressBigInt", addressBigInt);
      } else {
        // There are characters in the address that aren't in a valid hex number, so set
        // addressErrorMessage to proper error message and addressBigInt to null.
        String addressErrorMessage =
            "Address [" + originalAddress + "] is not a valid hexadecimal number.";
        session.setAttribute("addressErrorMessage", addressErrorMessage);
        session.setAttribute("addressBigInt", null);
      }
    }

    // Reload the interactive-histogram.html.
    response.sendRedirect("/memory-map.html");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Response will be a Json.
    response.setContentType("application/json");

    // Get this user's session.
    HttpSession session = request.getSession();

    // Get the address string that the user actually entered, and the error message if there is one.
    String address = (String) session.getAttribute("address");
    String addressErrorMessage = (String) session.getAttribute("addressErrorMessage");

    // Index is set to -1 and r is set to null, and changed to a valid index and region if the
    // address is a valid address for this memory map.
    int index = -1;
    Region r = null;
    session.setAttribute("index", index);
    session.setAttribute("r", r);

    // If addressBigInt is null, it means the search box was blank, reset was clicked, or the
    // address wasn't a valid hex number, so index and r should stay at -1 and null.
    BigInteger addressBigInt = (BigInteger) session.getAttribute("addressBigInt");
    if (addressBigInt != null) {
      // Get the list of regions.
      List<Region> regions = (List<Region>) session.getAttribute("regionList");

      // Get the range map from the session, which is able to take in an address, figure out which
      // address range it lies within, and return the region corresponding to that range.
      ImmutableRangeMap<BigInteger, Region> addressRangeMap =
          (ImmutableRangeMap<BigInteger, Region>) session.getAttribute("rangeMap");

      // Use the range map to get the region in which the address the user entered is in, if there
      // is no match, r is set to null.
      r = addressRangeMap.get(addressBigInt);
      session.setAttribute("r", r);

      // Get the index of the region in the list, which is also the ID of the region in the memory
      // map. If r is null, then set addressErrorMessage to the proper error message.
      if (r != null) {
        index = regions.indexOf(r);
        session.setAttribute("index", index);
      } else {
        String originalAddress = (String) session.getAttribute("originalAddress");
        addressErrorMessage = "No region in which address [" + originalAddress + "] can be found.";
        session.setAttribute("addressErrorMessage", addressErrorMessage);
      }
    }

    // Put all the fields that will be turned into Jsons into an array.
    Object[] fields = {address, index, addressErrorMessage, r};

    // Create a list that all the Json objects will go into.
    List<String> jsonList = new ArrayList<String>();

    // Go through each field and turn it into a Json, and add it to the list of Jsons.
    for (Object field : fields) {
      Gson gson = new Gson();
      String json = gson.toJson(field);
      jsonList.add(json);
    }

    // Write Json list to memory-map.js.
    response.getWriter().println(jsonList);
  }

  /* Parses the address the user entered by removing a leading 0x, h, underscores, and spaces, so
   * that later in the doPost when there's a check for it being a valid hex it won't mistakenly
   * break because of these scenarios.*/
  static String addressParser(String originalAddress) {
    // Convert the address to be all lowercase.
    String formattedAddress = originalAddress.toLowerCase();

    // Check if the address starts with any leading zeroes followed by an x, and remove everything
    // up to and including the x from the address.
    if (formattedAddress.contains("x")) {
      int x = formattedAddress.indexOf("x");
      formattedAddress = formattedAddress.substring(x + 1);
    }

    // Check if the address contains an h, and remove it.
    if (formattedAddress.contains("h")) {
      formattedAddress = formattedAddress.replaceAll("h", "");
    }

    // Check if the address contains any underscores, and remove them.
    if (formattedAddress.contains("_")) {
      formattedAddress = formattedAddress.replaceAll("_", "");
    }

    // Check if the address contains any spaces, and remove them.
    if (formattedAddress.contains(" ")) {
      formattedAddress = formattedAddress.replaceAll(" ", "");
    }

    return formattedAddress;
  }
}