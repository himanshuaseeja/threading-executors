package com.xyz.thread;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.xyz.services.ServiceA;
import com.xyz.services.ServiceAA;
import com.xyz.services.ServiceB;
import com.xyz.services.ServiceC;

/**
 * Unit test for simple App.
 */

public class AppTest {

	@Mock
	private ServiceA serviceA;

	@Mock
	private ServiceAA serviceAA;

	@Mock
	private ServiceB serviceB;

	@Mock
	private ServiceC serviceC;

	@Before
	public void setUp() {
		//Initialize all mocks
		MockitoAnnotations.initMocks(this);
		
		//Mock thread factory to and create executor service for test case setup
		ThreadFactory mock = new CustomThreadFactory();
		ExecutorService executorService = Executors.newCachedThreadPool(mock);
		
		//inject all mocks in unit under test
		App.setExecutorService(executorService);
		App.setServiceA(serviceA);
		App.setServiceAA(serviceAA);
		App.setServiceB(serviceB);
		App.setServiceC(serviceC);
	}

	@Test
	public void testMain() {

		// Define stubs on all services
		when(serviceA.returnSomething()).thenReturn("Service A..");
		when(serviceAA.returnSomething("Service A..")).thenReturn("Service A..ServiceAA..");
		when(serviceB.returnSomethingElse()).thenReturn("ServiceB..");
		when(serviceC.returnFinalOutcome("Service A..ServiceAA..", "ServiceB.."))
				.thenReturn("Service A..ServiceAA..ServiceB..Service C..");

		// The actual method under test
		App.main(null);

		// The assertion on actual outcome
		assertEquals("Service A..ServiceAA..ServiceB..Service C..", App.getActualOutCome());

		// Verifications on service calls.
		verify(serviceA, times(1)).returnSomething();
		verify(serviceAA, times(1)).returnSomething("Service A..");
		verify(serviceB, times(1)).returnSomethingElse();
		verify(serviceC, times(1)).returnFinalOutcome("Service A..ServiceAA..", "ServiceB..");
	}

	@Test
	public void testMainForExceptionScenarioInA() {

		// Define stubs on all services
		when(serviceA.returnSomething()).thenThrow(new RuntimeException());
		when(serviceAA.returnSomething("Service A..")).thenReturn("Service A..ServiceAA..");
		when(serviceB.returnSomethingElse()).thenReturn("ServiceB..");
		when(serviceC.returnFinalOutcome("Service A..ServiceAA..", "ServiceB.."))
				.thenReturn("Service A..ServiceAA..ServiceB..Service C..");

		// The actual method under test
		App.main(null);

		//verification of calls on mocks
		verify(serviceA, times(1)).returnSomething();
	}

	@Test
	public void testMainForExceptionScenarioInAA() {

		// Define stubs on all services
		when(serviceA.returnSomething()).thenReturn("Service A..");
		when(serviceAA.returnSomething("Service A..")).thenThrow(new RuntimeException());
		when(serviceB.returnSomethingElse()).thenReturn("ServiceB..");
		when(serviceC.returnFinalOutcome("Service A..ServiceAA..", "ServiceB.."))
				.thenReturn("Service A..ServiceAA..ServiceB..Service C..");

		// The actual method under test
		App.main(null);
		
		//verification of calls on mocks
		verify(serviceA, times(1)).returnSomething();
		verify(serviceAA, times(1)).returnSomething("Service A..");

	}
	
	@Test
	public void testMainForExceptionScenarioInB() {

		// Define stubs on all services
		when(serviceA.returnSomething()).thenReturn("Service A..");
		when(serviceAA.returnSomething("Service A..")).thenReturn("Service A..ServiceAA..");
		when(serviceB.returnSomethingElse()).thenThrow(new RuntimeException());
		when(serviceC.returnFinalOutcome("Service A..ServiceAA..", "ServiceB.."))
				.thenReturn("Service A..ServiceAA..ServiceB..Service C..");

		// The actual method under test
		App.main(null);
		
		//verification of calls on mocks
		verify(serviceA, times(1)).returnSomething();
		verify(serviceAA, times(1)).returnSomething("Service A..");
		verify(serviceB, times(1)).returnSomethingElse();

	}

	@After
	public void tearDown() {

	}

	class CustomThreadFactory implements ThreadFactory {
		public Thread newThread(Runnable r) {
			return new Thread(r);
		}
	}
}
