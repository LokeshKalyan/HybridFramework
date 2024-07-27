/****************************************************************************
 * File Name 		: CucumberTestRunner.java
 * Package			: com.dxc.zurich.cucumber.runner
 * Author			: pmusunuru2
 * Creation Date	: Jul 11, 2022
 * Project			: Zurich Automation - UKLife
 * CopyRight		: DXC
 * Description		: 
 * ****************************************************************************
 * | Mod Date	| Mod Desc									| Mod Ticket Id   |
 * ****************************************************************************
 * |			|											|				  |
 * | 			|		*									|	*			  |
 * ***************************************************************************/
package com.abc.project.cucumber.runner;


import org.junit.runner.RunWith;


import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

/**
 * @author pmusunuru2
 * @since Jul 11, 2022 10:59:03 am
 */

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/main/java/com/dxc/zurich/cucumber/feature", 
glue = {"com.dxc.zurich.cucumber.base" },
monochrome = true, 
tags = "@smoke"
)


public class CucumberTestRunner{
	
	
}
