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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import java.util.Scanner;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/** Servlet fires up homepage. */
@WebServlet(name = "Homepage", value = "/home")
@MultipartConfig
public class Homepage extends HttpServlet {
  public boolean fileSubmitted = false;
  public String fileName = "";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Properties properties = System.getProperties();

    response.setContentType("text/plain");

    if (this.fileSubmitted) {
      File dump = new File(
          "/usr/local/google/home/sophbohr/SmapsProject/smaps-visualizer/smaps/src/main/java/com/google/smaps/uploaded-dumps/"
          + this.fileName);

      Scanner sc = new Scanner(dump);
      while (sc.hasNextLine()) {
        response.getOutputStream().println(sc.nextLine());
      }

    } else {
      response.getWriter().println("No file uploaded yet...");
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // retrieve file part submitted by user
    Part filePart = request.getPart("fileToUpload");
    // String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
    // gets the input stream to store the file
    InputStream fileInputStream = filePart.getInputStream();

    // for now, copy the file to the server
    File fileToSave = new File(
        "/usr/local/google/home/sophbohr/SmapsProject/smaps-visualizer/smaps/src/main/java/com/google/smaps/uploaded-dumps/"
        + filePart.getSubmittedFileName());
    Files.copy(fileInputStream, fileToSave.toPath(), StandardCopyOption.REPLACE_EXISTING);

    /*
    // get the URL of the uploaded file
    String fileUrl = "http://localhost:8080/smaps/src/main/java/com/google/smaps/uploaded-dumps/"
        + filePart.getSubmittedFileName();
    */

    this.fileSubmitted = true;
    this.fileName = filePart.getSubmittedFileName();

    Scanner sc = new Scanner(fileToSave);
    while (sc.hasNextLine()) {
      response.getOutputStream().println(sc.nextLine());
    }

    /*
    // create output HTML that uses the
    response.getOutputStream().println("<p>Here's a link to your uploaded file:</p>");
    response.getOutputStream().println("<p><a href=\"" + fileUrl + "\">" + fileUrl + "</a></p>");
    */
  }

  /** Returns project description. */
  public static String getProjInfo() {
    return "This application takes a process' smap dump and creates useful charts/visualizations from it.";
  }
}
