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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.apache.commons.io.FileUtils;

/**
 * Retrieves
 */
@WebServlet(name = "FileUpload", value = "/fileupload")
public class FileUpload extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    // Clears the temp directory of any prior upload.
    File tempDirectory = new File("./temp");
    FileUtils.cleanDirectory(tempDirectory);

    // Gets the file chosen by the user.
    Part filePart = request.getPart("smapsFile");

    // Gets the InputStream to eventually store the file in temp directory.
    InputStream fileInputStream = filePart.getInputStream();

    // Copy the uploaded file to the server.
    File fileToSave = new File("./temp/smaps-upload.txt");
    Files.copy(fileInputStream, fileToSave.toPath(), StandardCopyOption.REPLACE_EXISTING);

    // Resets the postFired flag in Histogram.java so that the slider and textboxes will start with
    // the min/max values of this file and not with any previously chosen bounds.
    Histogram.setNewUpload();

    // Send user to the histogram page.
    response.sendRedirect("/interactive-histogram.html");
  }
}