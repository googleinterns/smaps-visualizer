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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.apache.commons.io.FileUtils;

/**
 * Retrieves the uploaded file from the user and then saves it to the tmp directory.
 */
@MultipartConfig
@WebServlet(name = "FileUpload", value = "/fileupload")
public class FileUpload extends HttpServlet {
  // This String is sent back to index.html with a message if there's an error with file upload.
  String errorMessage = "";

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    // Clears the tmp directory of any prior upload.
    File tmpDirectory = new File("./tmp");
    FileUtils.cleanDirectory(tmpDirectory);
    // Resets the error message.
    errorMessage = "";

    // Gets the file chosen by the user.
    Part filePart = request.getPart("smapsFile");

    // TODO(@sophbohr): add more error cases with printouts.
    // Sets error because file was either not selected or is empty.
    if (filePart.getSize() == 0) {
      errorMessage = "Error: No file chosen or file was empty.";
      // Send user back to the index.html page with error message printed with doGet.
      response.sendRedirect("/index.html");
    } else {
      // Gets the InputStream to eventually store the file in tmp directory.
      InputStream fileInputStream = filePart.getInputStream();

      // Copies the uploaded file to the server and rename it smaps-upload.txt.
      File fileToSave = new File("./tmp/smaps-upload.txt");
      Files.copy(fileInputStream, fileToSave.toPath(), StandardCopyOption.REPLACE_EXISTING);

      // Resets the postFired flag in Histogram.java so that the slider and textboxes will start
      // with the min/max values of this file and not with any previously chosen bounds.
      Histogram.setNewUpload();

      // Send user to the histogram page.
      response.sendRedirect("/interactive-histogram.html");
    }
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    // Transfer the String into JavaScript Object (Json).
    Gson gson = new Gson();
    String errorJson = gson.toJson(errorMessage);
    // Write Json to index.js.
    response.getWriter().println(errorJson);
  }
}