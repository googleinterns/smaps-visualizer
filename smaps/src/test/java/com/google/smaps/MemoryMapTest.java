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
 * Unit tests for {@link MemoryMap}.
 */
@RunWith(JUnit4.class)
public class MemoryMapTest {
  private static final String FAKE_URL = "fake.fk/memorymap";
  private final LocalServiceTestHelper helper = new LocalServiceTestHelper();

  @Mock private HttpServletRequest mockRequest;
  @Mock private HttpServletResponse mockResponse;
  private StringWriter responseWriter;
  private List<Region> regions;
  private MemoryMap servletUnderTest;
  private HttpSession session;

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

    // Creates MemoryMap servlet.
    servletUnderTest = new MemoryMap();

    // Creates a session.
    session = mock(HttpSession.class);

    // Create the regions list.
    regions = Analyzer.makeRegionList("../smaps-small.txt", session);
  }

  @After
  public void tearDown() {
    // Tears down the Servlet after tests are done.
    helper.tearDown();
  }

  @Test
  public void doGet_writesResponse() throws Exception {
    // Tests that the JSON response is what is expected with smaps-small as the file.

    // Sets up the fake session for the request.
    when(mockRequest.getSession()).thenReturn(session);

    // Set the session variable for regionList that would normally be set by doPost in FileUpload.
    when(session.getAttribute("regionList")).thenReturn(regions);

    // Call doGet with the mockRequest and mockResponse.
    servletUnderTest.doGet(mockRequest, mockResponse);

    // Assert that the JSON response is what is expected for the smaps-small file, and contains an
    // array of every region with its address range and permissions.
    assertThat(responseWriter.toString())
        .named("MemoryMap response")
        .contains("[[\"0000016ec0000000 - 0000016efa600000\",\"---p\"],"
            + "[\"0000016efa600000 - 0000016f00000000\",\"rw-p\"],"
            + "[\"0000142580000000 - 00001425a8000000\",\"---p\"],"
            + "[\"00001425a8000000 - 00001425c0000000\",\"rw-p\"],"
            + "[\"00001425c0000000 - 00001425ce800000\",\"---p\"],"
            + "[\"00001425ce800000 - 0000142ac6000000\",\"rw-p\"]]");
  }

  @Test
  public void dataArray() {
    // Tests creation of list of Object arrays for histogram from regions list.
    List<Object[]> dataArray = servletUnderTest.makeDataArray(regions);

    // Checks first entry.
    assertEquals("0000016ec0000000 - 0000016efa600000", dataArray.get(0)[0]);
    assertEquals("---p", dataArray.get(0)[1]);

    // Checks last entry.
    assertEquals("00001425ce800000 - 0000142ac6000000", dataArray.get(5)[0]);
    assertEquals("rw-p", dataArray.get(5)[1]);
  }

  @Test
  public void formatAddress() {
    // Tests that the formatAddress function correctly appends zeroes to the beginning of an address
    // until the address is 16 bits.
    String address;
    String formattedAddress;

    // Address that needs zeroes appended to be 16 bits.
    address = "5f";
    formattedAddress = servletUnderTest.formatAddress(address);
    assertEquals("000000000000005f", formattedAddress);

    // Address that is already 16 bits.
    address = "ffffffffffd56730";
    formattedAddress = servletUnderTest.formatAddress(address);
    assertEquals("ffffffffffd56730", formattedAddress);
  }
}