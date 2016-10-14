package com.xyz.services;
/**
 * Some service AA
 * @author Himanshu.Aseeja
 *
 */
public class ServiceAA {

	public String returnSomething(final String inputString) {
		StringBuilder strToReturn = new StringBuilder(inputString).append("ServiceAA..");
		return strToReturn.toString();
	}
}
