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

@WebServlet("/histogram")
public class Histogram extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");

    List<Region> regionList = FileParser.getRegionList("../smaps-small.txt");
    // Hashtable<Long, Long> regionSizesHash = makeHashtable(regionList);
    Object[][] dataArray = makeDataArray(regionList);

    Gson gson = new Gson();
    String histogramJson = gson.toJson(dataArray);

    response.getWriter().println(histogramJson);
  }

  static Hashtable<Long, Long> makeHashtable(List<Region> regionList) {
    List<Long> keys = new ArrayList<Long>();
    Hashtable<Long, Long> regionSizesHash = new Hashtable<Long, Long>();
    for (int i = 0; i < regionList.size(); i++) {
      Long key = regionList.get(i).size();
      if (!keys.contains(key)) {
        keys.add(key);
        Long init = new Long(1);
        regionSizesHash.put(key, init);
      } else {
        Long currCount = regionSizesHash.get(key);
        Long newCount = currCount + 1;
        regionSizesHash.put(key, newCount);

        System.out.println("KEY: " + key + " VAL: " + newCount);
      }
    }
    return regionSizesHash;
  }

  static Object[][] makeDataArray(List<Region> regions) {
    // int numKeys = regionSizesHash.size();
    // Enumeration<Long> keys = regionSizesHash.keys();
    Object[][] dataArray = new Object[regions.size() + 1][2];
    dataArray[0][0] = "Name";
    dataArray[0][1] = "Size";

    for (int i = 0; i < regions.size(); i++) {
      // Object key = (Object) keys.nextElement();
      Region curR = regions.get(i);
      String range = curR.startLoc() + " - " + curR.endLoc();
      // Object name = (Object) "Region Name";
      Object val = (Object) curR.size();
      Object[] keyValPair = {range, val};
      dataArray[i + 1] = keyValPair;
    }

    for (int i = 0; i < dataArray.length; i++) {
      System.out.println(Arrays.toString(dataArray[i]));
    }

    return dataArray;
  }
}