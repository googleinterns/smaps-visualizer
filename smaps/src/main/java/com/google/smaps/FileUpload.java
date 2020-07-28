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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

/**
 * Retrieves the uploaded file from the user and then saves it to the tmp directory, while also
 * setting attributes to this user's session.
 */
@MultipartConfig
@WebServlet(name = "FileUpload", value = "/fileupload")
public class FileUpload extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    // Get the current session, which contains user-specific data.
    HttpSession session = request.getSession();

    // The name of this user's file upload, will later be set to a file path.
    String filename;

    // Reset the error message to be empty, it will later be set to a message if there's an error
    // with upload.
    String fileErrorMessage = "";
    session.setAttribute("fileErrorMessage", fileErrorMessage);

    // Check whether example mode was selected, if it wasn't selected the exampleButton will be
    // null.
    String exampleButton = request.getParameter("exampleMode");
    if (exampleButton != null) {
      // Create an input stream from the example smaps file.
      InputStream exampleInputStream = new FileInputStream(new File("WEB-INF/smaps-example.txt"));

      // Get the randomized filename from uploadFile and set the filename to the session.
      filename = uploadFile(session, exampleInputStream);
      session.setAttribute("filename", filename);

      // Send user to the histogram page.
      response.sendRedirect("/interactive-histogram.html");
      return;

    } else {
      // Get the file chosen by the user.
      Part filePart = request.getPart("smapsFile");

      // TODO(@sophbohr): Add more error checking.
      // Check if the file part is empty (meaning no file was selected or the file was empty).
      if (filePart.getSize() == 0) {
        // Set the error message.
        fileErrorMessage = "Error: No file chosen or file was empty.";
        session.setAttribute("fileErrorMessage", fileErrorMessage);
        // Send user back to the index.html page with error message printed with doGet.
        response.sendRedirect("/index.html");
        return;
      }

      // Create an input stream from the file the user uploaded.
      InputStream fileInputStream = filePart.getInputStream();

      // Get the randomized filename from uploadFile and set the filename to the session.
      filename = uploadFile(session, fileInputStream);
      session.setAttribute("filename", filename);

      // Send user to the histogram page.
      response.sendRedirect("/interactive-histogram.html");
      return;
    }
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    // Get the current session, which contains user-specific data.
    HttpSession session = request.getSession();

    // Get the error message currently in this session.
    String fileErrorMessage = (String) session.getAttribute("fileErrorMessage");

    // Transfer the String into JavaScript Object (Json).
    Gson gson = new Gson();
    String errorJson = gson.toJson(fileErrorMessage);

    // Write Json to index.js.
    response.getWriter().println(errorJson);
  }

  /**
   * Creates a new file from fileInputStream and saves it to the tmp directory with a random name,
   * also creates the necessary data structures and sets them to this session.
   */
  public String uploadFile(HttpSession session, InputStream fileInputStream) throws IOException {
    // Generate a random filename for this user's file.
    Random rand = new Random();
    int randomInt = rand.nextInt();
    String filename = "/tmp/smaps-upload-" + randomInt + ".txt";

    // Copy the uploaded file to the server and rename it to the generated filename.
    File fileToSave = new File(filename);
    Files.copy(fileInputStream, fileToSave.toPath(), StandardCopyOption.REPLACE_EXISTING);

    // Make the list of regions from this file that will be utilized for various
    // charts/visualizations, and set the list to the session.
    List<Region> regionList = Analyzer.makeRegionList(filename);
    session.setAttribute("regionList", regionList);

    // Make the range map that will allow for the address search feature on the memory map page, and
    // set the range map to the session.
    ImmutableRangeMap<BigInteger, Region> rangeMap = Analyzer.makeRangeMap(regionList);
    session.setAttribute("rangeMap", rangeMap);

    // Reset the fields in SearchAddress.java so that the textbox will start blank and the
    // class will not contain any information from previous searches.
    session.setAttribute("address", "");
    session.setAttribute("addressBigInt", null);
    session.setAttribute("addressErrorMessage", "");

    // Get the min and max region sizes in the list which will be utilized to set
    // and reset bounds in the histogram, and set them to the session.
    long[] extrema = Analyzer.getMinMax(regionList);
    session.setAttribute("minBound", extrema[0]);
    session.setAttribute("maxBound", extrema[1]);

    // Set the postFired flag in Histogram.java to be false since this is a new upload and textboxes
    // will start with the min/max values of this file and not with any previously chosen bounds.
    session.setAttribute("postFired", false);

    return filename;
  }
}