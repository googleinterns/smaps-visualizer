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

import com.google.auto.value.AutoValue;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.IllegalArgumentException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Takes in an smap dump text file and produces a list of the regions of that file.
 */
@AutoValue
abstract class FileParser {
  static List<Region> getRegionList(String filePathName) {
    List<Region> regions = new ArrayList<Region>(); // holds all regions of smaps dump

    try {
      // Create the file.
      File dump = new File(filePathName);

      // Ensure file is a text file.
      String fileName = dump.getName();
      String fileExt = fileName.substring(fileName.length() - 4);
      if (!fileExt.equals(".txt")) {
        // TODO(sophbohr22): implement logging to print exception to user.
        throw new IllegalArgumentException();
      }

      Scanner sc = new Scanner(dump);
      Region.Builder region = Region.builder();

      // Indicates whether a new region is being evaluated at each iteration.
      boolean nextRegion = true;

      while (sc.hasNextLine()) {
        String line = sc.nextLine();
        if (nextRegion) {
          // Make a new region builder for every region.
          region = Region.builder();

          // Remove extra space between inode and pathname, replace with one space.
          String spaceLine = line.replaceAll("\\s+", " ");

          // Place all fields of first line into array.
          String[] attributes = spaceLine.split(" ");

          // Split the address range by the hyphen.
          String[] addressBounds = attributes[0].split("-");
          String startLoc = addressBounds[0];
          String endLoc = addressBounds[1];

          // Define all other fields.
          String permissions = attributes[1];
          String offset = attributes[2];
          String device = attributes[3];
          long inode = Long.parseLong(attributes[4]);

          // If pathname exists, add it and any other details.
          String pathname = "";
          if (attributes.length > 5) {
            pathname += attributes[5];
            for (int i = 6; i < attributes.length; i++) {
              pathname += " " + attributes[i];
            }
          }

          // Set fields.
          region.setStartLoc(startLoc);
          region.setEndLoc(endLoc);
          region.setPermissions(permissions);
          region.setOffset(offset);
          region.setDevice(device);
          region.setInode(inode);
          region.setPathname(pathname);

          // No longer a new region for the next iteration.
          nextRegion = false;
        } else {
          if (line.contains("VmFlags")) {
            // Get just the flags.
            String flagsLine = line.substring(9);
            String[] flagsArray = flagsLine.split(" ");
            List<String> flags = Arrays.asList(flagsArray);
            region.setVmFlags(flags);

            // This is the last line in region, so build the region and add to regions list.
            Region r = region.build();
            regions.add(r);
            nextRegion = true;
          } else {
            // Get the name of the field.
            String fieldLine = line.replaceAll("\\s", "");
            int colonIndex = fieldLine.indexOf(':');
            String field = fieldLine.substring(0, colonIndex);

            // Remove all non-digit chars from the rest of the string to get the value.
            String restOfLine = fieldLine.substring(colonIndex);
            String valueStr = restOfLine.replaceAll("\\D", "");
            long value = Long.parseLong(valueStr);

            // Set the value to the field.
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
          }
        }
      }
      sc.close();
    } catch (IllegalArgumentException e) {
      // TODO(sophbohr22): implement logging to print exception to user.
      System.out.println("Incorrect file type, must be .txt.");
    } catch (FileNotFoundException e) {
      // TODO(sophbohr22): implement logging to print exception to user.
      System.out.println("File not found.");
    }
    return regions;
  }
}
