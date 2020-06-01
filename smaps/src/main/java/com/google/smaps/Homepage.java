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
import java.io.IOException;
import java.util.Properties;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet fires up homepage. */
@WebServlet(name = "Homepage", value = "/home")
public class Homepage extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Properties properties = System.getProperties();

    response.setContentType("text/plain");
    response.getWriter().println("Info - Standard using Java "
        + properties.get("java.specification.version") + " on " + properties.get("os.name")
        + " with App Engine " + SystemProperty.version.get());
  }

  /** Returns project description. */
  public static String getProjInfo() {
    return "This application takes a process' smap dump and creates useful charts/visualizations from it.";
  }
}
