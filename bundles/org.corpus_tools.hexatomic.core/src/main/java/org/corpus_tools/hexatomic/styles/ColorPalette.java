/*-
 * #%L
 * org.corpus_tools.hexatomic.core
 * %%
 * Copyright (C) 2018 - 2020 Stephan Druskat,
 *                                     Thomas Krause
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package org.corpus_tools.hexatomic.styles;

import org.eclipse.swt.graphics.Color;

/**
 * Contains constants for a colorblind-friendly palette.
 * 
 * <p>
 * These are the same colors that are <a href=
 * "http://www.cookbook-r.com/Graphs/Colors_(ggplot2)/#a-colorblind-friendly-palette">commonly used
 * for R plots</a> and originally from <a href="https://jfly.uni-koeln.de/color/">Okabe and Ito
 * 2008</a>.
 * </p>
 * 
 * @author Thomas Krause {@literal thomas.krause@hu-berlin.de}
 */
public final class ColorPalette {
  
  private ColorPalette() {
    // Private (but empty) constructor to avoid instantiation of this utility class.
  }

  /**
   * A <strong style="color:#999999">gray</strong> tone matching to the other colors in this
   * palette.
   */
  public final static Color GRAY = new Color(153, 153, 153);

  /**
   * A matt <strong style="color:#E69F00">orange</strong> tone.
   * 
   * @see ColorPalette#VERMILLION
   */
  public final static Color ORANGE = new Color(230, 159, 0);

  /**
   * A lighter <strong style="color:#56B4E9">sky blue</strong> tone and one of the blue colors in
   * this palette.
   * 
   * @see ColorPalette#BLUE
   */
  public final static Color SKY_BLUE = new Color(86, 180, 233);

  /**
   * A <strong style="color:#009E73">bluish green</strong> tone and the only "green" color in this
   * palette.
   */
  public final static Color BLUISH_GREEN = new Color(0, 158, 115);
  
  /**
   * A bright <strong style="color:#F0E442">yellow</strong> tone.
   */
  public final static Color YELLOW = new Color(240, 228, 66);


  /**
   * A darker <strong style="color:#0072B2">blue</strong> tone.
   * 
   * @see ColorPalette#SKY_BLUE
   */
  public final static Color BLUE = new Color(0, 114, 178);


  /**
   * A darker <strong style="color:#D55E00">orange</strong> tone.
   * 
   * @see ColorPalette#ORANGE
   */
  public final static Color VERMILLION = new Color(213, 94, 0);

  /**
   * A <strong style="color:#CC79A7">purple</strong> tone with a red component.
   */
  public final static Color REDDISH_PURPLE = new Color(204, 121, 167);

}
