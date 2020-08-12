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
import com.google.common.collect.ImmutableRangeMap;
import com.google.gson.Gson;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
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
  private ImmutableRangeMap<BigInteger, Region> addressRangeMap;
  private HttpSession session;

  @Before
  public void setUp() throws Exception {
    // Sets up the servlet and mock request and response.
    MockitoAnnotations.initMocks(this);
    helper.setUp();

    //  Sets up some fake HTTP requests.
    when(mockRequest.getRequestURI()).thenReturn(FAKE_URL);

    // Sets up a fake HTTP response.
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));

    // Create an instance of the SearchAddress servlet.
    servletUnderTest = new SearchAddress();

    // Creates a session.
    session = mock(HttpSession.class);

    // Make the regions list and range map.
    regions = Analyzer.makeRegionList("../smaps-full.txt", session);
    addressRangeMap = Analyzer.makeRangeMap(regions);
  }

  @After
  public void tearDown() {
    // Tears down the Servlet after tests are done.
    helper.tearDown();
  }

  @Test
  public void doGet_writesResponseNoRegion() throws Exception {
    // Tests that the JSON response is what is expected with smaps-full as the file and a valid
    // hexadecimal address is searched for that is not in any of the regions.

    // Sets up the fake session for the request.
    when(mockRequest.getSession()).thenReturn(session);

    // Set the session variables for regionList that would normally be set by doPost.
    when(session.getAttribute("address")).thenReturn("77fd");
    when(session.getAttribute("addressErrorMessage")).thenReturn("");
    when(session.getAttribute("addressBigInt")).thenReturn(new BigInteger("77fd", 16));
    when(session.getAttribute("rangeMap")).thenReturn(addressRangeMap);
    when(session.getAttribute("regionList")).thenReturn(regions);
    when(session.getAttribute("originalAddress")).thenReturn("77fd");

    // Call doGet with the mockRequest and mockResponse.
    servletUnderTest.doGet(mockRequest, mockResponse);

    // Assert that the JSON response is what is expected, and contains the address searched for, -1
    // for the index, the error message for no region being found, and null for the region.
    assertThat(responseWriter.toString())
        .named("SearchAddress response")
        .contains("[\"77fd\", -1, \"No region in which address [77fd] can be found.\", null]");
  }

  @Test
  public void doGet_writesResponseInvalidHex() throws Exception {
    // Tests that the JSON response is what is expected with smaps-full as the file and an invalid
    // hexadecimal address is searched for.

    // Sets up the fake session for the request.
    when(mockRequest.getSession()).thenReturn(session);

    // Set the session variables for regionList that would normally be set by doPost.
    when(session.getAttribute("address")).thenReturn("not hex");
    when(session.getAttribute("addressErrorMessage"))
        .thenReturn("Address [not hex] is not a valid hexadecimal number.");
    when(session.getAttribute("addressBigInt")).thenReturn(null);
    when(session.getAttribute("regionList")).thenReturn(regions);
    when(session.getAttribute("originalAddress")).thenReturn("not hex");

    // Call doGet with the mockRequest and mockResponse.
    servletUnderTest.doGet(mockRequest, mockResponse);

    // Assert that the JSON response is what is expected, and contains the invalid address searched
    // for, -1 for the index, the error message for it not being a hex address, and null for the
    // region.
    assertThat(responseWriter.toString())
        .named("SearchAddress response")
        .contains(
            "[\"not hex\", -1, \"Address [not hex] is not a valid hexadecimal number.\", null]");
  }

  @Test
  public void doGet_writesResponseValidHex() throws Exception {
    // Tests that the JSON response is what is expected with smaps-full as the file and a valid
    // hexadecimal address is searched for.

    // Sets up the fake session for the request.
    when(mockRequest.getSession()).thenReturn(session);

    // Set the session variables for regionList that would normally be set by doPost.
    when(session.getAttribute("address")).thenReturn("16ec0000007");
    when(session.getAttribute("addressErrorMessage")).thenReturn("");
    when(session.getAttribute("addressBigInt")).thenReturn(new BigInteger("16ec0000007", 16));
    when(session.getAttribute("regionList")).thenReturn(regions);
    when(session.getAttribute("rangeMap")).thenReturn(addressRangeMap);
    when(session.getAttribute("originalAddress")).thenReturn("16ec0000007");

    // Call doGet with the mockRequest and mockResponse.
    servletUnderTest.doGet(mockRequest, mockResponse);

    // Get the region that should be returned and covert it to a json for checking later.
    Region r = regions.get(0);
    Gson gson = new Gson();
    String jsonR = gson.toJson(r);

    // Assert that the JSON response is what is expected, and contains the valid address searched
    // for, 0 for the index, no error message, and the proper region.
    assertThat(responseWriter.toString())
        .named("SearchAddress response")
        .contains("[\"16ec0000007\", 0, \"\", " + jsonR + "]");
  }

  @Test
  public void addressParser() throws Exception {
    // Tests that addressParser returns the proper address when given a hex address
    // with capital letters, 0x, h, underscores, and/or spaces.
    String parsedString = SearchAddress.addressParser("0x7FfE_A689 4000h");
    assertEquals("7ffea6894000", parsedString);
  }
}