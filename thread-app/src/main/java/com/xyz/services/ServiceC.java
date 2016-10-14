package com.xyz.services;
/**
 * Some service C
 * @author Himanshu.Aseeja
 *
 */
public class ServiceC {

	public String returnFinalOutcome(final String inputFromA, final String inputFromB) {
		StringBuilder output = new StringBuilder(inputFromA).append(inputFromB).append("Service C..");
		return output.toString();
	}
}
