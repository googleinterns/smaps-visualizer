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
import static org.mockito.Mockito.*;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
  private List<Region> regions;
  private Histogram servletUnderTest;
  private HttpSession session;

  @Before
  public void setUp() throws Exception {
    // Sets up the servlet and mock request and response.
    MockitoAnnotations.initMocks(this);
    helper.setUp();

    //  Sets up some fake HTTP request.
    when(mockRequest.getRequestURI()).thenReturn(FAKE_URL);

    // Sets up a fake HTTP response.
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));

    // Creates Histogram servlet.
    servletUnderTest = new Histogram();

    // Creates a session.
    session = mock(HttpSession.class);

    // Create the regions list.
    regions = Analyzer.makeRegionList("../smaps-full.txt", session);
  }

  @After
  public void tearDown() {
    // Tears down the Servlet after tests are done.
    helper.tearDown();
  }

  @Test
  public void test() {
    assertEquals("hi", "hi");
  }

  @Test
  public void doGet_writesResponseExtrema() throws Exception {
    // Tests that the JSON response is what is expected when the bounds are the preset extrema for
    // the histogram.

    // Sets up the fake session for the request.
    when(mockRequest.getSession()).thenReturn(session);

    // Set the session variables that would normally be set by doPost.
    when(session.getAttribute("regionList")).thenReturn(regions);
    when(session.getAttribute("postFired")).thenReturn(false);
    when(session.getAttribute("minBound")).thenReturn(4L);
    when(session.getAttribute("maxBound")).thenReturn(20832256L);
    when(session.getAttribute("name")).thenReturn("");

    // Call doGet with the mockRequest and mockResponse.
    servletUnderTest.doGet(mockRequest, mockResponse);

    // Assert that the JSON response is what is expected for the smaps-full file with postFired set
    // to false, so the entire range should be displayed and the labels for the data should be range
    // and size.
    assertThat(responseWriter.toString())
        .named("Histogram response")
        .contains("[[4,20832256], \"\", [[\"Range\",\"Size\"]");
  }

  @Test
  public void doGet_writesResponseCustomBounds() throws Exception {
    // Tests that the JSON response is what is expected when the bounds are customized for the
    // histogram.

    // Sets up the fake session for the request.
    when(mockRequest.getSession()).thenReturn(session);

    // Set the session variables that would normally be set by doPost.
    when(session.getAttribute("regionList")).thenReturn(regions);
    when(session.getAttribute("postFired")).thenReturn(true);
    when(session.getAttribute("lowerBound")).thenReturn(1000L);
    when(session.getAttribute("upperBound")).thenReturn(2000L);
    when(session.getAttribute("name")).thenReturn("memfd");

    // Call doGet with the mockRequest and mockResponse.
    servletUnderTest.doGet(mockRequest, mockResponse);

    // Assert that the JSON response is what is expected for the smaps-full file with postFired set
    // to true, so the range is set to the custom bounds, name is memfd, and the labels for the data
    // should be range and size.
    assertThat(responseWriter.toString())
        .named("Histogram response")
        .contains("[[1000,2000], \"memfd\", [[\"Range\",\"Size\"]");
  }

  @Test
  public void dataArrayEmpty() {
    // Tests creation of list of Object arrays for histogram from regions list when the path name
    // filter field is a string that is not found in any region.
    List<Object[]> dataArray = servletUnderTest.makeDataArray(regions, "not found");

    // Checks labels.
    assertEquals("Range", dataArray.get(0)[0]);
    assertEquals("Size", dataArray.get(0)[1]);

    // Checks first entry.
    assertEquals(null, dataArray.get(1)[0]);
    assertEquals(-1, dataArray.get(1)[1]);
  }

  @Test
  public void dataArraySomeData() {
    // Tests creation of list of Object arrays for histogram from regions list when the path name
    // filter is a string that is found in some regions.
    List<Object[]> dataArray = servletUnderTest.makeDataArray(regions, "memfd");

    // Checks labels.
    assertEquals("Range", dataArray.get(0)[0]);
    assertEquals("Size", dataArray.get(0)[1]);

    // Checks first entry.
    assertEquals("7fd126400000 - 7fd12a400000", dataArray.get(2)[0]);
    assertEquals(65536L, dataArray.get(2)[1]);

    // Checks last entry.
    assertEquals("7fd149292000 - 7fd149293000", dataArray.get(33)[0]);
    assertEquals(4L, dataArray.get(33)[1]);
  }

  @Test
  public void dataArrayFull() {
    // Tests creation of list of Object arrays for histogram from regions list when the path name
    // filter is not chosen, so all regions are included.
    List<Object[]> dataArray = servletUnderTest.makeDataArray(regions, "");

    // Checks labels.
    assertEquals("Range", dataArray.get(0)[0]);
    assertEquals("Size", dataArray.get(0)[1]);

    // Checks first entry.
    assertEquals("16ec0000000 - 16efa600000", dataArray.get(2)[0]);
    assertEquals(956416L, dataArray.get(2)[1]);

    // Checks last entry.
    assertEquals("ffffffffff600000 - ffffffffff601000", dataArray.get(1073)[0]);
    assertEquals(4L, dataArray.get(1073)[1]);
  }
}