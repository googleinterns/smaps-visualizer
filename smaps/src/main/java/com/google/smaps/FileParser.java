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
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * FIXME: add javadoc
 */
@AutoValue
abstract class FileParser {
  static List<Region> getRegionList(File dump) {
    List<Region> regions = new ArrayList<Region>();

    try {
      Scanner sc = new Scanner(dump);
      boolean nextRegion = true;
      Region.Builder region = Region.builder();

      while (sc.hasNextLine()) {
        String line = sc.nextLine();

        if (nextRegion) {
          // this is the first line of a new region

          // make a new region builder for every region FIXME:
          region = Region.builder();

          // split the first line up at every space
          String[] attributes = line.split(" ");

          // split the address range by the hyphen
          String[] addressBounds = attributes[0].split("-");
          String startLoc = addressBounds[0];
          String endLoc = addressBounds[1];

          String permissions = attributes[1];

          region.setStartLoc(startLoc);
          region.setEndLoc(endLoc);
          region.setPermissions(permissions);

          nextRegion = false;
        } else {
          // this is the last line in the region, vmflags
          if (line.contains("VmFlags")) {
            Region r = region.build();
            regions.add(r);

            System.out.println("=================================");
            System.out.println("Size: " + r.size());
            System.out.println("Kernel Size: " + r.kernelPageSize());
            System.out.println("MMU: " + r.mmuPageSize());
            System.out.println("Rss: " + r.rss());

            nextRegion = true;
          } else {
            // remove all spaces in the line
            String fieldLine = line.replaceAll("\\s", "");
            // get the name of the field
            int colonIndex = fieldLine.indexOf(':');
            String field = fieldLine.substring(0, colonIndex);
            // remove all non-digit chars from the rest of the string,
            String restOfLine = fieldLine.substring(colonIndex);
            String valueStr = restOfLine.replaceAll("\\D", "");
            long value = Long.parseLong(valueStr);

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
                /*
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
                */
              default:
                System.out.println("NOT BEING ADDED - " + field);
            }
          }
        }
      }

      // ignore the ones we don't know, as default

      // keep going until you see vmflags, then the next line is the new region

      sc.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    return regions;
  }
}
