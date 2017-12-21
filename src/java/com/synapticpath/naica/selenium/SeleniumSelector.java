/* Copyright (C) 2017 synapticpath.com - All Rights Reserved

 This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.synapticpath.naica.selenium;

import org.openqa.selenium.By;

/**
 * A Facade for Selenium {@link By} selector.  Use this to identify DOM elements.
 * 
 * @author developer@synapticpath.com 
 *
 */
public class SeleniumSelector {

    private String byCss;

    private String byXpath;

    private SeleniumSelector() {
    }

    public By toBySelector() {
        return byCss == null ? By.xpath(byXpath) : By.cssSelector(byCss);
    }

    public static SeleniumSelector byId(String id) {
        SeleniumSelector selector = new SeleniumSelector();
        selector.setSelector("#" + id);
        return selector;
    }

    public static SeleniumSelector byXpath(String xp) {
        SeleniumSelector selector = new SeleniumSelector();
        selector.setXpathSelector(xp);
        return selector;
    }

    public static SeleniumSelector byCss(String css) {
        SeleniumSelector selector = new SeleniumSelector();
        selector.setSelector(css);
        return selector;
    }

    public static SeleniumSelector byClassName(String className) {
        SeleniumSelector selector = new SeleniumSelector();
        selector.setSelector("." + className);
        return selector;
    }

    private void setSelector(String selector) {
        this.byCss = selector.replaceAll(":", "\\\\:");
    }

    private void setXpathSelector(String xp) {
        this.byXpath = xp;
    }

    @Override
    public String toString() {
        return toBySelector().toString();
    }
}
