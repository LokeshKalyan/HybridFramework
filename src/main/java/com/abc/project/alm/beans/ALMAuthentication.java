/****************************************************************************
 * File Name 		: ALMAuthentication.java
 * Package			: com.dxc.zurich.alm.beans
 * Author			: M.BODDU
 * Creation Date	: Jun 09, 2023
 * Project			: Zurich Automation - UKLife
 * CopyRight		: DXC
 * Description		: 
 * ****************************************************************************
 * | Mod Date	| Mod Desc									| Mod Ticket Id   |
 * ****************************************************************************
 * |			|											|				  |
 * | 			|		*									|	*			  |
 * ***************************************************************************/
package com.abc.project.alm.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author M.BODDU
 * @since Jun 09, 2023 9:41:39 AM
 */
@XmlRootElement(name = "alm-authentication")
@XmlAccessorType(XmlAccessType.FIELD)
public class ALMAuthentication {

	@XmlElement(name = "user", required = true)
	private String user;
	
	@XmlElement(name = "password", required = true)
	private String password;

	/**
	 * @return the user
	 */
	public String user() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void user(String user) {
		this.user = user;
	}

	/**
	 * @return the password
	 */
	public String password() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void password(String password) {
		this.password = password;
	}
	
	
}
