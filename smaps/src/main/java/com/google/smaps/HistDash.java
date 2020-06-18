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
import java.util.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet fires up homepage. */
@WebServlet(name = "HistDash", value = "/histdash")
public class HistDash extends HttpServlet {
  Long lowerBound = 4L;
  Long upperBound = 30000000L;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    Long[] bounds = {lowerBound, upperBound};
    List<String> boundsJsonList = new ArrayList<String>();
    for (Long b : bounds) {
      Gson gson = new Gson();
      String boundJson = gson.toJson(b);
      boundsJsonList.add(boundJson);
    }

    response.getWriter().println(boundsJsonList);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String lower = request.getParameter("lower-bound");
    String upper = request.getParameter("upper-bound");

    lowerBound = Long.parseLong(lower);
    upperBound = Long.parseLong(upper);

    response.sendRedirect("/interactive-histogram.html");
  }
}