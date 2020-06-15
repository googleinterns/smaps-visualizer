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

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for {@link Histogram}.
 */
@RunWith(JUnit4.class)
public class HistogramTest {
  private static final String FAKE_URL = "fake.fk/histogram";
  private final LocalServiceTestHelper helper = new LocalServiceTestHelper();

  @Mock private HttpServletRequest mockRequest;
  @Mock private HttpServletResponse mockResponse;
  private StringWriter responseWriter;
  private Histogram servletUnderTest;

  @Before
  public void setUp() throws Exception {
    // Sets up the servlet and mock request and response.
    MockitoAnnotations.initMocks(this);
    helper.setUp();

    //  Sets up some fake HTTP requests
    when(mockRequest.getRequestURI()).thenReturn(FAKE_URL);

    // Sets up a fake HTTP response.
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));

    servletUnderTest = new Histogram();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void doGetWritesJson() throws Exception {
    // Tests that the response is named correctly and holds at least the labels in a 2D array for
    // the Json.
    servletUnderTest.doGet(mockRequest, mockResponse);
    assertThat(responseWriter.toString())
        .named("Histogram response")
        .contains("[[\"Range\",\"Size\"]");
  }

  @Test
  public void dataArray() {
    // Tests creation of data array for histogram from regions list.
    String filePathname = "../smaps-full.txt";
    List<Region> regions = FileParser.getRegionList(filePathname);
    Object[][] dataArray = servletUnderTest.makeDataArray(regions);

    // Checks labels.
    assertEquals("Range", dataArray[0][0]);
    assertEquals("Size", dataArray[0][1]);

    // Checks first entry.
    assertEquals("16ec0000000 - 16efa600000", dataArray[1][0]);
    assertEquals(956416L, dataArray[1][1]);

    // Checks last entry.
    assertEquals("ffffffffff600000 - ffffffffff601000", dataArray[1072][0]);
    assertEquals(4L, dataArray[1072][1]);
  }
}