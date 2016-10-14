package com.xyz.thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.xyz.services.ServiceA;
import com.xyz.services.ServiceAA;
import com.xyz.services.ServiceB;
import com.xyz.services.ServiceC;

/**
 * Main class -- entry point.
 *
 */
public class App {
	
	
	private static ServiceA serviceA;
	
	private static ServiceAA serviceAA;
	
	private static ServiceB serviceB;
	
	private static ServiceC serviceC;
	
	public static void setServiceA(ServiceA serviceA) {
		App.serviceA = serviceA;
	}
	
	public static void setServiceAA(ServiceAA serviceAA) {
		App.serviceAA = serviceAA;
	}
	
	public static void setServiceB(ServiceB serviceB) {
		App.serviceB = serviceB;
	}
	public static void setServiceC(ServiceC serviceC) {
		App.serviceC = serviceC;
	}
	
	private static String actualOutcome; 
	private static ExecutorService executorService;

	public static String getActualOutCome() {
		return actualOutcome;
	}
	public static void setExecutorService(ExecutorService customService) {
		App.executorService = customService;
	}

	public static void main(String[] args) {
		if (executorService == null) {
			executorService = Executors.newFixedThreadPool(2);
		}
    	
		//Step 1 which includes call to A and then call to AA
    	final CompletableFuture<String> futureFromAandAA = 
    		    CompletableFuture.supplyAsync(App::callServiceA, executorService).thenApply(App::callServiceAA);
    	
    	futureFromAandAA.exceptionally(th -> {
    	    actualOutcome = "Exception in A or AA..";
    	    return actualOutcome;
    	});
    	
    	//Step 2 which contains call to B
    	final CompletableFuture<String> futureFromB = 
    		    CompletableFuture.supplyAsync(App::callServiceB, executorService);
    	
    	futureFromB.exceptionally(th -> {
    	    actualOutcome = "Exception in B..";
    	    return actualOutcome;
    	});
    	
    	//Step 3 which contains call to C.. This should continue if both the above steps have completed non-exceptionally
    	if(!(futureFromAandAA.isCompletedExceptionally() || futureFromB.isCompletedExceptionally())) {
    		CompletableFuture<String> futureFromC = 
        			futureFromAandAA.thenCombine(futureFromB, (fromAA, fromB) -> callServiceC(fromAA, fromB));
        	
        	futureFromC.exceptionally(th -> {
        	    actualOutcome = "Exception in C..";
        	    return actualOutcome;
        	});
        	try {
				actualOutcome = futureFromC.get();
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	executorService.shutdown();
    	
    	System.out.println(actualOutcome);

	}

	private static String callServiceC(final String fromAA, final String fromB) {
		if(serviceC == null) {
			serviceC = new ServiceC();// or some web service
		}
		
		String outputFromC = serviceC.returnFinalOutcome(fromAA, fromB);
		return outputFromC;
	}
	private static String callServiceA() {
		if(serviceA == null) {
			serviceA = new ServiceA();
		}
		return serviceA.returnSomething();
	}
	private static String callServiceAA(final String fromA) {
		if(serviceAA == null) {
			serviceAA = new ServiceAA();
		}
		String outputFromAA = serviceAA.returnSomething(fromA);
		return outputFromAA;
	}
	private static String callServiceB() {
		//ServiceB serB = new ServiceB();//or some web service
		if(serviceB == null) {
			serviceB = new ServiceB();
		}
		return serviceB.returnSomethingElse();
	}

}
