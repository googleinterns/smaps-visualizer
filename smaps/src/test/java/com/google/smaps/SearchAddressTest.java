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
import com.google.common.collect.ImmutableRangeMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
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

    // Make the regions list and range map.
    regions = Analyzer.makeRegionList("../smaps-full.txt");
    addressRangeMap = Analyzer.makeRangeMap(regions);
  }

  @After
  public void tearDown() {
    // Tears down the Servlet after tests are done.
    helper.tearDown();
  }

  // TODO(@sophbohr22): Look into how to implement JUnit tests with sessions.

  @Test
  public void addressParserFunctionality() throws Exception {
    // Tests that addressParser returns the proper address when given a hex address
    // with capital letters, 0x, h, underscores, and/or spaces.
    String parsedString = SearchAddress.addressParser("0x7FfE_A689 4000h");
    assertEquals("7ffea6894000", parsedString);
  }
}