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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class FileParserTest {
  // private FileParser mockDumpParser;

  @Before
  public void setUp() throws Exception {
    File dump =
        new File("/usr/local/google/home/sophbohr/SmapsProject/smaps-visualizer/smaps-full.txt");
    List<Region> regions = FileParser.getRegionList(dump);
  }

  @Test
  public void FileParserTest() {
    String path1 = "I am sophie.";
    String path2 = "I am sophie.";
    assertEquals(path1, path2); // FIXME: make a real test here
  }
}