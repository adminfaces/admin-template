/*
 * The MIT License
 *
 * Copyright 2018 rmpestano.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.adminfaces.template.config;

import java.io.Serializable;

/**
 * Holds control sidebar initial configuration
 * @author rmpestano
 */
public class ControlSidebarConfig implements Serializable {
    
    private final Boolean showOnMobile;
    private final Boolean fixedLayout;
    private final Boolean boxedLayout;
    private final Boolean expandOnHover;
    private final Boolean sidebarCollapsed;
    private final Boolean fixed;
    private final Boolean darkSkin;


    public ControlSidebarConfig(boolean showOnMobile, boolean fixedLayout, boolean boxedLayout, boolean expandOnHover, boolean sidebarCollapsed, boolean fixed, boolean darkSkin) {
         this.showOnMobile = showOnMobile;
         this.fixedLayout =fixedLayout;
         this.boxedLayout = boxedLayout;
         this.expandOnHover = expandOnHover;
         this.sidebarCollapsed = sidebarCollapsed;
         this.fixed = fixed;
         this.darkSkin = darkSkin;
    }

    public Boolean getShowOnMobile() {
        return showOnMobile;
    }

    public Boolean getFixedLayout() {
        return fixedLayout;
    }

    public Boolean getBoxedLayout() {
        return boxedLayout;
    }

    public Boolean getExpandOnHover() {
        return expandOnHover;
    }

    public Boolean getSidebarCollapsed() {
        return sidebarCollapsed;
    }

    public Boolean getFixed() {
        return fixed;
    }

    public Boolean getDarkSkin() {
        return darkSkin;
    }
    
}
