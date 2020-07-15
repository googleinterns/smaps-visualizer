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
import java.math.BigInteger;
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
 * Unit tests for {@link SearchAddress}.
 */
@RunWith(JUnit4.class)
public class SearchAddressTest {
  private static final String FAKE_URL = "fake.fk/searchaddress";
  private final LocalServiceTestHelper helper = new LocalServiceTestHelper();

  @Mock private HttpServletRequest mockRequest;
  @Mock private HttpServletResponse mockResponse;
  private StringWriter responseWriter;
  private SearchAddress servletUnderTest;
  private List<Region> regions;

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

    // Create an instance of t
    servletUnderTest = new SearchAddress();

    // Make the regions list and range map.
    Analyzer.makeRegionList("../smaps-full.txt");
    regions = Analyzer.getRegionList();
    Analyzer.makeRangeMap(regions);
  }

  @Test
  public void findRegionSuccess() throws Exception {
    // Create a valid address for this memory map.
    String address = "16ec0000009";

    // The region for this address is the first in the regions list.
    Region expectedR = regions.get(0);

    // Set the fields in the SearchAddress class (which would normally be set by doPost).
    SearchAddress.address = address;
    SearchAddress.addressBigInt = new BigInteger(address, 16);

    // Use the findRegion() method to retrieve the region based on this address.
    Region r = servletUnderTest.findRegion();

    // Ensure the returned region is the same as the expected one.
    assertEquals(expectedR, r);

    // Test that the doGet function returns the Json list containing the address, the index
    // in the list, and the error message, which in this case should be null.
    servletUnderTest.doGet(mockRequest, mockResponse);
    assertThat(responseWriter.toString())
        .named("SearchAddress response")
        .contains("[\"16ec0000009\", 0, null]");
  }

  @Test
  public void findRegionFailure() throws Exception {
    // Create an address that isn't present in this memory map.
    String address = "ff6789";

    // Set the fields in the SearchAddress class (which would normally be set by doPost).
    SearchAddress.address = address;
    SearchAddress.addressBigInt = new BigInteger(address, 16);

    // Use the findRegion() method to retrieve the region based on this address.
    Region r = servletUnderTest.findRegion();

    // Ensure the returned region is null, because there isn't a region for this address.
    assertNull(r);

    // Test that the doGet function returns the Json list containing the address, -1 as the index,
    // and the proper error message.
    servletUnderTest.doGet(mockRequest, mockResponse);
    assertThat(responseWriter.toString())
        .named("SearchAddress response")
        .contains("[\"ff6789\", -1, \"Address ff6789 is not present in this memory map.\"]");
  }

  @After
  public void tearDown() {
    // Tears down the Servlet after tests are done.
    helper.tearDown();
  }
}