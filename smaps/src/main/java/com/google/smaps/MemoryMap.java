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
 * Retrieves histogram information and formats it into a Json array for creating the chart
 * and dashboard tools in memory-map.js.
 */
@WebServlet(name = "MemoryMap", value = "/memorymap")
public class Histogram extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // TODO(@sophboh22): add switch options for sorting the data based on permissions, size, huge
    // pages, etc.
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Response will be a Json.
    response.setContentType("application/json");

    // Parse the file from the user upload.
    List<Region> regionList = FileParser.getRegionList("/tmp/smaps-upload.txt");

    // Parse column chart data.
    List<Object[]> colChartData = makeDataArray(regionList);
  }
}