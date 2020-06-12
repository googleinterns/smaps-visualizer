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

import static org.junit.Assert.*;

import java.io.File;
import java.util.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Unit tests for {@link Histogram}.
 */
@RunWith(JUnit4.class)
public class HistogramTest {
  private List<Region> regions;

  @Before
  public void setUp() throws Exception {
    // Creates regions list from smaps-full.txt file.
    String filePathname = "../smaps-full.txt";
    regions = FileParser.getRegionList(filePathname);
  }

  @Test
  public void makeDataArray() {
    // TODO(@sophbohr22): implement this test
    Object[][] dataArray = Histogram.makeDataArray(regions);
  }
}