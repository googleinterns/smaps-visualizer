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
import java.io.FileNotFoundException;
import java.lang.IllegalArgumentException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import javax.servlet.http.HttpSession;

/**
 * Takes in an smaps dump text file and produces a list of the {@link Region} objects of that file.
 *
 * TODO(sophbohr22): implement data integrity check for all fields.
 */
class FileParser {
  static List<Region> parseRegionList(String filePathname, HttpSession session) {
    try {
      List<Region> regions = parseFile(filePathname, session);
      return regions;
    } catch (FileNotFoundException e) {
      session.setAttribute("fileErrorMessage", "File not found.");
      System.out.println("File not found.");
    } catch (IllegalArgumentException e) {
      System.out.println("File has improper formatting.");
    }
    // If an exception was caught, return null.
    return null;
  }

  /* Parses the smaps file and returns a list of regions.*/
  static List<Region> parseFile(String filePathname, HttpSession session)
      throws FileNotFoundException, IllegalArgumentException {
    // Holds all regions of smaps dump.
    List<Region> regions = new ArrayList<Region>();
    // Create the file.
    File dump = new File(filePathname);
    Scanner sc = new Scanner(dump);
    Region.Builder region = Region.builder();

    // TODO(@sophbohr22): Add a defense to the parser so that files with extremely large region
    // counts aren't allowed.

    // Indicates whether there is a previous region to be built (will only be false for the first
    // region).
    boolean prevRegion = false;

    // Initialize lineNumber, and loop through the file while there's still a new line.
    int lineNumber = 0;
    while (sc.hasNextLine()) {
      lineNumber++;
      String line = sc.nextLine();
      if (checkNewRegion(line)) {
        // Build the previous region (if there is one) and add it to the list.
        if (prevRegion) {
          // Ensure that a valid size was set for this region.
          Region r = region.build();
          if (r.size() == -1) {
            session.setAttribute("fileErrorMessage",
                "One or more regions does not contain a 'size' field, see go/smaps-vis/smaps-example.txt to view properly formatted smaps file.");
            throw new IllegalArgumentException();
          }
          regions.add(r);
        }

        // Set prevRegion to be true because we're making a new region.
        prevRegion = true;

        // Make a new region builder for every region.
        region = Region.builder();

        // Remove extra space between inode and pathname, replace with one space.
        String spaceLine = line.replaceAll("\\s+", " ");

        // Place all fields of first line into array.
        String[] attributes = spaceLine.split(" ");

        if (attributes.length < 5) {
          session.setAttribute("fileErrorMessage",
              "First line for each region not formatted properly, see go/smaps-vis/smaps-example.txt to view properly formatted smaps file.");
          throw new IllegalArgumentException();
        }

        // Split the address range by the hyphen.
        // ex: 55d225800000-55d225820000
        String[] addressBounds = attributes[0].split("-");
        String startLoc = addressBounds[0];
        String endLoc = addressBounds[1];

        // ex: rw-s
        String permissions = attributes[1];
        // ex: 001a1000
        String offset = attributes[2];
        // ex: 08:01
        String device = attributes[3];
        // ex: 7700
        long inode = Long.parseLong(attributes[4]);

        // If pathname exists, add it and any other details.
        String pathname = "";
        if (attributes.length > 5) {
          pathname += attributes[5];
          for (int i = 6; i < attributes.length; i++) {
            // If there are extra details after pathname, append to string and add space between.
            // ex: /memfd:stan (deleted)
            pathname += " " + attributes[i];
          }
        }

        // Set fields.
        region.setLineNumber(lineNumber);
        region.setStartLoc(startLoc);
        region.setEndLoc(endLoc);
        region.setPermissions(permissions);
        region.setOffset(offset);
        region.setDevice(device);
        region.setInode(inode);
        region.setPathname(pathname);

      } else if (line.contains("VmFlags")) { // VmFlags is a special case, so is parsed differently.
        // ex: rd ex mr mw me lo sd
        String flagsLine = line.substring(9);
        String[] flagsArray = flagsLine.split(" ");
        List<String> flags = Arrays.asList(flagsArray);
        region.setVmFlags(flags);

      } else { // This isn't the last line of the region, so continue normally.
        // Get the name of the field.
        String fieldLine = line.replaceAll("\\s", "");
        int colonIndex = fieldLine.indexOf(':');
        String field = fieldLine.substring(0, colonIndex);

        // Remove all non-digit chars from the rest of the string to get the value.
        String restOfLine = fieldLine.substring(colonIndex);
        String valueStr = restOfLine.replaceAll("\\D", "");
        long value = Long.parseLong(valueStr);

        // Set the value to the field.
        fillField(field, value, region);
      }
    }

    // Build the final region.
    Region r = region.build();
    regions.add(r);

    // Close the scanner and return the list.
    sc.close();
    return regions;
  }

  /* Checks whether the line that is being passed is the first line in a new region, with the format
   * having at least the address range. This address range being present in a line is the indication
   * of a new region. */
  static boolean checkNewRegion(String line) {
    // Check the beginning of line matches this format, for example: 16ec0000000-16efa600000
    if (line.matches("^[0-9a-fA-F]+[-][0-9a-fA-F]+.*")) {
      return true;
    }
    return false;
  }

  static void fillField(String field, long value, Region.Builder region) {
    switch (field) {
      case "Size":
        region.setSize(value);
        break;
      case "KernelPageSize":
        region.setKernelPageSize(value);
        break;
      case "MMUPageSize":
        region.setMmuPageSize(value);
        break;
      case "Rss":
        region.setRss(value);
        break;
      case "Pss":
        region.setPss(value);
        break;
      case "Shared_Clean":
        region.setSharedClean(value);
        break;
      case "Shared_Dirty":
        region.setSharedDirty(value);
        break;
      case "Private_Clean":
        region.setPrivateClean(value);
        break;
      case "Private_Dirty":
        region.setPrivateDirty(value);
        break;
      case "Referenced":
        region.setReferenced(value);
        break;
      case "Anonymous":
        region.setAnonymous(value);
        break;
      case "LazyFree":
        region.setLazyFree(value);
        break;
      case "AnonHugePages":
        region.setAnonHugePages(value);
        break;
      case "ShmemHugePages":
        region.setShmemHugePages(value);
        break;
      case "ShmemPmdMapped":
        region.setShmemPmdMapped(value);
        break;
      case "Shared_Hugetlb":
        region.setSharedHugetlb(value);
        break;
      case "Private_Hugetlb":
        region.setPrivateHugetlb(value);
        break;
      case "HugePFNMap":
        region.setHugePFNMap(value);
        break;
      case "Swap":
        region.setSwap(value);
        break;
      case "SwapPss":
        region.setSwapPss(value);
        break;
      case "Locked":
        region.setLocked(value);
        break;
      default:
        // TODO(sophbohr22): implement logging to identify unknown fields to user.
    }
    return;
  }
}
